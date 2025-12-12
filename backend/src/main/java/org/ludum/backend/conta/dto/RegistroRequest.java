package org.ludum.backend.conta.dto;

import org.ludum.dominio.identidade.conta.enums.TipoConta;

public class RegistroRequest {
    private String nome;
    private String senha;
    private TipoConta tipo;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    public void setTipo(TipoConta tipo) {
        this.tipo = tipo;
    }
}
