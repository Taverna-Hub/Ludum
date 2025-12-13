package org.ludum.aplicacao.tag;

import org.ludum.dominio.catalogo.tag.entidades.Tag;

/**
 * DTO para resumo de tag na camada de aplicação.
 */
public class TagResumo {
    private String id;
    private String nome;

    public TagResumo() {
    }

    public TagResumo(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static TagResumo fromTag(Tag tag) {
        return new TagResumo(
            tag.getId().getValue(),
            tag.getNome()
        );
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
