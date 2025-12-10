package org.ludum.dominio.catalogo.jogo.estrategias;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;

import java.util.ArrayList;
import java.util.List;

public class PublicacaoRejeitado implements EstrategiaPublicacao {
    
    @Override
    public List<String> validar(Jogo jogo) {
        List<String> erros = new ArrayList<>();
        
        erros.add("Jogo foi rejeitado pela equipe de validação");
        erros.add("Corrija os problemas identificados antes de reenviar para publicação");
        erros.add("Use a funcionalidade de reenvio após as correções");
        
        return erros;
    }
    
    @Override
    public void executar(Jogo jogo) {
        throw new IllegalStateException(
            "Não é possível publicar um jogo rejeitado. " +
            "Corrija os problemas e reenvie para validação."
        );
    }
    
    @Override
    public boolean podeAplicar(StatusPublicacao status) {
        return status == StatusPublicacao.REJEITADO;
    }
}
