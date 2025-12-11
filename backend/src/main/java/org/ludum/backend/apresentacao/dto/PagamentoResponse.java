package org.ludum.backend.apresentacao.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoResponse {

  private boolean sucesso;
  private String mensagem;
  private LocalDateTime dataProcessamento;
  private BigDecimal valorProcessado;

  public PagamentoResponse() {
  }

  public PagamentoResponse(boolean sucesso, String mensagem,
      LocalDateTime dataProcessamento, BigDecimal valorProcessado) {
    this.sucesso = sucesso;
    this.mensagem = mensagem;
    this.dataProcessamento = dataProcessamento;
    this.valorProcessado = valorProcessado;
  }

  public static PagamentoResponse sucesso(BigDecimal valor) {
    return new PagamentoResponse(true, "Pagamento processado com sucesso",
        LocalDateTime.now(), valor);
  }

  public static PagamentoResponse falha(String mensagemErro) {
    return new PagamentoResponse(false, mensagemErro,
        LocalDateTime.now(), null);
  }

  // Getters e Setters
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
