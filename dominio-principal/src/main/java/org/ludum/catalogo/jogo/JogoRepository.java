package org.ludum.catalogo.jogo;

import org.ludum.catalogo.jogo.entidades.Jogo;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.entidades.Slug;
import org.ludum.identidade.conta.entidades.ContaId;

public interface JogoRepository {

    void salvar(Jogo jogo);

    Jogo obterPorId(JogoId id);

    Jogo obterPorSlug(Slug slug);

    boolean existeSlugParaDesenvolvedora(ContaId devId, Slug slug);
}
