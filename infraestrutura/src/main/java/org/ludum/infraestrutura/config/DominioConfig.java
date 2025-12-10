package org.ludum.infraestrutura.config;

import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.jogo.services.PublicacaoService;
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
}
