package org.ludum.dominio.crowdfunding.entidades;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class CampanhaId implements Serializable {
    private final String value;

    public CampanhaId() {
        this.value = UUID.randomUUID().toString();
    }

    public CampanhaId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CampanhaId that = (CampanhaId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
