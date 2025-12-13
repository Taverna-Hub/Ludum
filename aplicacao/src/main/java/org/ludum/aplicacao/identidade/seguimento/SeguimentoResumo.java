package org.ludum.aplicacao.identidade.seguimento;

import org.ludum.dominio.identidade.seguimento.entities.Seguimento;
import org.ludum.dominio.identidade.seguimento.enums.TipoAlvo;

import java.time.LocalDateTime;

/**
 * DTO de resumo de seguimento para a camada de aplicação.
 */
public class SeguimentoResumo {
    
    private String id;
    private String seguidorId;
    private String seguidoId;
    private TipoAlvo tipoAlvo;
    private LocalDateTime dataSeguimento;

    // Informações agregadas do alvo (seguido)
    private String alvoNome;

    public SeguimentoResumo() {}

    public static SeguimentoResumo fromSeguimento(Seguimento seguimento, String alvoNome) {
        SeguimentoResumo resumo = new SeguimentoResumo();
        resumo.id = seguimento.getId().getValue();
        resumo.seguidorId = seguimento.getSeguidorId().getValue();
        resumo.seguidoId = seguimento.getSeguidoId().getValue();
        resumo.tipoAlvo = seguimento.getTipoAlvo();
        resumo.dataSeguimento = seguimento.getDataSeguimento();
        resumo.alvoNome = alvoNome;
        return resumo;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSeguidorId() {
        return seguidorId;
    }

    public void setSeguidorId(String seguidorId) {
        this.seguidorId = seguidorId;
    }

    public String getSeguidoId() {
        return seguidoId;
    }

    public void setSeguidoId(String seguidoId) {
        this.seguidoId = seguidoId;
    }

    public LocalDateTime getDataSeguimento() {
        return dataSeguimento;
    }

    public void setDataSeguimento(LocalDateTime dataSeguimento) {
        this.dataSeguimento = dataSeguimento;
    }

    public TipoAlvo getTipoAlvo() {
        return tipoAlvo;
    }

    public void setTipoAlvo(TipoAlvo tipoAlvo) {
        this.tipoAlvo = tipoAlvo;
    }



    public String getAlvoNome() {
        return alvoNome;
    }

    public void setAlvoNome(String alvoNome) {
        this.alvoNome = alvoNome;
    }
}
