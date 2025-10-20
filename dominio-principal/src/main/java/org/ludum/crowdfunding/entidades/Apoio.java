package org.ludum.crowdfunding.entidades;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import org.ludum.financeiro.transacao.entidades.TransacaoId;
import org.ludum.identidade.conta.entidades.ContaId;

public class Apoio {
    private final String id;
    private final CampanhaId campanhaId;
    private final ContaId apoiadorId;
    private final TransacaoId transacaoId;
    private final BigDecimal valor;
    private final LocalDateTime data;
    private boolean isCancelado;

    public Apoio(CampanhaId campanhaId, ContaId apoiadorId, TransacaoId transacaoId, BigDecimal valor) {
        this.id = UUID.randomUUID().toString();
        this.campanhaId = Objects.requireNonNull(campanhaId);
        this.apoiadorId = Objects.requireNonNull(apoiadorId);
        this.transacaoId = Objects.requireNonNull(transacaoId);
        this.valor = Objects.requireNonNull(valor);
        this.data = LocalDateTime.now();
        this.isCancelado = false;
    }

    public void cancelar() {
        if (this.isCancelado) {
            throw new IllegalStateException("Esse apoio já foi cancelado.");
        }
        this.isCancelado = true;
    }

    public String getId() {
        return id;
    
    }
    public CampanhaId getCampanhaId() {
        return campanhaId;
    }

    public ContaId getApoiadorId() {
        return apoiadorId;
    }

    public TransacaoId getTransacaoId() {
        return transacaoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDateTime getData() {
        return data;
    }

    public boolean isCancelado() {
        return isCancelado;
    }
}
