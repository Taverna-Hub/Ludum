package org.ludum.backend.apresentacao.dto.Post;

import org.ludum.dominio.comunidade.post.enums.PostStatus;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponse {

    private String id;
    private String jogoId;
    private String autorId;
    private String titulo;
    private String conteudo;
    private LocalDateTime dataPublicacao;
    private LocalDateTime dataAgendamento;
    private String imagemUrl;
    private PostStatus status;
    private List<String> tagIds;
    private int numeroCurtidas;
    private int numeroComentarios;

    public PostResponse() {
    }

    public PostResponse(String id, String jogoId, String autorId, String titulo,
            String conteudo, LocalDateTime dataPublicacao,
            LocalDateTime dataAgendamento, String imagemUrl,
            PostStatus status, List<String> tagIds,
            int numeroCurtidas, int numeroComentarios) {
        this.id = id;
        this.jogoId = jogoId;
        this.autorId = autorId;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.dataPublicacao = dataPublicacao;
        this.dataAgendamento = dataAgendamento;
        this.imagemUrl = imagemUrl;
        this.status = status;
        this.tagIds = tagIds;
        this.numeroCurtidas = numeroCurtidas;
        this.numeroComentarios = numeroComentarios;
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public List<String> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<String> tagIds) {
        this.tagIds = tagIds;
    }

    public int getNumeroCurtidas() {
        return numeroCurtidas;
    }

    public void setNumeroCurtidas(int numeroCurtidas) {
        this.numeroCurtidas = numeroCurtidas;
    }

    public int getNumeroComentarios() {
        return numeroComentarios;
    }

    public void setNumeroComentarios(int numeroComentarios) {
        this.numeroComentarios = numeroComentarios;
    }
}
