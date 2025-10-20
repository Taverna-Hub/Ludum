package org.ludum.crowdfunding.entidades;

import java.math.BigDecimal;
import java.util.Objects;

import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.crowdfunding.enums.StatusCampanha;
import org.ludum.identidade.conta.entities.ContaId;

public class Campanha {

    private final CampanhaId id;
    private final JogoId jogoId;
    private final ContaId desenvolvedorId;
    private final BigDecimal meta;
    private BigDecimal valorArrecadado;
    private Periodo periodo;
    private StatusCampanha status;

    public Campanha(JogoId jogoId, ContaId desenvolvedorId, BigDecimal meta, Periodo periodo) {
        this.id = new CampanhaId();
        this.jogoId = Objects.requireNonNull(jogoId);
        this.desenvolvedorId = Objects.requireNonNull(desenvolvedorId);
        if (meta == null || meta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A meta financeira deve ser positiva");
        }
        this.meta = meta;
        this.periodo = Objects.requireNonNull(periodo);
        this.status = StatusCampanha.EM_PREPARACAO;
        this.valorArrecadado = BigDecimal.ZERO;
    }

    // --- Comportamentos ---

    public void iniciar() {
        if (this.status != StatusCampanha.EM_PREPARACAO) {
            throw new IllegalStateException("Apenas campanhas em preparação podem ser iniciadadas.");
        }
        this.status = StatusCampanha.ATIVA;
    }

    public void adicionarApoio(BigDecimal valor) {
        if (this.status != StatusCampanha.ATIVA) {
            throw new IllegalStateException("A campanha não está ativa para receber apoios.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do apoio deve ser positivo.");
        }
        this.valorArrecadado = this.valorArrecadado.add(valor);
    }

    public void finalizar() {
        if (this.status != StatusCampanha.ATIVA) {
            throw new IllegalStateException("Apenas campanhas ativas podem ser finalizadas.");
        }
        if (this.valorArrecadado.compareTo(this.meta) >= 0) {
            this.status = StatusCampanha.FINANCIADA;
        } else {
            this.status = StatusCampanha.NAO_FINANCIADA;
        }
    }

    public BigDecimal getValorFaltante() {
        BigDecimal faltante = this.meta.subtract(this.valorArrecadado);
        return faltante.compareTo(BigDecimal.ZERO) > 0 ? faltante : BigDecimal.ZERO;
    }

    public void removerApoio(BigDecimal valor) {
        if (this.status != StatusCampanha.ATIVA) {
            throw new IllegalStateException("Apoios só podem ser removidos de campanhas ativas.");
        }
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor do apoio a ser removido deve ser positivo.");
        }
        this.valorArrecadado = this.valorArrecadado.subtract(valor);
    }

    public CampanhaId getId() {
        return id;
    }

    public JogoId getJogoId() {
        return jogoId;
    }

    public ContaId getDesenvolvedorId() {
        return desenvolvedorId;
    }

    public BigDecimal getMeta() {
        return meta;
    }

    public BigDecimal getValorArrecadado() {
        return valorArrecadado;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public StatusCampanha getStatus() {
        return status;
    }
}
