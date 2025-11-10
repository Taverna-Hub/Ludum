package org.ludum.dominio.comunidade.post.entidades;

import java.util.Objects;

public class PostId {
    private final String id;

    public PostId(String id) {
        this.id = Objects.requireNonNull(id);
    }

    public String getId() {
        return id;
    }

}
