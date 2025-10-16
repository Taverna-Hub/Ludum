package org.ludum.identidade.conta.entidades;

import java.util.Objects;

public class ContaId {
    private String value;

    public ContaId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
