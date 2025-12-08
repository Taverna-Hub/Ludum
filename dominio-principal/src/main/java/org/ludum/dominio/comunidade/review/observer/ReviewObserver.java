package org.ludum.dominio.comunidade.review.observer;

import org.ludum.dominio.comunidade.review.entidades.Review;

public interface ReviewObserver {
    void quandoNovaReviewCriada(Review review);
}
