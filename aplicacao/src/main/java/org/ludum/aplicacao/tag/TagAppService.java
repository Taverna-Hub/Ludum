package org.ludum.aplicacao.tag;

import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.catalogo.tag.TagRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TagAppService {

    private final TagRepository tagRepository;

    public TagAppService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public List<Tag> obterTagsPorIds(List<String> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            throw new IllegalArgumentException("Lista de tag IDs não pode ser vazia");
        }

        return tagIds.stream()
                .map(id -> {
                    Tag tag = tagRepository.obterPorId(new TagId(id));
                    if (tag == null) {
                        throw new IllegalArgumentException("Tag não encontrada: " + id);
                    }
                    return tag;
                })
                .collect(Collectors.toList());
    }


    public Tag obterTagPorNome(String nome) {
        Tag tag = tagRepository.obterPorNome(nome);
        if (tag == null) {
            throw new IllegalArgumentException("Tag não encontrada: " + nome);
        }
        return tag;
    }


    public Tag obterTagPorId(String id) {
        Tag tag = tagRepository.obterPorId(new TagId(id));
        if (tag == null) {
            throw new IllegalArgumentException("Tag não encontrada: " + id);
        }
        return tag;
    }
}
