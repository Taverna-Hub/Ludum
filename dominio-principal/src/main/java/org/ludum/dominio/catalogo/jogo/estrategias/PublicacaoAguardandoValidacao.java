package org.ludum.dominio.catalogo.jogo.estrategias;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;

import java.util.ArrayList;
import java.util.List;

public class PublicacaoAguardandoValidacao implements EstrategiaPublicacao {
    
    @Override
    public List<String> validar(Jogo jogo) {
        List<String> erros = new ArrayList<>();
        
        if (jogo.getTitulo() == null || jogo.getTitulo().isBlank()) {
            erros.add("Título não pode ser vazio");
        }
        
        if (jogo.getDescricao() == null || jogo.getDescricao().isBlank()) {
            erros.add("Descrição não pode ser vazia");
        }
        
        return erros;
    }
    
    @Override
    public void executar(Jogo jogo) {
        jogo.publicar();
    }
    
    @Override
    public boolean podeAplicar(StatusPublicacao status) {
        return status == StatusPublicacao.AGUARDANDO_VALIDACAO;
    }
}
