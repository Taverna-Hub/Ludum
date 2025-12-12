package org.ludum.backend.apresentacao.dto.Post;

import java.util.List;

public class CriarPostRequest {

    private String jogoId;
    private String autorId;
    private String titulo;
    private String conteudo;
    private String imagemUrl;
    private List<String> tagIds;

    public CriarPostRequest() {
    }

    public CriarPostRequest(String jogoId, String autorId, String titulo,
            String conteudo, String imagemUrl, List<String> tagIds) {
        this.jogoId = jogoId;
        this.autorId = autorId;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.imagemUrl = imagemUrl;
        this.tagIds = tagIds;
    }

    // Getters e Setters
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

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public List<String> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<String> tagIds) {
        this.tagIds = tagIds;
    }
}
