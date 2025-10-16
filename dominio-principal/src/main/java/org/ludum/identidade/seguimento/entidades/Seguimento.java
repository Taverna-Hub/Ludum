package org.ludum.identidade.seguimento.entidades;

import org.ludum.identidade.conta.entidades.ContaId;
import org.ludum.identidade.seguimento.TipoAlvo;

import java.util.Objects;

public class Seguimento {

    private SeguimentoId id;
    private ContaId seguidorId;
    private AlvoId seguidoId;
    private TipoAlvo tipoAlvo;

    public Seguimento(SeguimentoId id, ContaId seguidorId, AlvoId seguidoId, TipoAlvo tipoAlvo) {
        this.id = Objects.requireNonNull(id);
        this.seguidorId = Objects.requireNonNull(seguidorId);
        this.seguidoId = Objects.requireNonNull(seguidoId);
        this.tipoAlvo = Objects.requireNonNull(tipoAlvo);
    }

    public SeguimentoId getId() {
        return id;
    }

    public void setId(SeguimentoId id) {
        this.id = id;
    }

    public ContaId getSeguidorId() {
        return seguidorId;
    }

    public void setSeguidorId(ContaId seguidorId) {
        this.seguidorId = seguidorId;
    }

    public AlvoId getSeguidoId() {
        return seguidoId;
    }

    public void setSeguidoId(AlvoId seguidoId) {
        this.seguidoId = seguidoId;
    }

    public TipoAlvo getTipoAlvo() {
        return tipoAlvo;
    }

    public void setTipoAlvo(TipoAlvo tipoAlvo) {
        this.tipoAlvo = tipoAlvo;
    }
}
