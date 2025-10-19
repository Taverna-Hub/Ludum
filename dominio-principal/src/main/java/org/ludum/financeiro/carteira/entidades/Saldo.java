package org.ludum.financeiro.carteira.entidades;

import java.math.BigDecimal;

public class Saldo {
    private BigDecimal disponivel;
    private BigDecimal bloqueado;

    public Saldo() {
        this.disponivel = BigDecimal.ZERO;
        this.bloqueado = BigDecimal.ZERO;
    }

    public BigDecimal getDisponivel() {
        return disponivel;
    }

    public BigDecimal getBloqueado() {
        return bloqueado;
    }

    public void addDisponivel(BigDecimal valor) {
        if (valor == null) return;
        this.disponivel = this.disponivel.add(valor);
    }

    public void addBloqueado(BigDecimal valor) {
        if (valor == null) return;
        this.bloqueado = this.bloqueado.add(valor);
    }

    public void setDisponivel(BigDecimal valor) {
        this.disponivel = valor;
    }

    public void setBloqueado(BigDecimal valor) {
        this.bloqueado = valor;
    }

    public void subtrairDisponivel(BigDecimal valor) {
        this.disponivel = this.disponivel.subtract(valor);
    }

    public void subtrairBloqueado(BigDecimal valor) {
        this.bloqueado = this.bloqueado.subtract(valor);
    }
}