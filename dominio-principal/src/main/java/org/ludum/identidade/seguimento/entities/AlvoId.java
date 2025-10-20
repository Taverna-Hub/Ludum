package org.ludum.identidade.seguimento.entities;

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
