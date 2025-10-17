package org.ludum.identidade.seguimento.entities;

import java.util.Objects;

public class SeguimentoId {
    private final String value;

    public SeguimentoId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
