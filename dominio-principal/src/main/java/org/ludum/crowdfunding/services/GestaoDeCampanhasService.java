package org.ludum.crowdfunding.services;

import java.math.BigDecimal;
import java.util.Objects;

import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.crowdfunding.entidades.Campanha;
import org.ludum.crowdfunding.entidades.CampanhaId;
import org.ludum.crowdfunding.entidades.Periodo;
import org.ludum.crowdfunding.repositorios.CampanhaRepository;
import org.ludum.identidade.conta.entidades.ContaId;

public class GestaoDeCampanhasService {
    
    private final CampanhaRepository campanhaRepository;

    public GestaoDeCampanhasService(CampanhaRepository campanhaRepository) {
        this.campanhaRepository = Objects.requireNonNull(campanhaRepository);
    }

    public Campanha criarCampanha(JogoId jogoId, ContaId devId, BigDecimal meta, Periodo periodo) {
        // TODO: Adicionar validações, como verificar se o jogoId existe e pertence ao devId
        Campanha novaCampanha = new Campanha(jogoId, devId, meta, periodo);
        campanhaRepository.salvar(novaCampanha);
        return novaCampanha;
    }

    public void iniciarCampanha(CampanhaId campanhaId, ContaId solicitanteId) {
        Campanha campanha = campanhaRepository.buscarPorId(campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada"));
        
        if (!campanha.getDesenvolvedorId().equals(solicitanteId)) {
            throw new IllegalStateException("Apenas o dono da campanha pode iniciá-la.");
        }

        campanha.iniciar();
        campanhaRepository.salvar(campanha);
    }

    public void finalizarCampanha(CampanhaId campanhaId) {
        Campanha campanha = campanhaRepository.buscarPorId(campanhaId)
                .orElseThrow(() -> new IllegalArgumentException("Campanha não encontrada."));
        
        // TODO: Criar métodos de finalização de forma manual ou por "job".

        campanha.finalizar();
        campanhaRepository.salvar(campanha);
    }
}
