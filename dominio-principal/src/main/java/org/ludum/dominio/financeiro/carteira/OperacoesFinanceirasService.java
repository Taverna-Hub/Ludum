package org.ludum.dominio.financeiro.carteira;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno.ResultadoPagamento;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.carteira.entidades.Saldo;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.enums.StatusTransacao;
import org.ludum.dominio.financeiro.transacao.enums.TipoTransacao;
import org.ludum.dominio.identidade.conta.entities.ContaId;

public class OperacoesFinanceirasService {
    private final TransacaoRepository transacaoRepository;
    private final CarteiraRepository carteiraRepository;
    private ProcessadorPagamentoExterno processadorPagamento;

    public OperacoesFinanceirasService(TransacaoRepository transacaoRepository, CarteiraRepository carteiraRepository) {
        this.transacaoRepository = transacaoRepository;
        this.carteiraRepository = carteiraRepository;
    }

    public void setProcessadorPagamento(ProcessadorPagamentoExterno processadorPagamento) {
        this.processadorPagamento = processadorPagamento;
    }

    public Carteira obterOuCriarCarteira(ContaId contaId) {
        Objects.requireNonNull(contaId, "ContaId não pode ser nulo");

        Carteira carteira = carteiraRepository.obterPorContaId(contaId);
        if (carteira == null) {
            carteira = new Carteira(contaId, new Saldo());
            carteiraRepository.salvar(carteira);
        }
        return carteira;
    }

    public Carteira obterCarteira(ContaId contaId) {
        Objects.requireNonNull(contaId, "ContaId não pode ser nulo");
        return carteiraRepository.obterPorContaId(contaId);
    }

    public boolean adicionarSaldo(ContaId contaId, BigDecimal valor, String moeda, String descricao,
            String nomeCliente, String cpfCnpjCliente, String emailCliente, String telefoneCliente) {
        Objects.requireNonNull(contaId, "ContaId não pode ser nulo");
        Objects.requireNonNull(valor, "Valor não pode ser nulo");
        Objects.requireNonNull(nomeCliente, "Nome do cliente não pode ser nulo");
        Objects.requireNonNull(cpfCnpjCliente, "CPF/CNPJ do cliente não pode ser nulo");
        Objects.requireNonNull(emailCliente, "Email do cliente não pode ser nulo");

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        if (processadorPagamento == null) {
            throw new IllegalStateException(
                    "Processador de pagamento não configurado. Use setProcessadorPagamento() primeiro.");
        }

        Carteira carteira = obterOuCriarCarteira(contaId);

        ResultadoPagamento resultado = processadorPagamento.processar(contaId, valor, moeda, descricao,
                nomeCliente, cpfCnpjCliente, emailCliente, telefoneCliente);

        if (resultado.isSucesso()) {
            if (valor.compareTo(new BigDecimal("100")) > 0) {
                carteira.getSaldo().addBloqueado(valor);
            } else {
                carteira.getSaldo().addDisponivel(valor);
            }
            carteiraRepository.salvar(carteira);
        }

        return resultado.isSucesso();
    }

    public void bloquearSaldo(Carteira carteira, BigDecimal valor) {
        Objects.requireNonNull(carteira, "Carteira não pode ser nula");
        Objects.requireNonNull(valor, "Valor não pode ser nulo");

        if (carteira.getSaldo().getDisponivel().compareTo(valor) < 0) {
            throw new IllegalStateException("Saldo insuficiente para bloqueio");
        }

        carteira.getSaldo().subtrairDisponivel(valor);
        carteira.getSaldo().addBloqueado(valor);
        carteiraRepository.salvar(carteira);
    }

    public void liberarSaldoBloqueado(Carteira carteira) {
        Objects.requireNonNull(carteira, "Carteira não pode ser nula");
        carteira.liberarSaldoBloqueado();
        carteiraRepository.salvar(carteira);
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
                    TipoTransacao.DEBITO, StatusTransacao.CONFIRMADA, LocalDateTime.now(), valor);
            transacaoRepository.salvar(transacaoDebito);

            Transacao transacaoCredito = new Transacao(null, carteiraSaida.getId(), carteiraEntrada.getId(),
                    TipoTransacao.CREDITO, StatusTransacao.PENDENTE, LocalDateTime.now(), valor);
            transacaoRepository.salvar(transacaoCredito);

            return true;
        }
        return false;
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
        }
        return false;
    }

    public boolean solicitarSaque(Carteira carteira, String recipientId, BigDecimal valor,
            Date dataVenda, boolean isCrowdfunding, boolean metaAtingida) {

        if (!carteira.isContaExternaValida()) {
            return false;
        }

        if (recipientId == null || recipientId.isBlank()) {
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

        if (carteira.getSaldo().getDisponivel().compareTo(valor) < 0) {
            return false;
        }

        if (processadorPagamento == null) {
            return false;
        }

        try {
            processadorPagamento.executarPayout(carteira.getId(), valor, "Saque de vendas - Ludum");

            carteira.getSaldo().subtrairDisponivel(valor);

            Transacao transacao = new Transacao(null, carteira.getId(), null, TipoTransacao.SAQUE,
                    StatusTransacao.CONFIRMADA, LocalDateTime.now(), valor);
            transacaoRepository.salvar(transacao);
            carteiraRepository.salvar(carteira);

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
