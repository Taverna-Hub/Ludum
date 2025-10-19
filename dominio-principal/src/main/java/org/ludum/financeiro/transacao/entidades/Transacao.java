package org.ludum.financeiro.transacao.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.ludum.financeiro.carteira.entidades.ContaId;
import org.ludum.financeiro.transacao.enums.StatusTransacao;
import org.ludum.financeiro.transacao.enums.TipoTransacao;

public class Transacao {
  private TransacaoId id;
  private ContaId contaOrigem;
  private ContaId contaDestino;
  private TipoTransacao tipo;
  private StatusTransacao status;
  private LocalDateTime data;
  private BigDecimal valor;

  public Transacao(TransacaoId transacaoId, ContaId contaOrigem, ContaId contaDestino, TipoTransacao tipo, StatusTransacao status, LocalDateTime data, BigDecimal valor) {
    this.id = transacaoId;
    this.contaOrigem = contaOrigem;
    this.contaDestino = contaDestino;
    this.tipo = tipo;
    this.status = status;
    this.data = data;
    this.valor = valor;
  }

  public TransacaoId getTransacaoId() {
    return id;
  }

  public void setTransacaoId(TransacaoId transacaoId) {
    this.id = transacaoId;
  }

  public ContaId getContaOrigem() {
    return contaOrigem;
  }

  public void setContaOrigem(ContaId contaOrigem) {
    this.contaOrigem = contaOrigem;
  }

  public ContaId getContaDestino() {
    return contaDestino;
  }

  public void setContaDestino(ContaId contaDestino) {
    this.contaDestino = contaDestino;
  }

  public TipoTransacao getTipo() {
    return tipo;
  }

  public void setTipo(TipoTransacao tipo) {
    this.tipo = tipo;
  }

  public StatusTransacao getStatus() {
    return status;
  }

  public void setStatus(StatusTransacao status) {
    this.status = status;
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
