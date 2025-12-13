package org.ludum.dominio.financeiro.carteira;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.dominio.catalogo.biblioteca.services.BibliotecaService;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
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
    private final BibliotecaService bibliotecaService;
    private ProcessadorPagamentoExterno processadorPagamento;
    private ProcessadorPayoutExterno processadorPayout;

    public OperacoesFinanceirasService(TransacaoRepository transacaoRepository, 
                                      CarteiraRepository carteiraRepository,
                                      BibliotecaService bibliotecaService) {
        this.transacaoRepository = transacaoRepository;
        this.carteiraRepository = carteiraRepository;
        this.bibliotecaService = bibliotecaService;
    }

    public void setProcessadorPagamento(ProcessadorPagamentoExterno processadorPagamento) {
        this.processadorPagamento = processadorPagamento;
    }

    public void setProcessadorPayout(ProcessadorPayoutExterno processadorPayout) {
        this.processadorPayout = processadorPayout;
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

    public void atualizarChavePix(ContaId contaId, String chavePix) {
        Objects.requireNonNull(contaId, "ContaId não pode ser nulo");
        Objects.requireNonNull(chavePix, "Chave PIX não pode ser nula");
        
        if (chavePix.isBlank()) {
            throw new IllegalArgumentException("Chave PIX não pode estar vazia");
        }
        
        Carteira carteira = obterOuCriarCarteira(contaId);
        carteira.setContaExterna(chavePix);
        carteiraRepository.salvar(carteira);
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

    public boolean comprarJogo(Carteira carteiraSaida, Carteira carteiraEntrada, BigDecimal valor, JogoId jogoId) {
        Objects.requireNonNull(carteiraSaida, "Carteira de saída não pode ser nula");
        Objects.requireNonNull(carteiraEntrada, "Carteira de entrada não pode ser nula");
        Objects.requireNonNull(valor, "Valor não pode ser nulo");
        Objects.requireNonNull(jogoId, "JogoId não pode ser nulo");

        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }

        if (carteiraSaida.getSaldo().getDisponivel().compareTo(valor) >= 0) {
            carteiraSaida.getSaldo().subtrairDisponivel(valor);
            carteiraEntrada.getSaldo().addBloqueado(valor);

            carteiraRepository.salvar(carteiraSaida);
            carteiraRepository.salvar(carteiraEntrada);

            Transacao transacaoDebito = new Transacao(null, carteiraSaida.getId(), carteiraEntrada.getId(),
                    TipoTransacao.DEBITO, StatusTransacao.CONFIRMADA, LocalDateTime.now(), valor);
            transacaoRepository.salvar(transacaoDebito);

            Transacao transacaoCredito = new Transacao(null, carteiraSaida.getId(), carteiraEntrada.getId(),
                    TipoTransacao.CREDITO, StatusTransacao.PENDENTE, LocalDateTime.now(), valor);
            transacaoRepository.salvar(transacaoCredito);

            try {
                bibliotecaService.adicionarJogo(ModeloDeAcesso.PAGO, jogoId, carteiraSaida.getId(), transacaoDebito.getTransacaoId());
            } catch (Exception e) {
                System.err.println("Erro ao adicionar jogo à biblioteca: " + e.getMessage());
            }

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

    public boolean solicitarSaque(Carteira carteira, BigDecimal valor, Date dataVenda, 
            boolean isCrowdfunding, boolean metaAtingida) {
        Objects.requireNonNull(carteira, "Carteira não pode ser nula");
        Objects.requireNonNull(valor, "Valor não pode ser nulo");
        Objects.requireNonNull(dataVenda, "Data da venda não pode ser nula");

        // Validar tempo desde a venda (regra de negócio do domínio)
        long diferencaMillis = new Date().getTime() - dataVenda.getTime();
        long diferencaHoras = TimeUnit.MILLISECONDS.toHours(diferencaMillis);

        if (isCrowdfunding) {
            if (!metaAtingida) {
                throw new IllegalStateException("Crowdfunding: meta não foi atingida");
            }
            if (diferencaHoras < 24) {
                throw new IllegalStateException("Crowdfunding: aguarde 24 horas após atingir a meta");
            }
        } else if (diferencaHoras < 24) {
            throw new IllegalStateException("Aguarde 24 horas após a venda para solicitar saque");
        }

        if (processadorPayout == null) {
            throw new IllegalStateException("Processador de payout não configurado");
        }

        // ╭───────────────────────────────────────────────────────────────╮
        // │  AQUI O TEMPLATE METHOD DE PAYOUT É INVOCADO                    │
        // │  O método executarPayout() executa todo o algoritmo:           │
        // │  1. beforeExecutarPayout() - Hook                            │
        // │  2. obterCarteira() - Busca carteira                         │
        // │  3. validarDadosPayout() - Abstract Step (validações Asaas) │
        // │  4. prepararTransferencia() - Abstract Step (formata dados)  │
        // │  5. executarTransferenciaNoGateway() - Abstract Step (API)   │
        // │  6. atualizarSaldoERegistrar() - Optional Step (atualiza DB)│
        // │  7. afterExecutarPayout() - Hook                             │
        // ╰───────────────────────────────────────────────────────────────╯
        org.ludum.dominio.financeiro.dto.ResultadoPayout resultado = processadorPayout.executarPayout(
            carteira.getId(), 
            valor, 
            "Saque de vendas - Ludum"
        );

        return resultado.isSucesso();
    }
}
