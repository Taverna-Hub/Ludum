package org.ludum.backend.apresentacao.dto;

import java.math.BigDecimal;

public class CarteiraResponse {

  private String id;
  private BigDecimal disponivel;
  private BigDecimal bloqueado;
  private boolean contaExternaValida;
  private String contaExterna;


  public CarteiraResponse() {
  }

  public CarteiraResponse(String id, BigDecimal disponivel,
      BigDecimal bloqueado, boolean contaExternaValida, String contaExterna) {
    this.id = id;
    this.disponivel = disponivel;
    this.bloqueado = bloqueado;
    this.contaExternaValida = contaExternaValida;
    this.contaExterna = contaExterna;
  }

  // Getters e Setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public BigDecimal getDisponivel() {
    return disponivel;
  }

  public void setDisponivel(BigDecimal disponivel) {
    this.disponivel = disponivel;
  }

  public BigDecimal getBloqueado() {
    return bloqueado;
  }

  public void setBloqueado(BigDecimal bloqueado) {
    this.bloqueado = bloqueado;
  }

  public boolean isContaExternaValida() {
    return contaExternaValida;
  }

  public void setContaExternaValida(boolean contaExternaValida) {
    this.contaExternaValida = contaExternaValida;
  }

  public String getContaExterna() {
    return contaExterna;
  }

  public void setContaExterna(String contaExterna) {
    this.contaExterna = contaExterna;
  }
}
