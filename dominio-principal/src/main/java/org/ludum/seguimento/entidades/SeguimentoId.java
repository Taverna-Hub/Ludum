package org.ludum.seguimento.entidades;

import java.util.Objects;
import java.util.UUID;

public class SeguimentoId {
    private final String value;

    public SeguimentoId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
