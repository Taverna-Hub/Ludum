package org.ludum.dominio.catalogo.jogo.estrategias;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;

import java.util.ArrayList;
import java.util.List;

public class PublicacaoEmUpload implements EstrategiaPublicacao {
    
    @Override
    public List<String> validar(Jogo jogo) {
        List<String> erros = new ArrayList<>();
        
        if (jogo.getTitulo() == null || jogo.getTitulo().isBlank()) {
            erros.add("Título é obrigatório para publicação");
        }
        
        if (jogo.getDescricao() == null || jogo.getDescricao().isBlank()) {
            erros.add("Descrição é obrigatória para publicação");
        }
        
        if (jogo.getCapaOficial() == null) {
            erros.add("Capa oficial é obrigatória para publicação");
        }
        
        if (jogo.getTags() == null || jogo.getTags().isEmpty()) {
            erros.add("Pelo menos uma tag é obrigatória para publicação");
        }
        
        if (jogo.getScreenshots() == null || jogo.getScreenshots().isEmpty()) {
            erros.add("Pelo menos uma screenshot é obrigatória para publicação");
        }
        
        return erros;
    }
    
    @Override
    public void executar(Jogo jogo) {
        jogo.aguardarValidacao();
    }
    
    @Override
    public boolean podeAplicar(StatusPublicacao status) {
        return status == StatusPublicacao.EM_UPLOAD;
    }
}
