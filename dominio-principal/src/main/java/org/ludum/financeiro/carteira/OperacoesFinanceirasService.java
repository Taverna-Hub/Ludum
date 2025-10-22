package org.ludum.financeiro.carteira;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.ludum.financeiro.carteira.entidades.Carteira;
import org.ludum.financeiro.transacao.TransacaoRepository;
import org.ludum.financeiro.transacao.entidades.Transacao;
import org.ludum.financeiro.transacao.enums.StatusTransacao;
import org.ludum.financeiro.transacao.enums.TipoTransacao;

public class OperacoesFinanceirasService {
    private final TransacaoRepository transacaoRepository;

    public OperacoesFinanceirasService(TransacaoRepository transacaoRepository) {
        this.transacaoRepository = transacaoRepository;
    }

    public boolean adicionarSaldo(Carteira carteira, BigDecimal valor, boolean pagamentoConfirmado) {
        Objects.requireNonNull(carteira, "Carteira não pode ser nula");
        Objects.requireNonNull(valor, "Valor não pode ser nulo");

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        Transacao transacao = new Transacao(null, carteira.getId(), null, TipoTransacao.CREDITO,
                pagamentoConfirmado ? StatusTransacao.CONFIRMADA : StatusTransacao.PENDENTE,
                LocalDateTime.now(), valor);

        if (pagamentoConfirmado) {
            if (valor.compareTo(new BigDecimal("100")) > 0) {
                carteira.getSaldo().addBloqueado(valor);
                transacao.setStatus(StatusTransacao.PENDENTE);
            } else {
                carteira.getSaldo().addDisponivel(valor);
            }
        }

        transacaoRepository.salvar(transacao);
        return true;
    }

    public boolean comprarJogo(Carteira carteiraSaida, Carteira carteiraEntrada, BigDecimal valor) {
        Objects.requireNonNull(carteiraSaida, "Carteira de saída não pode ser nula");
        Objects.requireNonNull(carteiraEntrada, "Carteira de entrada não pode ser nula");
        Objects.requireNonNull(valor, "Valor não pode ser nulo");

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        if (carteiraSaida.getSaldo().getDisponivel().compareTo(valor) >= 0) {
            carteiraSaida.getSaldo().subtrairDisponivel(valor);

            carteiraEntrada.getSaldo().addBloqueado(valor);

            Transacao transacaoDebito = new Transacao(null, carteiraSaida.getId(), carteiraEntrada.getId(),
                    TipoTransacao.DEBITO,
                    StatusTransacao.CONFIRMADA, LocalDateTime.now(), valor);
            transacaoRepository.salvar(transacaoDebito);

            Transacao transacaoCredito = new Transacao(null, carteiraSaida.getId(), carteiraEntrada.getId(),
                    TipoTransacao.CREDITO,
                    StatusTransacao.PENDENTE, LocalDateTime.now(), valor);
            transacaoRepository.salvar(transacaoCredito);

            return true;
        } else {
            return false;
        }
    }

    public boolean solicitarReembolso(Carteira carteira, BigDecimal valor, Date dataTransacao) {
        long diferencaMillis = new Date().getTime() - dataTransacao.getTime();
        long diferencaHoras = TimeUnit.MILLISECONDS.toHours(diferencaMillis);

        if (diferencaHoras >= 0 && diferencaHoras <= 24) {
            carteira.getSaldo().addDisponivel(valor);

            Transacao transacao = new Transacao(null, null, carteira.getId(), TipoTransacao.CREDITO,
                    StatusTransacao.CONFIRMADA, LocalDateTime.now(), valor);

            transacaoRepository.salvar(transacao);
            return true;
        } else {
            return false;
        }
    }

    public boolean solicitarSaque(Carteira carteira, BigDecimal valor,
            Date dataVenda, boolean isCrowdfunding, boolean metaAtingida) {

        if (!carteira.isContaExternaValida()) {
            return false;
        }

        long diferencaMillis = new Date().getTime() - dataVenda.getTime();
        long diferencaHoras = TimeUnit.MILLISECONDS.toHours(diferencaMillis);

        if (isCrowdfunding) {
            if (!metaAtingida) {
                return false;
            }
            if (diferencaHoras < 24) {
                return false;
            }
        } else if (diferencaHoras < 24) {
            return false;
        }

        if (carteira.getSaldo().getDisponivel().compareTo(valor) >= 0) {
            carteira.getSaldo().subtrairDisponivel(valor);

            Transacao transacao = new Transacao(null, carteira.getId(), null, TipoTransacao.SAQUE,
                    StatusTransacao.CONFIRMADA, LocalDateTime.now(), valor);

            transacaoRepository.salvar(transacao);
            return true;
        } else {
            return false;
        }
    }
}