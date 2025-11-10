package org.ludum.dominio.oficina.mod.entidades;

import java.time.LocalDateTime;
import java.util.Objects;

public class VersaoMod {
    
    private final String notasDeAtualizacao;
    private final byte[] arquivo;
    private final LocalDateTime dataDeEnvio;

    public VersaoMod(String notasDeAtualizacao, byte[] arquivo) {
        this.notasDeAtualizacao = Objects.requireNonNull(notasDeAtualizacao, "As notas não podem ser nulas.");
        this.arquivo = Objects.requireNonNull(arquivo, "O arquivo de mod não pode ser nulo.");
        this.dataDeEnvio = LocalDateTime.now();
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
