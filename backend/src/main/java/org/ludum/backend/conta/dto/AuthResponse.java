package org.ludum.backend.conta.dto;

public class AuthResponse {
    private String token;
    private String userId;
    private String nome;

    public AuthResponse(String token, String userId, String nome) {
        this.token = token;
        this.userId = userId;
        this.nome = nome;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
