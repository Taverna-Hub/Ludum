package org.ludum.infraestrutura.financeiro;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.ProcessadorPayoutExterno;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.dto.DadosTransferencia;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AsaasProcessadorPayout extends ProcessadorPayoutExterno {
    private final String apiKey;
    private final String apiUrl;
    private final OkHttpClient httpClient;
    private final Gson gson;

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public AsaasProcessadorPayout(String apiKey,
                                  TransacaoRepository transacaoRepository,
                                  CarteiraRepository carteiraRepository) {
        super(transacaoRepository, carteiraRepository);
        this.apiKey = apiKey;
        this.apiUrl = "https://sandbox.asaas.com/api/v3";
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    @Override
    protected void beforeExecutarPayout(org.ludum.dominio.identidade.conta.entities.ContaId contaId, BigDecimal valor) {
        System.out.println(String.format(
            "[Asaas Payout] Iniciando transferência - ContaId: %s, Valor: R$ %s",
            contaId.getValue(), valor
        ));
    }

    @Override
    protected void validarDadosPayout(Carteira carteira, BigDecimal valor) {
        if (!carteira.isContaExternaValida()) {
            throw new IllegalStateException("Conta externa (PIX) não configurada ou não validada");
        }

        String chavePix = carteira.getContaExterna();
        if (chavePix == null || chavePix.isBlank()) {
            throw new IllegalStateException("Chave PIX não cadastrada na carteira");
        }

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        BigDecimal valorMinimo = new BigDecimal("0.01");
        if (valor.compareTo(valorMinimo) < 0) {
            throw new IllegalArgumentException("Asaas: Valor mínimo para transferência é R$ " + valorMinimo);
        }

        if (carteira.getSaldo().getDisponivel().compareTo(valor) < 0) {
            throw new IllegalStateException("Saldo insuficiente para saque");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Asaas API Key não configurada");
        }
    }

    @Override
    protected DadosTransferencia prepararTransferencia(Carteira carteira, BigDecimal valor, String descricao) {
        String chavePix = carteira.getContaExterna();
        String descricaoFinal = descricao != null ? descricao : "Saque de vendas - Ludum";
        
        return new DadosTransferencia(chavePix, valor, descricaoFinal);
    }

    @Override
    protected String executarTransferenciaNoGateway(DadosTransferencia dados) throws Exception {
        String sandboxPix = "d58478b0-ba7f-4da3-a662-3438cd7511d2";

        Map<String, Object> transferData = new HashMap<>();
        transferData.put("value", dados.getValor());
        transferData.put("pixAddressKey", sandboxPix);
        transferData.put("description", dados.getDescricao());

        String json = gson.toJson(transferData);

        System.out.println("Executando transferência PIX no Asaas...");
        System.out.println("Chave PIX: " + dados.getChavePix());
        System.out.println("Valor: R$ " + dados.getValor());

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

    @Override
    protected void afterExecutarPayout(String transferId, boolean sucesso) {
        System.out.println(String.format(
            "[Asaas Payout] Transferência %s - Transfer ID: %s",
            sucesso ? "SUCESSO" : "FALHA",
            transferId != null ? transferId : "N/A"
        ));
    }
}
