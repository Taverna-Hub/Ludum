package org.ludum.infraestrutura.config;

import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.jogo.services.PublicacaoService;
import org.ludum.dominio.comunidade.post.repositorios.PostRepository;
import org.ludum.dominio.comunidade.post.services.PostService;
import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.OperacoesFinanceirasService;
import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;
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
    public OperacoesFinanceirasService operacoesFinanceirasService(
            TransacaoRepository transacaoRepository,
            CarteiraRepository carteiraRepository,
            ProcessadorPagamentoExterno processadorPagamento) {
        OperacoesFinanceirasService service = new OperacoesFinanceirasService(transacaoRepository, carteiraRepository);
        service.setProcessadorPagamento(processadorPagamento);
        return service;
    }
}
