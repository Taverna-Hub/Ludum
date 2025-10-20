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
import java.util.List;
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

        Jogo jogo = jogoRepository.obterPorId(jogoId);
        if (jogo == null) {
            throw new IllegalArgumentException("Jogo não encontrado.");
        }

        if (jogo.getStatus() != StatusPublicacao.PUBLICADO) {
            throw new IllegalArgumentException("Não é possível avaliar um jogo que não está publicado.");
        }

        Biblioteca biblioteca = bibliotecaRepository.obterPorJogador(autorId);
        if (biblioteca == null || biblioteca.buscarJogoEmBiblioteca(jogoId).isEmpty()) {
            throw new IllegalArgumentException("Você precisa ter o jogo na sua biblioteca para avaliá-lo.");
        }

        if (reviewRepository.obterPorAutorEJogo(autorId, jogoId).isPresent()) {
            throw new IllegalArgumentException("Você já avaliou este jogo. Use a função de editar para alterar sua avaliação.");
        }

        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título da review não pode estar vazio.");
        }

        if (texto == null || texto.trim().isEmpty()) {
            throw new IllegalArgumentException("O texto da review não pode estar vazio.");
        }

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

        Review review = reviewRepository.obterPorId(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review não encontrada.");
        }

        String autorOriginal = review.getAutorId().getValue();
        if (!autorOriginal.equals(autorId.getValue())) {
            throw new IllegalArgumentException("Você só pode editar suas próprias avaliações.");
        }

        if (novoTitulo == null || novoTitulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título da review não pode estar vazio.");
        }

        if (novoTexto == null || novoTexto.trim().isEmpty()) {
            throw new IllegalArgumentException("O texto da review não pode estar vazio.");
        }

        review.editarConteudo(novoTitulo, novoTexto);
        review.ajustarNota(novaNota);
        review.setRecomendado(novaRecomendacao);

        reviewRepository.salvar(review);
    }

    /**
     * H-3: Como usuário, quero visualizar as reviews de um jogo.
     *
     * Critérios de Aceitação:
     * - O sistema deve mostrar a média geral das estrelas.
     * - O sistema deve mostrar o total de recomendações.
     * - O sistema deve mostrar a porcentagem de recomendações.
     */
    
    public List<Review> obterReviewsDoJogo(JogoId jogoId) {

        Jogo jogo = jogoRepository.obterPorId(jogoId);
        if (jogo == null) {
            throw new IllegalArgumentException("Jogo não encontrado.");
        }

        List<Review> todasReviews = reviewRepository.obterTodasPorJogo(jogoId);
        return todasReviews.stream()
                .filter(review -> review.getStatus() != StatusReview.EXCLUIDO)
                .collect(java.util.stream.Collectors.toList());
    }

    public double calcularMediaEstrelas(JogoId jogoId) {
        List<Review> reviews = obterReviewsDoJogo(jogoId);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }

        int somaNotas = reviews.stream()
                .mapToInt(Review::getNota)
                .sum();

        return (double) somaNotas / reviews.size();
    }

    public int obterTotalRecomendacoes(JogoId jogoId) {
        List<Review> reviews = obterReviewsDoJogo(jogoId);
        
        return (int) reviews.stream()
                .filter(Review::isRecomendado)
                .count();
    }

    public double calcularPorcentagemRecomendacoes(JogoId jogoId) {
        List<Review> reviews = obterReviewsDoJogo(jogoId);
        
        if (reviews.isEmpty()) {
            return 0.0;
        }

        int totalRecomendacoes = obterTotalRecomendacoes(jogoId);
        return (double) totalRecomendacoes / reviews.size() * 100;
    }

    /**
     * H-4: Como usuário, quero remover a minha review.
     *
     * Critérios de Aceitação:
     * - Apenas o autor pode excluir a review.
     * - Excluir a review significa desativar/ocultar (não apagar do banco, para manter histórico).
     * - Reviews excluídas não devem aparecer publicamente.
     */
    public void removerReview(ReviewId reviewId, ContaId autorId) {

        Review review = reviewRepository.obterPorId(reviewId);
        if (review == null) {
            throw new IllegalArgumentException("Review não encontrada.");
        }

        if (!review.getAutorId().getValue().equals(autorId.getValue())) {
            throw new IllegalArgumentException("Você só pode remover suas próprias avaliações.");
        }

        if (review.isExcluida()) {
            throw new IllegalArgumentException("Esta review já foi removida.");
        }

        // Realizar exclusão lógica (soft delete)
        review.excluir();

        // Salvar a review com status EXCLUIDO
        reviewRepository.salvar(review);
    }

    /**
     * H-5: Como usuário, quero filtrar as reviews de um jogo.
     *
     * Critérios de Aceitação:
     * - O sistema deve filtrar e ordenar por data de avaliação.
     * - O sistema deve filtrar apenas as avaliações da nota selecionada.
     */
    
    public List<Review> filtrarReviewsPorNota(JogoId jogoId, int nota) {
        // Validar nota
        if (nota < 0 || nota > 5) {
            throw new IllegalArgumentException("A nota deve estar entre 0 e 5.");
        }

        List<Review> reviews = obterReviewsDoJogo(jogoId);
        
        return reviews.stream()
                .filter(review -> review.getNota() == nota)
                .collect(java.util.stream.Collectors.toList());
    }

    public List<Review> ordenarReviewsPorData(JogoId jogoId, boolean maisRecentesPrimeiro) {
        List<Review> reviews = obterReviewsDoJogo(jogoId);
        
        if (maisRecentesPrimeiro) {
            // Ordenar do mais recente para o mais antigo
            return reviews.stream()
                    .sorted((r1, r2) -> r2.getData().compareTo(r1.getData()))
                    .collect(java.util.stream.Collectors.toList());
        } else {
            // Ordenar do mais antigo para o mais recente
            return reviews.stream()
                    .sorted((r1, r2) -> r1.getData().compareTo(r2.getData()))
                    .collect(java.util.stream.Collectors.toList());
        }
    }

}

