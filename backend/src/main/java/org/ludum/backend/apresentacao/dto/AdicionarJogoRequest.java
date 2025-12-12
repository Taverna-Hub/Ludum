package org.ludum.backend.apresentacao.dto;

public class AdicionarJogoRequest {

    private String jogoId;
    private String contaId;
    private String modeloDeAcesso;
    private String transacaoId;

    public AdicionarJogoRequest() {
    }

    public AdicionarJogoRequest(String jogoId, String contaId, String modeloDeAcesso, String transacaoId) {
        this.jogoId = jogoId;
        this.contaId = contaId;
        this.modeloDeAcesso = modeloDeAcesso;
        this.transacaoId = transacaoId;
    }

    public String getJogoId() {
        return jogoId;
    }

    public void setJogoId(String jogoId) {
        this.jogoId = jogoId;
    }

    public String getContaId() {
        return contaId;
    }

    public void setContaId(String contaId) {
        this.contaId = contaId;
    }

    public String getModeloDeAcesso() {
        return modeloDeAcesso;
    }

    public void setModeloDeAcesso(String modeloDeAcesso) {
        this.modeloDeAcesso = modeloDeAcesso;
    }

    public String getTransacaoId() {
        return transacaoId;
    }

    public void setTransacaoId(String transacaoId) {
        this.transacaoId = transacaoId;
    }
}
