package org.ludum.dominio.catalogo.tag.entidades;

import java.util.Objects;

public class TagId {
    private final String id;

    public TagId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getValue() {
        return id;
    }

    @Override
    public String toString() {
        return "TagId{" + id + '}';
    }
}
