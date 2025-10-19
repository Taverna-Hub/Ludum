package org.ludum.catalogo.jogo.entidades;

import java.util.Arrays;

public class PacoteZip {
    private final byte[] conteudo;

    public PacoteZip(byte[] conteudo) {
        this.conteudo = conteudo;
    }

    public byte[] getConteudo() {
        return Arrays.copyOf(this.conteudo, this.conteudo.length);
    }

    @Override
    public int hashCode(){
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
