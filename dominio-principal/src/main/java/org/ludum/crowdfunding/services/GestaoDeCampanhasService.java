package org.ludum.crowdfunding.services;

import java.math.BigDecimal;
import java.util.Objects;

import org.ludum.catalogo.jogo.entidades.Jogo;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.crowdfunding.entidades.Campanha;
import org.ludum.crowdfunding.entidades.CampanhaId;
import org.ludum.crowdfunding.entidades.Periodo;
import org.ludum.crowdfunding.repositorios.CampanhaRepository;
import org.ludum.identidade.conta.entities.ContaId;

public class GestaoDeCampanhasService {
    
    private final CampanhaRepository campanhaRepository;
    private final JogoRepository jogoRepository;

    public GestaoDeCampanhasService(CampanhaRepository campanhaRepository, JogoRepository jogoRepository) {
        this.campanhaRepository = Objects.requireNonNull(campanhaRepository);
        this.jogoRepository = Objects.requireNonNull(jogoRepository);
    }

    public Campanha criarCampanha(JogoId jogoId, ContaId devId, BigDecimal meta, Periodo periodo) {
        Jogo jogo = jogoRepository.obterPorId(jogoId);
        
        if (jogo == null) {
            throw new IllegalArgumentException("O jogo associado à campanha não foi encontrado.");
        }
        if (!jogo.getDesenvolvedoraId().equals(devId)) {
            throw new IllegalStateException("Apenas o desenvolvedor dono do jogo pode criar uma campanha para ele.");
        }
        if (jogo.getStatus() == StatusPublicacao.PUBLICADO) {
            throw new IllegalStateException("Campanhas de financiamento são permitidas apenas para jogos não publicados.");
        }

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
