package org.ludum.financeiro.transacao.entidades;

import java.util.Objects;

public class TransacaoId {
    private String value;

    public TransacaoId(String value) {
      this.value = Objects.requireNonNull(value);
    }

    public String getValue() {
        return value;
    }
}
