package org.ludum.catalogo.jogo.repositorios;

import org.ludum.catalogo.jogo.entidades.Jogo;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.entidades.Slug;
import org.ludum.catalogo.tag.entidades.TagId;
import org.ludum.identidade.conta.entities.ContaId;

import java.util.List;

public interface JogoRepository {

    void salvar(Jogo jogo);

    Jogo obterPorId(JogoId id);

    Jogo obterPorSlug(Slug slug);

    boolean existeSlugParaDesenvolvedora(ContaId devId, Slug slug);

    List<Jogo> obterJogosPorTag(TagId tagId);
}
