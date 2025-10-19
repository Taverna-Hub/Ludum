package org.ludum.identidade.bloqueio.entities;

import java.util.Objects;

public class BloqueioId {
    private String value;

    public BloqueioId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
