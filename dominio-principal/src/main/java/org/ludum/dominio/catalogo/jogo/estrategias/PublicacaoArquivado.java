package org.ludum.dominio.catalogo.jogo.estrategias;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;

import java.util.ArrayList;
import java.util.List;

public class PublicacaoArquivado implements EstrategiaPublicacao {
    
    @Override
    public List<String> validar(Jogo jogo) {
        List<String> erros = new ArrayList<>();
        
        erros.add("Jogo está arquivado");
        erros.add("Jogos arquivados não podem ser publicados diretamente");
        erros.add("Desarquive o jogo primeiro antes de tentar publicar");
        
        return erros;
    }
    
    @Override
    public void executar(Jogo jogo) {
        throw new IllegalStateException(
            "Operação bloqueada: jogo está arquivado"
        );
    }
    
    @Override
    public boolean podeAplicar(StatusPublicacao status) {
        return status == StatusPublicacao.ARQUIVADO;
    }
}