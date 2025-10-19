package org.ludum.comunidade.review.services;

import org.ludum.catalogo.jogo.JogoRepository;
import org.ludum.catalogo.jogo.entidades.Jogo;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.comunidade.review.entidades.Review;
import org.ludum.comunidade.review.entidades.ReviewId;
import org.ludum.comunidade.review.enums.*;
import org.ludum.comunidade.review.repositorios.ReviewRepository;
import org.ludum.identidade.conta.entities.ContaId;

import java.util.Date;
import java.util.UUID;

public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final JogoRepository jogoRepository;
    // TODO: Adicionar BibliotecaRepository quando estiver implementado

    public ReviewService(ReviewRepository reviewRepository, JogoRepository jogoRepository) {
        this.reviewRepository = reviewRepository;
        this.jogoRepository = jogoRepository;
    }

    /**
     * H-1: Como usuário, fazer uma review sobre um jogo que eu baixei.
     * 
     * Critérios de Aceitação:
     * - Só é permitido publicar a review se tiver comprado ou baixado o jogo.
     * - O jogo deve estar publicado.
     * - Só pode existir uma review por usuário por jogo.
     * - A review deve conter: Nota (0-5) estrelas, Comentário (texto), Recomendação (true/false).
     */
    public void avaliarJogo(JogoId jogoId, ContaId autorId, int nota, String titulo, String texto, boolean recomenda) {
        // Validar se o jogo existe
        Jogo jogo = jogoRepository.obterPorId(jogoId);
        if (jogo == null) {
            throw new IllegalArgumentException("Jogo não encontrado.");
        }

        // Validar se o jogo está publicado
        if (jogo.getStatus() != StatusPublicacao.PUBLICADO) {
            throw new IllegalArgumentException("Não é possível avaliar um jogo que não está publicado.");
        }

        // TODO: Validar se o usuário comprou ou baixou o jogo (aguardando BibliotecaRepository)
        // BibliotecaRepository.verificarSeUsuarioPossuiJogo(autorId, jogoId)

        // Validar se já existe uma review deste usuário para este jogo
        if (reviewRepository.obterPorAutorEJogo(autorId, jogoId).isPresent()) {
            throw new IllegalArgumentException("Você já avaliou este jogo. Use a função de editar para alterar sua avaliação.");
        }

        // Validar dados da review
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título da review não pode estar vazio.");
        }

        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O texto da review não pode estar vazio.");
        }

        // Criar a review
        ReviewId reviewId = new ReviewId(UUID.randomUUID().toString());
        Review novaReview = new Review(
            reviewId,
            jogoId,
            autorId,
            nota,
            titulo,
            texto,
            new Date(),
            recomenda,
            StatusReview.PUBLICADO
        );

        reviewRepository.salvar(novaReview);
    }

    public void editarAvaliacao(ReviewId reviewId, ContaId autorId, String novoTitulo, String novoTexto, int novaNota) {
        // TODO: Implementar lógica de edição de avaliação (H-2)
    }

}
