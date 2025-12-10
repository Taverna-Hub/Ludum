package org.ludum.aplicacao.catalogo;

import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.services.PublicacaoService;
import org.ludum.dominio.catalogo.jogo.services.ResultadoPublicacao;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.stereotype.Service;

@Service
public class PublicarJogoUseCase {
    
    private final PublicacaoService publicacaoService;
    
    public PublicarJogoUseCase(PublicacaoService publicacaoService) {
        this.publicacaoService = publicacaoService;
    }
    
    public ResultadoPublicacao executar(ContaId devId, JogoId jogoId) {
        return publicacaoService.publicarJogoComStrategy(devId, jogoId);
    }
    
    public ResultadoPublicacao validar(JogoId jogoId) {
        return publicacaoService.validarPublicacao(jogoId);
    }
}