package org.ludum.dominio.comunidade.review.observer;

import org.ludum.dominio.comunidade.review.entidades.Review;

public class NotificacaoDesenvolvedorObserver implements ReviewObserver {
    
    @Override
    public void quandoNovaReviewCriada(Review review) {
        // Simulação de notificação ao desenvolvedor
        System.out.println("NOTIFICAÇÃO: O jogo " + review.getJogoId().getValue() +
                         " recebeu uma nova avaliação nota " + review.getNota() + 
                         " de " + review.getAutorId().getValue());
    }
}
