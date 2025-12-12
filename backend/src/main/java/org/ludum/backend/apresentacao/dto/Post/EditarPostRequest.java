package org.ludum.backend.apresentacao.dto.Post;

public class EditarPostRequest {

    private String titulo;
    private String conteudo;

    public EditarPostRequest() {
    }

    public EditarPostRequest(String titulo, String conteudo) {
        this.titulo = titulo;
        this.conteudo = conteudo;
    }

    // Getters e Setters
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
}
