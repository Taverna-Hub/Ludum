package org.ludum.comunidade.post.entidades;

import org.ludum.identidade.conta.entities.ContaId;

import java.util.Objects;

public class Curtida {
    private final PostId postId;
    private final ContaId contaId;

    public Curtida(PostId postId, ContaId contaId) {
        this.postId = Objects.requireNonNull(postId, "PostId não pode ser nulo");
        this.contaId = Objects.requireNonNull(contaId, "ContaId não pode ser nulo");
    }

    // Getters apenas (imutável)
    public PostId getPostId() {
        return postId;
    }

    public ContaId getContaId() {
        return contaId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        Curtida curtida = (Curtida) obj;
        return Objects.equals(postId, curtida.postId) &&
                Objects.equals(contaId, curtida.contaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, contaId);
    }

    @Override
    public String toString() {
        return "Curtida{" +
                "postId=" + postId +
                ", contaId=" + contaId +
                '}';
    }
}