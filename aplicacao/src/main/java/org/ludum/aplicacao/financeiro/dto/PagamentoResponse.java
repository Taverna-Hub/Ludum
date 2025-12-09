package org.ludum.aplicacao.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoResponse {

  private boolean sucesso;
  private String transacaoId;
  private String idGateway;
  private String mensagemErro;
  private LocalDateTime dataProcessamento;
  private BigDecimal valorProcessado;

  public PagamentoResponse() {
  }

  public PagamentoResponse(boolean sucesso, String transacaoId, String idGateway,
      String mensagemErro, LocalDateTime dataProcessamento,
      BigDecimal valorProcessado) {
    this.sucesso = sucesso;
    this.transacaoId = transacaoId;
    this.idGateway = idGateway;
    this.mensagemErro = mensagemErro;
    this.dataProcessamento = dataProcessamento;
    this.valorProcessado = valorProcessado;
  }

  public static PagamentoResponse sucesso(String transacaoId, String idGateway, BigDecimal valor) {
    return new PagamentoResponse(true, transacaoId, idGateway, null,
        LocalDateTime.now(), valor);
  }

  public static PagamentoResponse falha(String transacaoId, String mensagemErro) {
    return new PagamentoResponse(false, transacaoId, null, mensagemErro,
        LocalDateTime.now(), null);
  }

  // Getters e Setters
  public boolean isSucesso() {
    return sucesso;
  }

  public void setSucesso(boolean sucesso) {
    this.sucesso = sucesso;
  }

  public String getTransacaoId() {
    return transacaoId;
  }

  public void setTransacaoId(String transacaoId) {
    this.transacaoId = transacaoId;
  }

  public String getIdGateway() {
    return idGateway;
  }

  public void setIdGateway(String idGateway) {
    this.idGateway = idGateway;
  }

  public String getMensagemErro() {
    return mensagemErro;
  }

  public void setMensagemErro(String mensagemErro) {
    this.mensagemErro = mensagemErro;
  }

  public LocalDateTime getDataProcessamento() {
    return dataProcessamento;
  }

  public void setDataProcessamento(LocalDateTime dataProcessamento) {
    this.dataProcessamento = dataProcessamento;
  }

  public BigDecimal getValorProcessado() {
    return valorProcessado;
  }

  public void setValorProcessado(BigDecimal valorProcessado) {
    this.valorProcessado = valorProcessado;
  }
}
