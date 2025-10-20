package org.ludum.financeiro.transacao.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Recibo {
  private ReciboId id;
  private LocalDateTime data;
  private BigDecimal valor;

  public Recibo(ReciboId id, LocalDateTime data, BigDecimal valor) {
    this.id = id;
    this.data = data;
    this.valor = valor;
  }

  public ReciboId getId() {
    return id;
  }

  public void setId(ReciboId id) {
    this.id = id;
  }

  public LocalDateTime getData() {
    return data;
  }

  public void setData(LocalDateTime data) {
    this.data = data;
  }

  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }
}
