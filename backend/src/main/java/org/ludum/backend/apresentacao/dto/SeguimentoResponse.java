package org.ludum.backend.apresentacao.dto;

import org.ludum.aplicacao.identidade.seguimento.SeguimentoResumo;

import java.time.LocalDateTime;

/**
 * DTO de resposta HTTP para seguimentos.
 */
public class SeguimentoResponse {
    
    private String id;
    private String seguidorId;
    private String alvoId;
    private String tipoAlvo;
    private LocalDateTime dataSeguimento;
    private String alvoNome;

    public SeguimentoResponse() {}

    public static SeguimentoResponse fromResumo(SeguimentoResumo resumo) {
        SeguimentoResponse response = new SeguimentoResponse();
        response.id = resumo.getId();
        response.seguidorId = resumo.getSeguidorId();
        response.alvoId = resumo.getSeguidoId();
        response.tipoAlvo = resumo.getTipoAlvo() != null ? resumo.getTipoAlvo().name() : null;
        response.dataSeguimento = resumo.getDataSeguimento();
        response.alvoNome = resumo.getAlvoNome();
        return response;
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

    public String getAlvoId() {
        return alvoId;
    }

    public void setAlvoId(String alvoId) {
        this.alvoId = alvoId;
    }

    public String getTipoAlvo() {
        return tipoAlvo;
    }

    public void setTipoAlvo(String tipoAlvo) {
        this.tipoAlvo = tipoAlvo;
    }

    public LocalDateTime getDataSeguimento() {
        return dataSeguimento;
    }

    public void setDataSeguimento(LocalDateTime dataSeguimento) {
        this.dataSeguimento = dataSeguimento;
    }

    public String getAlvoNome() {
        return alvoNome;
    }

    public void setAlvoNome(String alvoNome) {
        this.alvoNome = alvoNome;
    }
}
