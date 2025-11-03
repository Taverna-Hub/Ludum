package org.ludum.dominio.comunidade.post.entidades;

import java.util.Objects;

public class ComentarioId {
    private final String id;

    public ComentarioId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getId() {
        return id;
    }
}
