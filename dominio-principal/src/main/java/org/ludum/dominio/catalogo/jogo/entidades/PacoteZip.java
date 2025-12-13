package org.ludum.dominio.catalogo.jogo.entidades;

import java.util.Arrays;

public class PacoteZip {
    private final byte[] conteudo;

    public PacoteZip(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    private static final byte[] MAGIC_BYTES_ZIP = { 0x50, 0x4B, 0x03, 0x04 };

    public byte[] getConteudo() {
        return Arrays.copyOf(this.conteudo, this.conteudo.length);
    }

    public boolean ehIntegro() {
        if (this.conteudo == null || this.conteudo.length < 4) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            if (this.conteudo[i] != MAGIC_BYTES_ZIP[i]) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(conteudo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PacoteZip other = (PacoteZip) obj;
        return Arrays.equals(conteudo, other.conteudo);

    }

}
