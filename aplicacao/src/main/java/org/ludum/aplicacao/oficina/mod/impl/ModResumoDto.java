package org.ludum.aplicacao.oficina.mod.impl;

import java.time.LocalDateTime;

import org.ludum.aplicacao.oficina.mod.query.ModResumo;

public class ModResumoDto implements ModResumo{

    private final String id;
    private final String nome;
    private final String descricao;
    private final String autorId;
    private final String status;
    private final int totalVersoes;
    private final LocalDateTime dataUltimoEnvio;

    public ModResumoDto(String id, String nome, String descricao, String autorId, String status,
                        int totalVersoes, LocalDateTime dataUltimoEnvio) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.autorId = autorId;
        this.status = status;
        this.totalVersoes = totalVersoes;
        this.dataUltimoEnvio = dataUltimoEnvio;
    }

    @Override
    public String getId() { 
        return id; 
    }

    @Override
    public String getNome() { 
        return nome; 
    }
    
    @Override
    public String getDescricao() { 
        return descricao; 
    }

    @Override
    public String getAutorId() { 
        return autorId;
    }

    @Override
    public String getStatus() { 
        return status; 
    }

    @Override
    public int getTotalVersoes() { 
        return totalVersoes; 
    }
    
    @Override
    public LocalDateTime getDataUltimoEnvio() { 
        return dataUltimoEnvio; 
    }
}
