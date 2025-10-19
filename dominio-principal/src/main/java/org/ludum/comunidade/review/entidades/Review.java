package org.ludum.comunidade.review.entidades;

import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.comunidade.review.enums.StatusReview;
import org.ludum.identidade.conta.entities.ContaId;

import java.util.Date;
import java.util.Objects;

public class Review {
    private ReviewId id;
    private JogoId jogoId;
    private ContaId autorId;
    private int nota;
    private String titulo;
    private String texto;
    private Date data;
    private boolean isRecomendado;
    private StatusReview status;

    public Review(ReviewId id, JogoId jogoId, ContaId autorId, int nota, String titulo, String texto, Date data, boolean isRecomendado, StatusReview status) {
        this.id = Objects.requireNonNull(id);
        this.jogoId = Objects.requireNonNull(jogoId);
        this.autorId = Objects.requireNonNull(autorId);
        validarNota(nota);
        this.nota = nota;
        this.titulo = Objects.requireNonNull(titulo);
        this.texto = Objects.requireNonNull(texto);
        this.data = Objects.requireNonNull(data);
        this.isRecomendado = isRecomendado;
        this.status = Objects.requireNonNull(status);
    }

    private void validarNota(int nota) {
        if (nota < 0 || nota > 5) {
            throw new IllegalArgumentException("A nota deve estar entre 0 e 5.");
        }
    }

    public void editarConteudo(String novoTitulo, String novoTexto) {
        // TODO: Implementar lógica de edição de conteúdo
    }

    public void ajustarNota(int novaNota) {
        // TODO: Implementar lógica de ajuste de nota
    }

    public void marcarComoInapropriado() {
        // TODO: Implementar lógica de marcação como inapropriado
    }

    public ReviewId getId() {
        return id;
    }

    public void setId(ReviewId id) {
        this.id = id;
    }

    public JogoId getJogoId() {
        return jogoId;
    }

    public void setJogoId(JogoId jogoId) {
        this.jogoId = jogoId;
    }

    public ContaId getAutorId() {
        return autorId;
    }

    public void setAutorId(ContaId autorId) {
        this.autorId = autorId;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public boolean isRecomendado() {
        return isRecomendado;
    }

    public void setRecomendado(boolean recomendado) {
        isRecomendado = recomendado;
    }

    public StatusReview getStatus() {
        return status;
    }

    public void setStatus(StatusReview status) {
        this.status = status;
    }
}
