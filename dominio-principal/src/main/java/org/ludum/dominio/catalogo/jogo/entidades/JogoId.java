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

}
