package org.ludum.backend.apresentacao.dto;

import java.math.BigDecimal;

public class CarteiraResponse {

  private String contaId;
  private BigDecimal saldoDisponivel;
  private BigDecimal saldoBloqueado;
  private boolean contaExternaValida;

  public CarteiraResponse() {
  }

  public CarteiraResponse(String contaId, BigDecimal saldoDisponivel,
      BigDecimal saldoBloqueado, boolean contaExternaValida) {
    this.contaId = contaId;
    this.saldoDisponivel = saldoDisponivel;
    this.saldoBloqueado = saldoBloqueado;
    this.contaExternaValida = contaExternaValida;
  }

  // Getters e Setters
  public String getContaId() {
    return contaId;
  }

  public void setContaId(String contaId) {
    this.contaId = contaId;
  }

  public BigDecimal getSaldoDisponivel() {
    return saldoDisponivel;
  }

  public void setSaldoDisponivel(BigDecimal saldoDisponivel) {
    this.saldoDisponivel = saldoDisponivel;
  }

  public BigDecimal getSaldoBloqueado() {
    return saldoBloqueado;
  }

  public void setSaldoBloqueado(BigDecimal saldoBloqueado) {
    this.saldoBloqueado = saldoBloqueado;
  }

  public boolean isContaExternaValida() {
    return contaExternaValida;
  }

  public void setContaExternaValida(boolean contaExternaValida) {
    this.contaExternaValida = contaExternaValida;
  }
}
