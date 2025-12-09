package org.ludum.infraestrutura.financeiro;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AsaasProcessadorPagamento extends ProcessadorPagamentoExterno {

  private final String apiKey;
  private final String apiUrl;
  private final OkHttpClient httpClient;
  private final Gson gson;
  private final CarteiraRepository carteiraRepository;

  // ThreadLocal para armazenar o customerId durante a transação
  private final ThreadLocal<String> currentCustomerId = new ThreadLocal<>();

  private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

  public AsaasProcessadorPagamento(String apiKey, CarteiraRepository carteiraRepository) {
    this.apiKey = apiKey;
    this.apiUrl = "https://sandbox.asaas.com/api/v3";
    this.httpClient = new OkHttpClient();
    this.gson = new Gson();
    this.carteiraRepository = carteiraRepository;
  }

  @Override
  protected void validarSolicitacao(ContaId contaId, BigDecimal valor, String moeda) {
    if (contaId == null || contaId.getValue() == null) {
      throw new IllegalArgumentException("ContaId não pode ser nula");
    }

    if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Valor deve ser maior que zero");
    }

    BigDecimal valorMinimo = new BigDecimal("5.00");
    if (valor.compareTo(valorMinimo) < 0) {
      throw new IllegalArgumentException(
          "Asaas requer valor mínimo de R$ " + valorMinimo);
    }

    if (!"BRL".equalsIgnoreCase(moeda)) {
      throw new IllegalArgumentException("Asaas suporta apenas BRL (Real brasileiro)");
    }

    if (apiKey == null || apiKey.isBlank()) {
      throw new IllegalStateException("Asaas API Key não configurada");
    }
  }

  @Override
  protected Object prepararDadosGateway(ContaId contaId, BigDecimal valor, String moeda, String descricao) {
    Map<String, Object> paymentData = new HashMap<>();

    // Usar o customerId definido ou o contaId como fallback
    String customerId = currentCustomerId.get();
    if (customerId == null || customerId.isBlank()) {
      customerId = contaId.getValue();
    }
    paymentData.put("customer", customerId);

    paymentData.put("value", valor);
    paymentData.put("description", descricao != null ? descricao : "Adicionar saldo Ludum");
    paymentData.put("dueDate", java.time.LocalDate.now().plusDays(7).toString());

    paymentData.put("billingType", "UNDEFINED");

    Map<String, Object> pixConfig = new HashMap<>();
    pixConfig.put("expirationDate", java.time.LocalDate.now().plusDays(1).toString());
    paymentData.put("pix", pixConfig);

    paymentData.put("externalReference", contaId.getValue());

    return paymentData;
  }

  @Override
  protected String executarPagamentoNoGateway(Object dadosGateway, BigDecimal valor) throws Exception {
    @SuppressWarnings("unchecked")
    Map<String, Object> paymentData = (Map<String, Object>) dadosGateway;
    String json = gson.toJson(paymentData);

    System.out.println("Criando cobrança no Asaas...");
    System.out.println("Valor: R$ " + valor);

    Request request = new Request.Builder()
        .url(apiUrl + "/payments")
        .addHeader("access_token", apiKey)
        .addHeader("Content-Type", "application/json")
        .post(RequestBody.create(json, JSON))
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = response.body() != null ? response.body().string() : "";

      if (!response.isSuccessful()) {
        System.err.println("Erro ao criar cobrança no Asaas: " + response.code());
        System.err.println("Response: " + responseBody);
        throw new IOException("Erro na API Asaas: " + response.code() + " - " + responseBody);
      }

      JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
      String paymentId = jsonResponse.get("id").getAsString();
      String status = jsonResponse.get("status").getAsString();

      System.out.println("Cobrança criada: " + paymentId);
      System.out.println("Status: " + status);

      if ("RECEIVED".equals(status) || "CONFIRMED".equals(status)) {
        return paymentId;
      } else if ("PENDING".equals(status)) {
        System.out.println("Cobrança criada, aguardando pagamento");
        return paymentId;
      } else {
        throw new RuntimeException("Status inesperado: " + status);
      }
    }
  }

  @Override
  public String executarPayout(ContaId contaId, BigDecimal valor, String descricao) throws Exception {
    if (contaId == null || contaId.getValue() == null) {
      throw new IllegalArgumentException("ContaId não pode ser nula");
    }

    Carteira carteira = carteiraRepository.obterPorContaId(contaId);
    if (carteira == null) {
      throw new IllegalArgumentException("Carteira não encontrada para ContaId: " + contaId.getValue());
    }

    String chavePix = carteira.getContaExterna();
    if (chavePix == null || chavePix.isBlank()) {
      throw new IllegalArgumentException("Chave PIX não cadastrada na carteira");
    }

    if (!carteira.isContaExternaValida()) {
      throw new IllegalArgumentException("Conta externa (PIX) não validada");
    }

    if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Valor deve ser maior que zero");
    }

    BigDecimal valorMinimo = new BigDecimal("0.01");
    if (valor.compareTo(valorMinimo) < 0) {
      throw new IllegalArgumentException("Valor mínimo para transferência: R$ " + valorMinimo);
    }

    Map<String, Object> transferData = new HashMap<>();
    transferData.put("value", valor);
    transferData.put("pixAddressKey", chavePix);
    transferData.put("description", descricao != null ? descricao : "Saque de vendas");

    String json = gson.toJson(transferData);

    System.out.println("Executando transferência PIX no Asaas...");
    System.out.println("ContaId: " + contaId.getValue());
    System.out.println("Chave PIX: " + chavePix);
    System.out.println("Valor: R$ " + valor);

    Request request = new Request.Builder()
        .url(apiUrl + "/transfers")
        .addHeader("access_token", apiKey)
        .addHeader("Content-Type", "application/json")
        .post(RequestBody.create(json, JSON))
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = response.body() != null ? response.body().string() : "";

      if (!response.isSuccessful()) {
        System.err.println("Erro ao executar transferência PIX: " + response.code());
        System.err.println("Response: " + responseBody);
        throw new IOException("Erro na API Asaas: " + response.code() + " - " + responseBody);
      }

      JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
      String transferId = jsonResponse.get("id").getAsString();
      String status = jsonResponse.has("status") ? jsonResponse.get("status").getAsString() : "PENDING";

      System.out.println("Transferência PIX criada: " + transferId);
      System.out.println("Status: " + status);

      return transferId;
    }
  }

  public String criarCliente(String nome, String cpfCnpj, String email, String telefone) throws IOException {
    Map<String, Object> customerData = new HashMap<>();
    customerData.put("name", nome);
    customerData.put("cpfCnpj", cpfCnpj);
    customerData.put("email", email);
    if (telefone != null && !telefone.isBlank()) {
      customerData.put("mobilePhone", telefone);
    }

    String json = gson.toJson(customerData);

    System.out.println("Criando cliente no Asaas...");
    System.out.println("Nome: " + nome);
    System.out.println("CPF/CNPJ: " + cpfCnpj);

    Request request = new Request.Builder()
        .url(apiUrl + "/customers")
        .addHeader("access_token", apiKey)
        .addHeader("Content-Type", "application/json")
        .post(RequestBody.create(json, JSON))
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = response.body() != null ? response.body().string() : "";

      if (!response.isSuccessful()) {
        System.err.println("Erro ao criar cliente: " + response.code());
        System.err.println("Response: " + responseBody);
        throw new IOException("Erro na API Asaas: " + response.code() + " - " + responseBody);
      }

      JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
      String customerId = jsonResponse.get("id").getAsString();

      System.out.println("Cliente criado: " + customerId);
      return customerId;
    }
  }

  public String consultarStatusPagamento(String paymentId) throws IOException {
    Request request = new Request.Builder()
        .url(apiUrl + "/payments/" + paymentId)
        .addHeader("access_token", apiKey)
        .get()
        .build();

    try (Response response = httpClient.newCall(request).execute()) {
      String responseBody = response.body() != null ? response.body().string() : "";

      if (!response.isSuccessful()) {
        throw new IOException("Erro ao consultar payment: " + response.code());
      }

      JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
      return jsonResponse.get("status").getAsString();
    }
  }

  public boolean processarWebhookPagamento(String paymentId, String status) {
    System.out.println("Webhook recebido - Payment: " + paymentId + ", Status: " + status);

    if ("RECEIVED".equals(status) || "CONFIRMED".equals(status)) {
      System.out.println("Pagamento confirmado! Creditando saldo na carteira...");
      return true;
    }

    return false;
  }

  public void validarCredenciais() {
    try {
      Request request = new Request.Builder()
          .url(apiUrl + "/myAccount")
          .addHeader("access_token", apiKey)
          .get()
          .build();

      try (Response response = httpClient.newCall(request).execute()) {
        if (response.isSuccessful()) {
          System.out.println("Credenciais Asaas validadas com sucesso");

          String responseBody = response.body() != null ? response.body().string() : "";
          JsonObject account = gson.fromJson(responseBody, JsonObject.class);
          if (account.has("name")) {
            System.out.println("  Conta: " + account.get("name").getAsString());
          }
        } else {
          System.err.println("✗ Erro ao validar credenciais Asaas: " + response.code());
        }
      }
    } catch (IOException e) {
      System.err.println("✗ Erro ao validar credenciais Asaas: " + e.getMessage());
    }
  }

  public void setCustomerId(String customerId) {
    currentCustomerId.set(customerId);
  }

  public void clearCustomerId() {
    currentCustomerId.remove();
  }
}
