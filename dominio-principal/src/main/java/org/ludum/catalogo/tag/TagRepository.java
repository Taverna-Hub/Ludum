package org.ludum.catalogo.tag;

import org.ludum.catalogo.tag.entidades.Tag;
import org.ludum.catalogo.tag.entidades.TagId;

import java.util.List;

public interface TagRepository {

    Tag obterPorNome(String nome);

    void salvar(Tag tag);

    Tag obterPorId(TagId id);

    List<Tag> obterTodas();

    void remover(Tag tag);
}
