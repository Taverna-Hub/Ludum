package org.ludum.dominio.identidade.conta.entities;

import java.util.Objects;

public class ContaId {
    private String value;

    public ContaId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ContaId contaId = (ContaId) o;
        return Objects.equals(value, contaId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
