package org.ludum.dominio.catalogo.jogo.estrategias;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;

import java.util.List;


public interface EstrategiaPublicacao {
    
    List<String> validar(Jogo jogo);
    
    void executar(Jogo jogo);
    
    boolean podeAplicar(StatusPublicacao status);
}
