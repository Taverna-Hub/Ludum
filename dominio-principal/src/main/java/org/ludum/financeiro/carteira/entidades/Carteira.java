package org.ludum.financeiro.carteira.entidades;

public class Carteira {
  private ContaId id;
  private Saldo saldo;

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
}
