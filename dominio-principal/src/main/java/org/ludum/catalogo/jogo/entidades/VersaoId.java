package org.ludum.catalogo.jogo.entidades;

import java.util.Objects;

public class VersaoId {
    private final String id;

    public VersaoId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getValue() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VersaoId versaoId = (VersaoId) o;
        return Objects.equals(id, versaoId.id);
    }

}
