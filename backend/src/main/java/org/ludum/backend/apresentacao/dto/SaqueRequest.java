package org.ludum.backend.apresentacao.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Date;

public class SaqueRequest {

  @NotNull(message = "Valor é obrigatório")
  @Positive(message = "Valor deve ser maior que zero")
  private BigDecimal valor;

  private Date dataVenda;

  private boolean crowdfunding = false;

  private boolean metaAtingida = false;

  public SaqueRequest() {
  }

  public SaqueRequest(BigDecimal valor, Date dataVenda, boolean crowdfunding, boolean metaAtingida) {
    this.valor = valor;
    this.dataVenda = dataVenda;
    this.crowdfunding = crowdfunding;
    this.metaAtingida = metaAtingida;
  }

  // Getters e Setters
  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }

  public Date getDataVenda() {
    return dataVenda;
  }

  public void setDataVenda(Date dataVenda) {
    this.dataVenda = dataVenda;
  }

  public boolean isCrowdfunding() {
    return crowdfunding;
  }

  public void setCrowdfunding(boolean crowdfunding) {
    this.crowdfunding = crowdfunding;
  }

  public boolean isMetaAtingida() {
    return metaAtingida;
  }

  public void setMetaAtingida(boolean metaAtingida) {
    this.metaAtingida = metaAtingida;
  }
}
