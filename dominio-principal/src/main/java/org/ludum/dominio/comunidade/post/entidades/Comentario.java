package org.ludum.dominio.comunidade.post.entidades;

import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.time.LocalDateTime;
import java.util.Objects;

public class Comentario {
    private ComentarioId id;
    private PostId postId;
    private ContaId autorId;
    private String texto;
    private LocalDateTime data;
    private boolean oculto;

    public Comentario(ComentarioId id, PostId postId, ContaId autorId, String texto, LocalDateTime data) {
        this.id = Objects.requireNonNull(id);
        this.postId = Objects.requireNonNull(postId);
        this.autorId = Objects.requireNonNull(autorId);
        this.texto = Objects.requireNonNull(texto);
        this.data = Objects.requireNonNull(data);
        this.oculto = false;
    }

    public void editarTexto(String novoTexto) {
        Objects.requireNonNull(novoTexto, "Novo texto não pode ser nulo");

        if (novoTexto.isBlank()) {
            throw new IllegalArgumentException("Texto do comentário não pode ser vazio");
        }

        this.texto = novoTexto;
    }

    public void ocultar() {
        this.oculto = true;
    }

    public boolean isOculto() {
        return oculto;
    }

    public boolean isVisivel() {
        return !oculto;
    }

    public ComentarioId getId() {
        return id;
    }

    public PostId getPostId() {
        return postId;
    }

    public ContaId getAutorId() {
        return autorId;
    }

    public String getTexto() {
        return texto;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setId(ComentarioId id) {
        this.id = id;
    }

    public void setPostId(PostId postId) {
        this.postId = postId;
    }

    public void setAutorId(ContaId autorId) {
        this.autorId = autorId;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

}
