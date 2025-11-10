package org.ludum.dominio.comunidade.review.entidades;

import java.util.Objects;

public class ReviewId {
    private String value;

    public ReviewId(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
