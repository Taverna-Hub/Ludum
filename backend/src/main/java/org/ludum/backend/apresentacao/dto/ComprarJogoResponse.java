package org.ludum.backend.apresentacao.dto;

import java.math.BigDecimal;

public class ComprarJogoResponse {

  private boolean sucesso;
  private String mensagem;
  private String transacaoId;
  private BigDecimal saldoAtual;
  private BigDecimal valorFaltante;

  public ComprarJogoResponse() {
  }

  public ComprarJogoResponse(boolean sucesso, String mensagem, String transacaoId, 
                            BigDecimal saldoAtual, BigDecimal valorFaltante) {
    this.sucesso = sucesso;
    this.mensagem = mensagem;
    this.transacaoId = transacaoId;
    this.saldoAtual = saldoAtual;
    this.valorFaltante = valorFaltante;
  }

  public static ComprarJogoResponse sucesso(String transacaoId) {
    return new ComprarJogoResponse(true, "Compra realizada com sucesso!", transacaoId, null, null);
  }

  public static ComprarJogoResponse falha(String mensagem) {
    return new ComprarJogoResponse(false, mensagem, null, null, null);
  }

  public static ComprarJogoResponse saldoInsuficiente(BigDecimal saldoAtual, BigDecimal valorFaltante) {
    return new ComprarJogoResponse(false, "Saldo insuficiente", null, saldoAtual, valorFaltante);
  }

  public boolean isSucesso() {
    return sucesso;
  }

  public void setSucesso(boolean sucesso) {
    this.sucesso = sucesso;
  }

  public String getMensagem() {
    return mensagem;
  }

  public void setMensagem(String mensagem) {
    this.mensagem = mensagem;
  }

  public String getTransacaoId() {
    return transacaoId;
  }

  public void setTransacaoId(String transacaoId) {
    this.transacaoId = transacaoId;
  }

  public BigDecimal getSaldoAtual() {
    return saldoAtual;
  }

  public void setSaldoAtual(BigDecimal saldoAtual) {
    this.saldoAtual = saldoAtual;
  }

  public BigDecimal getValorFaltante() {
    return valorFaltante;
  }

  public void setValorFaltante(BigDecimal valorFaltante) {
    this.valorFaltante = valorFaltante;
  }
}
