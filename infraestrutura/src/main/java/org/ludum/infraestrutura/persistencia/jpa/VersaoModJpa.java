package org.ludum.infraestrutura.persistencia.jpa;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;

@Embeddable
public class VersaoModJpa {

    @Column(name = "notas_atualizacao", nullable = false, length = 2000)
    private String notasDeAtualizacao;

    @Lob
    @Column(name = "arquivo", nullable = false)
    private byte[] arquivo;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataDeEnvio;

    public VersaoModJpa() {}

    public VersaoModJpa(String notasDeAtualizacao, byte[] arquivo, LocalDateTime dataDeEnvio) {
        this.notasDeAtualizacao = notasDeAtualizacao;
        this.arquivo = arquivo;
        this.dataDeEnvio = dataDeEnvio;
    }

    public String getNotasDeAtualizacao() {
        return notasDeAtualizacao; 
    }
    
    public byte[] getArquivo() { 
        return arquivo; 
    }

    public LocalDateTime getDataDeEnvio() { 
        return dataDeEnvio; 
    }
}
