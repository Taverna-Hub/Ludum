package org.ludum.backend.apresentacao.dto.Post;

public class ComentarPostRequest {

    private String autorId;
    private String texto;

    public ComentarPostRequest() {
    }

    public ComentarPostRequest(String autorId, String texto) {
        this.autorId = autorId;
        this.texto = texto;
    }

    // Getters e Setters
    public String getAutorId() {
        return autorId;
    }

    public void setAutorId(String autorId) {
        this.autorId = autorId;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
