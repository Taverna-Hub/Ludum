package org.ludum.comunidade.review.repositorios;

import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.comunidade.review.entidades.Review;
import org.ludum.comunidade.review.entidades.ReviewId;
import org.ludum.conta.entidades.ContaId;

import java.util.List;

public interface ReviewRepository {
    void salvar(Review review);

    Review obterPorId(ReviewId id);

    List<Review> obterPorJogo(JogoId jogoId, int pagina, int tamanhoPagina);

    List<Review> obterPorAutor(ContaId autorId, int pagina, int tamanhoPagina);

    void remover(Review review);
}
