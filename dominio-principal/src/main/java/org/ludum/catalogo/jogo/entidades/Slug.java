package org.ludum.catalogo.jogo.entidades;

import java.text.Normalizer;
import java.util.Objects;

public class Slug {
    private final String valor;

    public Slug(String valor) {
        this.valor = Objects.requireNonNull(valor);
        validarFormato(valor);
    }

    public static Slug criar(String titulo) {
        Objects.requireNonNull(titulo, "Título não pode ser nulo");

        String normalizado = Normalizer.normalize(titulo, Normalizer.Form.NFD);
        String semAcentos = normalizado.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        String slug = semAcentos.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        return new Slug(slug);
    }

    private void validarFormato(String slug) {
        if (!slug.matches("^[a-z0-9]+(-[a-z0-9]+)*$")) {
            throw new IllegalArgumentException(
                    "Slug inválida. Deve conter apenas letras minúsculas, números e hífens: " + slug);
        }
    }

    public String getValor() {
        return valor;
    }

}