package org.ludum.bloqueio.entidades;

import org.ludum.conta.entidades.ContaId;

import java.util.Objects;

public class Bloqueio {
    private BloqueioId id;
    private ContaId bloqueadorId;
    private ContaId bloqueadoId;

    public Bloqueio(BloqueioId id, ContaId bloqueadorId, ContaId bloqueadoId) {
        this.id = Objects.requireNonNull(id);
        this.bloqueadorId = Objects.requireNonNull(bloqueadorId);
        this.bloqueadoId = Objects.requireNonNull(bloqueadoId);
    }

    public BloqueioId getId() {
        return id;
    }

    public void setId(BloqueioId id) {
        this.id = id;
    }

    public ContaId getBloqueadorId() {
        return bloqueadorId;
    }

    public void setBloqueadorId(ContaId bloqueadorId) {
        this.bloqueadorId = bloqueadorId;
    }

    public ContaId getBloqueadoId() {
        return bloqueadoId;
    }

    public void setBloqueadoId(ContaId bloqueadoId) {
        this.bloqueadoId = bloqueadoId;
    }
}
