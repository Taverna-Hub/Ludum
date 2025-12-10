package org.ludum.dominio.catalogo.jogo.estrategias;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;

import java.util.ArrayList;
import java.util.List;

public class PublicacaoPublicado implements EstrategiaPublicacao {
    
    @Override
    public List<String> validar(Jogo jogo) {
        List<String> erros = new ArrayList<>();
        
        erros.add("Jogo já está publicado na plataforma");
        erros.add("Não é possível publicar novamente um jogo que já está público");
        erros.add("Se deseja atualizar o jogo, use a funcionalidade de atualização");
        
        return erros;
    }
    
    @Override
    public void executar(Jogo jogo) {
        throw new IllegalStateException(
            "Não é possível publicar um jogo que já está publicado. " +
            "Status atual: " + jogo.getStatus()
        );
    }
    
    @Override
    public boolean podeAplicar(StatusPublicacao status) {
        return status == StatusPublicacao.PUBLICADO;
    }
}