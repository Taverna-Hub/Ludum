package org.ludum.seguimento.entidades;

import java.util.Objects;

public class AlvoId {
    private final String value;

    public AlvoId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
