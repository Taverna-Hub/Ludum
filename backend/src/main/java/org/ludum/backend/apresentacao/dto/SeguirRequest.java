package org.ludum.backend.apresentacao.dto;

import org.ludum.dominio.identidade.seguimento.enums.TipoAlvo;

public class SeguirRequest {
    private String alvoId;
    private TipoAlvo tipoAlvo;

    public String getAlvoId() {
        return alvoId;
    }

    public void setAlvoId(String alvoId) {
        this.alvoId = alvoId;
    }

    public TipoAlvo getTipoAlvo() {
        return tipoAlvo;
    }

    public void setTipoAlvo(TipoAlvo tipoAlvo) {
        this.tipoAlvo = tipoAlvo;
    }
}
