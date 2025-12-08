package org.ludum.infraestrutura.financeiro;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PagarMeProcessadorPagamento extends ProcessadorPagamentoExterno {

  private final String apiKey;
  private final String apiUrl;
  private final boolean usarModoTeste;
  private final OkHttpClient httpClient;
  private final Gson gson;

  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  public PagarMeProcessadorPagamento(String apiKey) {
    this.apiKey = apiKey;
    this.usarModoTeste = apiKey != null && apiKey.startsWith("sk_test_");
    this.apiUrl = "https://api.pagar.me/core/v5";
    this.httpClient = new OkHttpClient();
    this.gson = new Gson();
  }

  @Override
  protected void validarSolicitacao(ContaId contaId, BigDecimal valor, String moeda) {
    if (contaId == null || contaId.getValue() == null) {
      throw new IllegalArgumentException("ContaId não pode ser nula");
    }

    if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Valor deve ser maior que zero");
    }

    // Pagar.me: valor mínimo R$ 1,00 (100 centavos)
    BigDecimal valorMinimo = new BigDecimal("1.00");
    if (valor.compareTo(valorMinimo) < 0) {
      throw new IllegalArgumentException(
          "Pagar.me requer valor mínimo de R$ " + valorMinimo);
    }

    if (!"BRL".equalsIgnoreCase(moeda)) {
      throw new IllegalArgumentException("Pagar.me suporta apenas BRL (Real brasileiro)");
    }

    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalStateException("Pagar.me API Key não configurada");
    }
  }

  @Override
  protected Object prepararDadosGateway(ContaId contaId, BigDecimal valor, String moeda, String descricao) {
    long valorEmCentavos = valor.multiply(new BigDecimal("100")).longValue();

    Map<String, Object> orderData = new HashMap<>();
    orderData.put("amount", valorEmCentavos);
    orderData.put("currency", moeda);

    Map<String, Object> item = new HashMap<>();
    item.put("amount", valorEmCentavos);
    item.put("description", descricao != null ? descricao : "Adicionar saldo Ludum");
    item.put("quantity", 1);
    orderData.put("items", new Map[] { item });

    Map<String, Object> payments = new HashMap<>();
    payments.put("credit_card", Map.of("enabled", true));
    payments.put("pix", Map.of("enabled", true, "expires_in", 3600)); // PIX expira em 1h
    payments.put("boleto", Map.of("enabled", true, "expires_in", 86400)); // Boleto expira em 24h
    orderData.put("payments", new Map[] { payments });

    Map<String, String> metadata = new HashMap<>();
    metadata.put("conta_id", contaId.getValue());
    metadata.put("plataforma", "Ludum");
    metadata.put("tipo", "adicionar_saldo");
    metadata.put("ambiente", usarModoTeste ? "teste" : "producao");
    orderData.put("metadata", metadata);

    return orderData;
  }

  @Override
  protected String executarPagamentoNoGateway(Object dadosGateway, BigDecimal valor) throws Exception {
    @SuppressWarnings("unchecked")
    Map<String, Object> orderData = (Map<String, Object>) dadosGateway;
    String json = gson.toJson(orderData);

    System.out.println("Criando Order no Pagar.me...");
    System.out.println("   Valor: " + orderData.get("amount") + " centavos");
    System.out.println("   Moeda: " + orderData.get("currency"));
    System.out.println("   Modo: " + (usarModoTeste ? "TESTE" : "PRODUÇÃO"));

    Request request = new Request.Builder()
        .url(apiUrl + "/orders")
        .addHeader("Authorization", "Bearer " + apiKey)
        .addHeader("Content-Type", "application/json")
        .post(RequestBody.create(json, JSON))
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = response.body() != null ? response.body().string() : "";

      if (!response.isSuccessful()) {
        System.err.println("Erro ao criar order no Pagar.me: " + response.code());
        System.err.println("Response: " + responseBody);
        throw new IOException("Erro na API Pagar.me: " + response.code() + " - " + responseBody);
      }

      JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
      String orderId = jsonResponse.get("id").getAsString();
      String status = jsonResponse.get("status").getAsString();

      System.out.println("Order criada: " + orderId);
      System.out.println("   Status: " + status);

      if ("paid".equals(status)) {
        return orderId;
      } else if ("pending".equals(status)) {
        System.out.println("Order criada mas aguardando pagamento");
        return orderId;
      } else {
        throw new RuntimeException("Status inesperado: " + status);
      }
    }
  }

  public String executarPayout(String recipientId, BigDecimal valor, String descricao) throws IOException {
    if (recipientId == null || recipientId.isBlank()) {
      throw new IllegalArgumentException("RecipientId não pode ser nulo");
    }

    if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Valor deve ser maior que zero");
    }

    long valorEmCentavos = valor.multiply(new BigDecimal("100")).longValue();

    Map<String, Object> transferData = new HashMap<>();
    transferData.put("amount", valorEmCentavos);
    transferData.put("recipient_id", recipientId);

    if (descricao != null) {
      Map<String, String> metadata = new HashMap<>();
      metadata.put("description", descricao);
      metadata.put("tipo", "saque");
      transferData.put("metadata", metadata);
    }

    String json = gson.toJson(transferData);

    System.out.println("Executando payout no Pagar.me...");
    System.out.println("   Recipient: " + recipientId);
    System.out.println("   Valor: " + valorEmCentavos + " centavos");

    Request request = new Request.Builder()
        .url(apiUrl + "/transfers")
        .addHeader("Authorization", "Bearer " + apiKey)
        .addHeader("Content-Type", "application/json")
        .post(RequestBody.create(json, JSON))
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = response.body() != null ? response.body().string() : "";

      if (!response.isSuccessful()) {
        System.err.println("Erro ao executar payout: " + response.code());
        System.err.println("Response: " + responseBody);
        throw new IOException("Erro na API Pagar.me: " + response.code() + " - " + responseBody);
      }

      JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
      String transferId = jsonResponse.get("id").getAsString();
      String status = jsonResponse.get("status").getAsString();

      System.out.println("Payout criado: " + transferId);
      System.out.println("   Status: " + status);

      return transferId;
    }
  }

  public String consultarStatusPagamento(String orderId) throws IOException {
    Request request = new Request.Builder()
        .url(apiUrl + "/orders/" + orderId)
        .addHeader("Authorization", "Bearer " + apiKey)
        .get()
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = response.body() != null ? response.body().string() : "";

      if (!response.isSuccessful()) {
        throw new IOException("Erro ao consultar order: " + response.code());
      }

      JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
      return jsonResponse.get("status").getAsString();
    }
  }

  public boolean processarWebhookPagamento(String orderId, String status) {
    System.out.println("Webhook recebido - Order: " + orderId + ", Status: " + status);

    if ("paid".equals(status)) {
      System.out.println("Pagamento confirmado! Creditando saldo na carteira...");
      return true;
    }

    return false;
  }

  public void validarCredenciais() {
    try {
      Request request = new Request.Builder()
          .url(apiUrl + "/balance")
          .addHeader("Authorization", "Bearer " + apiKey)
          .get()
          .build();

      try (Response response = httpClient.newCall(request).execute()) {
        if (response.isSuccessful()) {
          System.out.println("✓ Credenciais Pagar.me validadas com sucesso");
        } else {
          System.err.println("✗ Erro ao validar credenciais Pagar.me: " + response.code());
        }
      }
    } catch (IOException e) {
      System.err.println("✗ Erro ao validar credenciais Pagar.me: " + e.getMessage());
    }
  }
}
