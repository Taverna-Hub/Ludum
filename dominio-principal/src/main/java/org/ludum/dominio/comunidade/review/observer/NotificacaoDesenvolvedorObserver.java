package org.ludum.dominio.comunidade.review.observer;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.comunidade.review.entidades.Review;

public class NotificacaoDesenvolvedorObserver implements ReviewObserver {

    private final JogoRepository jogoRepository;

    public NotificacaoDesenvolvedorObserver(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }
    
    @Override
    public void quandoNovaReviewCriada(Review review) {
        Jogo jogo = jogoRepository.obterPorId(review.getJogoId());
        String nomeJogo = jogo != null ? jogo.getTitulo() : review.getJogoId().getValue();
        
        System.out.println("\n========================================");
        System.out.println("📢 NOTIFICAÇÃO PARA DESENVOLVEDOR");
        System.out.println("========================================");
        System.out.println("Seu jogo \"" + nomeJogo + "\" tem uma nova review!");
        System.out.println("Nota: " + review.getNota() + "/5 estrelas");
        System.out.println("Recomenda: " + (review.isRecomendado() ? "Sim ✅" : "Não ❌"));
        System.out.println("========================================\n");
    }
}
