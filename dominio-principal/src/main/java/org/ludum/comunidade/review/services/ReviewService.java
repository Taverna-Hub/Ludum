package org.ludum.comunidade.review.services;

import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.comunidade.review.entidades.ReviewId;
import org.ludum.comunidade.review.enums.StatusReview;
import org.ludum.comunidade.review.repositorios.ReviewRepository;
import org.ludum.conta.entidades.ContaId;

public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void avaliarJogo(JogoId jogoId, ContaId autorId, int nota, String titulo, String texto, boolean recomenda) {
        // TODO: Implementar lógica de avaliação de jogo
    }

    public void editarAvaliacao(ReviewId reviewId, ContaId autorId, String novoTitulo, String novoTexto, int novaNota) {
        // TODO: Implementar lógica de edição de avaliação
    }

}
