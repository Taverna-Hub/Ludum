package org.ludum.comunidade.review.services;

import org.ludum.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.catalogo.biblioteca.repositorios.BibliotecaRepository;
import org.ludum.catalogo.jogo.entidades.Jogo;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.catalogo.jogo.repositorios.JogoRepository;
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
    private final BibliotecaRepository bibliotecaRepository;

    public ReviewService(ReviewRepository reviewRepository, JogoRepository jogoRepository, BibliotecaRepository bibliotecaRepository) {
        this.reviewRepository = reviewRepository;
        this.jogoRepository = jogoRepository;
        this.bibliotecaRepository = bibliotecaRepository;
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

        // Validar se o usuário comprou ou baixou o jogo
        Biblioteca biblioteca = bibliotecaRepository.obterPorJogador(autorId);
        if (biblioteca == null || biblioteca.buscarJogoEmBiblioteca(jogoId).isEmpty()) {
            throw new IllegalArgumentException("Você precisa ter o jogo na sua biblioteca para avaliá-lo.");
        }

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

    /**
     * H-2: Como usuário, quero editar uma review já existente.
     *
     * Critérios de Aceitação:
     * - O usuário pode editar apenas a própria review.
     * - O sistema deve manter o histórico da última edição (data/hora).
     * - Após a edição, a review atualizada substitui a anterior.
     */
    public void editarAvaliacao(ReviewId reviewId, ContaId autorId, String novoTitulo, String novoTexto, int novaNota, boolean novaRecomendacao) {
        // Validar se a review existe
        Review review = reviewRepository.obterPorId(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review não encontrada.");
        }

        // Validar se o usuário é o autor da review
        String autorOriginal = review.getAutorId().getValue();
        if (!autorOriginal.equals(autorId.getValue())) {
            throw new IllegalArgumentException("Você só pode editar suas próprias avaliações.");
        }

        // Validar dados da edição
        if (novoTitulo == null || novoTitulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título da review não pode estar vazio.");
        }

        if (novoTexto == null || novoTexto.trim().isEmpty()) {
            throw new IllegalArgumentException("O texto da review não pode estar vazio.");
        }

        // Editar conteúdo e nota usando os métodos do agregado
        review.editarConteudo(novoTitulo, novoTexto);
        review.ajustarNota(novaNota);
        review.setRecomendado(novaRecomendacao);

        // Salvar a review editada
        reviewRepository.salvar(review);
    }

}
