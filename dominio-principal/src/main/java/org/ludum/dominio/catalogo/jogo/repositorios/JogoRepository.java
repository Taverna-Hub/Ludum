package org.ludum.dominio.catalogo.jogo.repositorios;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.Slug;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.util.List;

public interface JogoRepository {

    void salvar(Jogo jogo);

    Jogo obterPorId(JogoId id);

    Jogo obterPorSlug(Slug slug);

    boolean existeSlugParaDesenvolvedora(ContaId devId, Slug slug);

    List<Jogo> obterJogosPorTag(TagId tagId);
}
