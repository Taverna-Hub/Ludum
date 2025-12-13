package org.ludum.dominio.financeiro.dto;

import java.math.BigDecimal;

public class DadosTransferencia {
    private final String chavePix;
    private final BigDecimal valor;
    private final String descricao;
    private final Object dadosAdicionais;

    public DadosTransferencia(String chavePix, BigDecimal valor, String descricao) {
        this(chavePix, valor, descricao, null);
    }

    public DadosTransferencia(String chavePix, BigDecimal valor, String descricao, Object dadosAdicionais) {
        this.chavePix = chavePix;
        this.valor = valor;
        this.descricao = descricao;
        this.dadosAdicionais = dadosAdicionais;
    }

    public String getChavePix() { return chavePix; }
    public BigDecimal getValor() { return valor; }
    public String getDescricao() { return descricao; }
    public Object getDadosAdicionais() { return dadosAdicionais; }
}
