package org.ludum.infraestrutura.config;

import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.jogo.services.PublicacaoService;
import org.ludum.dominio.comunidade.post.repositorios.PostRepository;
import org.ludum.dominio.comunidade.post.services.PostService;
import org.ludum.dominio.catalogo.biblioteca.repositorios.BibliotecaRepository;
import org.ludum.dominio.catalogo.tag.TagRepository;
import org.ludum.dominio.comunidade.review.repositorios.ReviewRepository;
import org.ludum.dominio.comunidade.review.services.ReviewService;
import org.ludum.dominio.comunidade.review.observer.NotificacaoDesenvolvedorObserver;
import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.OperacoesFinanceirasService;
import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.identidade.bloqueio.repositories.BloqueioRepository;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;
import org.ludum.dominio.identidade.seguimento.repositories.SeguimentoRepository;
import org.ludum.dominio.identidade.seguimento.services.RelacionamentoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DominioConfig {

    @Bean
    public PublicacaoService publicacaoService(
            JogoRepository jogoRepository,
            ContaRepository contaRepository) {
        return new PublicacaoService(jogoRepository, contaRepository);
    }

    @Bean
    public PostService postService(PostRepository postRepository) {
        return new PostService(postRepository);
    }

    @Bean
    public org.ludum.dominio.catalogo.biblioteca.services.BibliotecaService bibliotecaService(
            BibliotecaRepository bibliotecaRepository,
            TransacaoRepository transacaoRepository,
            JogoRepository jogoRepository,
            ContaRepository contaRepository) {
        return new org.ludum.dominio.catalogo.biblioteca.services.BibliotecaService(bibliotecaRepository,
                transacaoRepository, jogoRepository, contaRepository);
    }

    @Bean
    public ReviewService reviewService(
            ReviewRepository reviewRepository,
            JogoRepository jogoRepository,
            BibliotecaRepository bibliotecaRepository) {
        ReviewService service = new ReviewService(reviewRepository, jogoRepository, bibliotecaRepository);

        // Registrar observer para notificar desenvolvedores sobre novas reviews
        service.adicionarObservador(new NotificacaoDesenvolvedorObserver(jogoRepository));

        return service;
    }

    @Bean
    public RelacionamentoService relacionamentoService(
            SeguimentoRepository seguimentoRepository,
            ContaRepository contaRepository,
            BloqueioRepository bloqueioRepository,
            JogoRepository jogoRepository,
            TagRepository tagRepository) {
        return new RelacionamentoService(seguimentoRepository, contaRepository, bloqueioRepository, jogoRepository,
                tagRepository);
    }

    @Bean
    public OperacoesFinanceirasService operacoesFinanceirasService(
            TransacaoRepository transacaoRepository,
            CarteiraRepository carteiraRepository,
            ProcessadorPagamentoExterno processadorPagamento) {
        OperacoesFinanceirasService service = new OperacoesFinanceirasService(transacaoRepository, carteiraRepository);
        service.setProcessadorPagamento(processadorPagamento);
        return service;
    }
}
