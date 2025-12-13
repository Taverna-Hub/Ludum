package org.ludum.dominio.financeiro.carteira;

import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.dto.DadosTransferencia;
import org.ludum.dominio.financeiro.dto.ResultadoPayout;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.enums.StatusTransacao;
import org.ludum.dominio.financeiro.transacao.enums.TipoTransacao;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Template Method para processamento de Payout (Saques/Transferências).
 * 
 * Define o algoritmo padrão para execução de saques, permitindo que
 * implementações concretas (Asaas, Stripe, etc.) customizem etapas específicas
 * sem alterar o fluxo geral.
 */
public abstract class ProcessadorPayoutExterno {

    private final TransacaoRepository transacaoRepository;
    private final CarteiraRepository carteiraRepository;

    protected ProcessadorPayoutExterno(TransacaoRepository transacaoRepository,
                                       CarteiraRepository carteiraRepository) {
        this.transacaoRepository = transacaoRepository;
        this.carteiraRepository = carteiraRepository;
    }

    public final ResultadoPayout executarPayout(ContaId contaId, BigDecimal valor, String descricao) {
        beforeExecutarPayout(contaId, valor);

        try {
            Carteira carteira = obterCarteira(contaId);
            
            validarDadosPayout(carteira, valor);
            
            DadosTransferencia dados = prepararTransferencia(carteira, valor, descricao);
            
            String transferId = executarTransferenciaNoGateway(dados);
            
            atualizarSaldoERegistrar(carteira, valor, transferId);
            
            afterExecutarPayout(transferId, true);
            
            return ResultadoPayout.sucesso(transferId, valor);
            
        } catch (Exception e) {
            afterExecutarPayout(null, false);
            return ResultadoPayout.falha(e.getMessage());
        }
    }

    private Carteira obterCarteira(ContaId contaId) {
        Carteira carteira = carteiraRepository.obterPorContaId(contaId);
        if (carteira == null) {
            throw new IllegalArgumentException("Carteira não encontrada para ContaId: " + contaId.getValue());
        }
        return carteira;
    }

    protected abstract void validarDadosPayout(Carteira carteira, BigDecimal valor);

    protected abstract DadosTransferencia prepararTransferencia(Carteira carteira, 
                                                                BigDecimal valor, 
                                                                String descricao);

    protected abstract String executarTransferenciaNoGateway(DadosTransferencia dados) throws Exception;

    protected void atualizarSaldoERegistrar(Carteira carteira, BigDecimal valor, String transferId) {
        carteira.getSaldo().subtrairDisponivel(valor);
        
        Transacao transacao = new Transacao(
            null, 
            carteira.getId(), 
            null,
            TipoTransacao.SAQUE, 
            StatusTransacao.CONFIRMADA, 
            LocalDateTime.now(), 
            valor
        );
        
        transacaoRepository.salvar(transacao);
        carteiraRepository.salvar(carteira);
    }


    protected void beforeExecutarPayout(ContaId contaId, BigDecimal valor) {
    }


    protected void afterExecutarPayout(String transferId, boolean sucesso) {
    }

    protected TransacaoRepository getTransacaoRepository() {
        return transacaoRepository;
    }

    protected CarteiraRepository getCarteiraRepository() {
        return carteiraRepository;
    }
}
