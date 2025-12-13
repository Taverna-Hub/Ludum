package org.ludum.dominio.catalogo.jogo.entidades;

import java.util.Objects;

public class JogoId {
    private final String id;

    public JogoId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getValue() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        JogoId jogoId = (JogoId) o;
        return Objects.equals(id, jogoId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }

}
