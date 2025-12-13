package org.ludum.aplicacao.catalogo.jogo.impl;

import org.ludum.aplicacao.catalogo.jogo.JogoRepositorioConsulta;
import org.ludum.aplicacao.catalogo.jogo.JogoResumo;
import org.ludum.aplicacao.catalogo.jogo.JogoServicoAplicacao;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.Slug;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.comunidade.review.services.ReviewService;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de aplicação para jogos.
 * Orquestra repositórios e serviços de domínio para compor os dados de resposta.
 */
@Service
public class JogoServicoAplicacaoImpl implements JogoServicoAplicacao {

    private final JogoRepository jogoRepository;
    private final JogoRepositorioConsulta jogoRepositorioConsulta;
    private final ContaRepository contaRepository;
    private final ReviewService reviewService;

    public JogoServicoAplicacaoImpl(
            JogoRepository jogoRepository,
            JogoRepositorioConsulta jogoRepositorioConsulta,
            ContaRepository contaRepository, 
            ReviewService reviewService) {
        this.jogoRepository = jogoRepository;
        this.jogoRepositorioConsulta = jogoRepositorioConsulta;
        this.contaRepository = contaRepository;
        this.reviewService = reviewService;
    }

    @Override
    public List<JogoResumo> listarJogosPublicados() {
        List<Jogo> jogos = jogoRepositorioConsulta.listarTodosPublicados();
        
        return jogos.stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<JogoResumo> obterPorIdOuSlug(String idOuSlug) {
        Jogo jogo = null;
        
        // Tenta buscar por slug primeiro
        try {
            jogo = jogoRepository.obterPorSlug(Slug.criar(idOuSlug));
        } catch (Exception e) {
            // Slug inválido, ignora e tenta por ID
        }
        
        // Se não encontrar, tenta por ID
        if (jogo == null) {
            jogo = jogoRepository.obterPorId(new JogoId(idOuSlug));
        }
        
        if (jogo == null) {
            return Optional.empty();
        }
        
        return Optional.of(converterParaResumo(jogo));
    }

    @Override
    public List<JogoResumo> listarPorDesenvolvedora(String desenvolvedoraId) {
        ContaId contaId = new ContaId(desenvolvedoraId);
        List<Jogo> jogos = jogoRepositorioConsulta.listarPorDesenvolvedora(contaId);
        
        return jogos.stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }

    /**
     * Converte uma entidade Jogo para JogoResumo, agregando dados de outras fontes.
     */
    private JogoResumo converterParaResumo(Jogo jogo) {
        // Buscar nome da desenvolvedora
        String developerName = obterNomeDesenvolvedora(jogo.getDesenvolvedoraId());
        
        // Buscar estatísticas de reviews
        JogoId jogoId = jogo.getId();
        double rating = calcularRating(jogoId);
        int reviewCount = contarReviews(jogoId);
        
        return JogoResumo.fromJogo(jogo, developerName, rating, reviewCount);
    }

    private String obterNomeDesenvolvedora(ContaId desenvolvedoraId) {
        Conta desenvolvedora = contaRepository.obterPorId(desenvolvedoraId);
        return desenvolvedora != null ? desenvolvedora.getNome() : "Desenvolvedor Desconhecido";
    }

    private double calcularRating(JogoId jogoId) {
        try {
            return reviewService.calcularMediaEstrelas(jogoId);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private int contarReviews(JogoId jogoId) {
        try {
            return reviewService.obterReviewsDoJogo(jogoId).size();
        } catch (Exception e) {
            return 0;
        }
    }
}
