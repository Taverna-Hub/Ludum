package org.ludum.dominio.catalogo.jogo.estrategias;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;

import java.util.ArrayList;
import java.util.List;

public class PublicacaoRejeitado implements EstrategiaPublicacao {
    
    @Override
    public List<String> validar(Jogo jogo) {
        List<String> erros = new ArrayList<>();
        
        if (jogo.getStatus() != StatusPublicacao.AGUARDANDO_VALIDACAO) {
            erros.add("Apenas jogos aguardando validação podem ser rejeitados");
            erros.add("Status atual: " + jogo.getStatus());
        }
        
        return erros;
    }
    
    @Override
    public void executar(Jogo jogo) {
        jogo.rejeitar();
    }
    
    @Override
    public boolean podeAplicar(StatusPublicacao status) {
        return status == StatusPublicacao.AGUARDANDO_VALIDACAO;
    }
}
