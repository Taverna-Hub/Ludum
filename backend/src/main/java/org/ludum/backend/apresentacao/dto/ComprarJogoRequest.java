package org.ludum.backend.apresentacao.dto;

import java.math.BigDecimal;

public class ComprarJogoRequest {

  private String jogoId;
  private String compradorId;
  private String desenvolvedoraId;
  private BigDecimal valor;

  public ComprarJogoRequest() {
  }

  public ComprarJogoRequest(String jogoId, String compradorId, String desenvolvedoraId, BigDecimal valor) {
    this.jogoId = jogoId;
    this.compradorId = compradorId;
    this.desenvolvedoraId = desenvolvedoraId;
    this.valor = valor;
  }

  public String getJogoId() {
    return jogoId;
  }

  public void setJogoId(String jogoId) {
    this.jogoId = jogoId;
  }

  public String getCompradorId() {
    return compradorId;
  }

  public void setCompradorId(String compradorId) {
    this.compradorId = compradorId;
  }

  public String getDesenvolvedoraId() {
    return desenvolvedoraId;
  }

  public void setDesenvolvedoraId(String desenvolvedoraId) {
    this.desenvolvedoraId = desenvolvedoraId;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }
}
