package org.ludum.comunidade.post.services;

import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.tag.entidades.Tag;

import java.util.List;

/**
 * Interface para consultar informações de jogos necessárias no contexto de
 * Post.
 * Evita dependência direta do PostService com JogoRepository.
 */
public interface JogoInfo {

    List<Tag> obterTagsDoJogo(JogoId jogoId);

    boolean isJogoAdulto(JogoId jogoId);
}
