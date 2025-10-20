package org.ludum.crowdfunding.services;

import java.math.BigDecimal;
import java.util.Objects;

import org.ludum.crowdfunding.entidades.Apoio;
import org.ludum.crowdfunding.entidades.Campanha;
import org.ludum.crowdfunding.entidades.CampanhaId;
import org.ludum.crowdfunding.repositorios.ApoioRepository;
import org.ludum.crowdfunding.repositorios.CampanhaRepository;
import org.ludum.financeiro.transacao.entidades.TransacaoId;
import org.ludum.identidade.conta.entidades.ContaId;

public class ApoioService {

    private final ApoioRepository apoioRepository;
    private final CampanhaRepository campanhaRepository;
    // TODO: Adicionar serviço de processamento financeiro

    public ApoioService(ApoioRepository apoioRepository, CampanhaRepository campanhaRepository) {
        this.apoioRepository = Objects.requireNonNull(apoioRepository);
        this.campanhaRepository = Objects.requireNonNull(campanhaRepository);
    }

    public Apoio apoiarCampanha(ContaId apoiadorId, CampanhaId campanhaId, BigDecimal valor) {
        Objects.requireNonNull(apoiadorId);
        Objects.requireNonNull(campanhaId);
        Objects.requireNonNull(valor);

        Campanha campanha = campanhaRepository.buscarPorId(campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada"));
        
        // Processa o pagamento e obtém um ID de transação
        // TODO: Adicionar o processamento do pagamento aqui.
        TransacaoId transacaoId = new TransacaoId("transacao-simulada-123"); // Simulação

        // Adiciona o valor ao total arrecadado na campanha
        campanha.adicionarApoio(valor);

        // Cria o registro do apoio
        Apoio novoApoio = new Apoio(campanhaId, apoiadorId, transacaoId, valor);

        // Salva ambos os agregados (idealmente numa transacao)
        apoioRepository.salvar(novoApoio);
        campanhaRepository.salvar(campanha);

        return novoApoio;
    }
}
