package org.ludum.dominio.financeiro.carteira.entidades;

import java.math.BigDecimal;

import org.ludum.dominio.identidade.conta.entities.ContaId;


public class Carteira {
  private ContaId id;
  private Saldo saldo;
  private boolean contaExternaValida;

  public Carteira(ContaId id, Saldo saldo) {
    this.id = id;
    this.saldo = saldo;
    this.contaExternaValida = false;
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

  public boolean isContaExternaValida() {
    return contaExternaValida;
  }

  public void setContaExternaValida(boolean contaExternaValida) {
    this.contaExternaValida = contaExternaValida;
  }

  public void liberarSaldoBloqueado() {
    BigDecimal valorBloqueado = this.saldo.getBloqueado();
    
    if (valorBloqueado.compareTo(BigDecimal.ZERO) > 0) {
        this.saldo.setDisponivel( this.saldo.getDisponivel().add(valorBloqueado) );
        this.saldo.setBloqueado(BigDecimal.ZERO); 
    }
  }
}