package org.ludum.backend.apresentacao.dto;

import org.ludum.dominio.comunidade.review.entidades.Review;
import org.ludum.dominio.comunidade.review.enums.StatusReview;

import java.util.Date;

public class ReviewResponse {
    private String id;
    private String jogoId;
    private String autorId;
    private String autorNome;
    private int nota;
    private String titulo;
    private String texto;
    private Date data;
    private Date dataUltimaEdicao;
    private boolean isRecomendado;
    private StatusReview status;

    public ReviewResponse() {}

    public static ReviewResponse fromReview(Review review, String autorNome) {
        ReviewResponse response = new ReviewResponse();
        response.id = review.getId().getValue();
        response.jogoId = review.getJogoId().getValue();
        response.autorId = review.getAutorId().getValue();
        response.autorNome = autorNome;
        response.nota = review.getNota();
        response.titulo = review.getTitulo();
        response.texto = review.getTexto();
        response.data = review.getData();
        response.dataUltimaEdicao = review.getDataUltimaEdicao();
        response.isRecomendado = review.isRecomendado();
        response.status = review.getStatus();
        return response;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJogoId() {
        return jogoId;
    }

    public void setJogoId(String jogoId) {
        this.jogoId = jogoId;
    }

    public String getAutorId() {
        return autorId;
    }

    public void setAutorId(String autorId) {
        this.autorId = autorId;
    }

    public String getAutorNome() {
        return autorNome;
    }

    public void setAutorNome(String autorNome) {
        this.autorNome = autorNome;
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

    public Date getDataUltimaEdicao() {
        return dataUltimaEdicao;
    }

    public void setDataUltimaEdicao(Date dataUltimaEdicao) {
        this.dataUltimaEdicao = dataUltimaEdicao;
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
