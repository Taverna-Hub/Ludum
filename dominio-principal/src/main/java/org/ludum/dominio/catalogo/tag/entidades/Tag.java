package org.ludum.dominio.catalogo.tag.entidades;

import java.util.Objects;

public class Tag {
    private TagId id;
    private String nome;

    public Tag(TagId id, String nome) {
        this.id = Objects.requireNonNull(id, "TagId não pode ser nulo");
        this.nome = Objects.requireNonNull(nome, "Nome da tag não pode ser nulo");

        if (nome.isBlank()) {
            throw new IllegalArgumentException("Nome da tag não pode ser vazio");
        }

        validarNome(nome);
    }

    private void validarNome(String nome) {
        if (nome.length() < 2) {
            throw new IllegalArgumentException("Nome da tag deve ter pelo menos 2 caracteres");
        }
        if (nome.length() > 30) {
            throw new IllegalArgumentException("Nome da tag não pode exceder 30 caracteres");
        }
    }

    // Getters
    public TagId getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    // Setters
    public void setNome(String nome) {
        Objects.requireNonNull(nome, "Nome da tag não pode ser nulo");
        validarNome(nome);
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}
