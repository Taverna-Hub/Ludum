package org.ludum.dominio.comunidade.post.services;

import java.net.URL;

/**
 * Interface para validação de conteúdo adulto em imagens.
 * Permite diferentes implementações (ex: APIs de IA, filtros, etc).
 */
public interface ConteudoAdultoValidator {

    boolean contemConteudoAdulto(URL imagem);
}
