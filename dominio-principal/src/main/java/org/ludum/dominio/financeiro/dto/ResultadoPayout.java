package org.ludum.dominio.financeiro.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ResultadoPayout {
    private final boolean sucesso;
    private final String transferId;
    private final BigDecimal valor;
    private final String mensagemErro;
    private final LocalDateTime dataProcessamento;

    private ResultadoPayout(boolean sucesso, String transferId, BigDecimal valor, String mensagemErro) {
        this.sucesso = sucesso;
        this.transferId = transferId;
        this.valor = valor;
        this.mensagemErro = mensagemErro;
        this.dataProcessamento = LocalDateTime.now();
    }

    public static ResultadoPayout sucesso(String transferId, BigDecimal valor) {
        return new ResultadoPayout(true, transferId, valor, null);
    }

    public static ResultadoPayout falha(String mensagemErro) {
        return new ResultadoPayout(false, null, null, mensagemErro);
    }

    public boolean isSucesso() { return sucesso; }
    public String getTransferId() { return transferId; }
    public BigDecimal getValor() { return valor; }
    public String getMensagemErro() { return mensagemErro; }
    public LocalDateTime getDataProcessamento() { return dataProcessamento; }
}
