package org.ludum.financeiro.carteira.entidades;

import java.math.BigDecimal;

import org.ludum.identidade.conta.entities.ContaId;


public class Carteira {
  private ContaId id;
  private Saldo saldo;
  private boolean contaExternaValida;

  public Carteira(ContaId id, Saldo saldo) {
    this.id = id;
    this.saldo = saldo;
  }

  public ContaId getId() {
    return id;
  }

  public void setId(ContaId id) {
    this.id = id;
  }

  public Saldo getSaldo() {
    return saldo;
  }

  public void setSaldo(Saldo saldo) {
    this.saldo = saldo;
  }

  public boolean getContaExternaValida() {
    return contaExternaValida;
  }

  public void setContaExternaValida(boolean contaExternaValida) {
    this.contaExternaValida = contaExternaValida;
  }

  public void liberarSaldoBloqueado() {
    this.saldo.setDisponivel( this.saldo.getDisponivel().add(this.saldo.getBloqueado()));
    this.saldo.setBloqueado(BigDecimal.valueOf(0));
  }
}
