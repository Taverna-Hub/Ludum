package org.ludum.dominio.financeiro.transacao.entidades;

import java.util.Objects;

public class ReciboId {
    private String value;

    public ReciboId(String value) {
      this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
