package org.ludum.crowdfunding.services;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import org.ludum.crowdfunding.entidades.Apoio;
import org.ludum.crowdfunding.entidades.Campanha;
import org.ludum.crowdfunding.entidades.CampanhaId;
import org.ludum.crowdfunding.repositorios.ApoioRepository;
import org.ludum.crowdfunding.repositorios.CampanhaRepository;
import org.ludum.financeiro.transacao.entidades.TransacaoId;
import org.ludum.identidade.conta.entities.ContaId;

public class ApoioService {

    private final ApoioRepository apoioRepository;
    private final CampanhaRepository campanhaRepository;
    // private final OperacoesFinanceirasService operacoesFinanceirasService;

    public ApoioService(ApoioRepository apoioRepository, CampanhaRepository campanhaRepository) {
        this.apoioRepository = Objects.requireNonNull(apoioRepository);
        this.campanhaRepository = Objects.requireNonNull(campanhaRepository);
        // this.operacoesFinanceirasService = Objects.requireNonNull(operacoesFinanceirasService);
    }

    public Apoio apoiarCampanha(ContaId apoiadorId, CampanhaId campanhaId, BigDecimal valor) {
        Objects.requireNonNull(apoiadorId);
        Objects.requireNonNull(campanhaId);
        Objects.requireNonNull(valor);

        Campanha campanha = campanhaRepository.buscarPorId(campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada"));
        
        // Processa o pagamento e obtém um ID de transação
        // TransacaoId transacao = operacoesFinanceirasService.processarPagamentoApoio(...);
        TransacaoId transacaoId = new TransacaoId("transacao-simulada-123"); // Simulação

        campanha.adicionarApoio(valor);
        Apoio novoApoio = new Apoio(campanhaId, apoiadorId, transacaoId, valor);

        // Salva ambos os agregados (idealmente numa transacao)
        apoioRepository.salvar(novoApoio);
        campanhaRepository.salvar(campanha);

        return novoApoio;
    }

    public void solicitarReembolso(String apoioId, ContaId solicitanteId) {
        Apoio apoio = apoioRepository.buscarPorId(apoioId)
                    .orElseThrow(() -> new IllegalArgumentException("Apoio não encontrado."));

        if (!apoio.getApoiadorId().equals(solicitanteId)) {
            throw new IllegalStateException("Apenas o apoiador original pode solicitar o reembolso.");
        }

        long horasDesdeApoio = Duration.between(apoio.getData(), LocalDateTime.now()).toHours();
        if (horasDesdeApoio > 24) {
            throw new IllegalStateException("O prazo para solicitar reembolso expirou (24 horas).");
        }

        apoio.cancelar();

        Campanha campanha = campanhaRepository.buscarPorId(apoio.getCampanhaId())
                            .orElseThrow(() -> new IllegalStateException("Campanha associada ao apoio não foi encontrada."));
        
        campanha.removerApoio(apoio.getValor());

        apoioRepository.salvar(apoio);
        campanhaRepository.salvar(campanha);
    }
}
