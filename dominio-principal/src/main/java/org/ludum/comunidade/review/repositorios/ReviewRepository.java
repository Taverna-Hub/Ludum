package org.ludum.comunidade.review.repositorios;

import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.comunidade.review.entidades.Review;
import org.ludum.comunidade.review.entidades.ReviewId;
import org.ludum.identidade.conta.entities.ContaId;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository {
    void salvar(Review review);

    Review obterPorId(ReviewId id);

    List<Review> obterPorJogo(JogoId jogoId, int pagina, int tamanhoPagina);

    List<Review> obterPorAutor(ContaId autorId, int pagina, int tamanhoPagina);

    void remover(Review review);

    Optional<Review> obterPorAutorEJogo(ContaId autorId, JogoId jogoId);
}
