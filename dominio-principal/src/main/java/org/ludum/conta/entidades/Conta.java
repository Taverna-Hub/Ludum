package org.ludum.conta.entidades;

import org.ludum.conta.enums.StatusConta;
import org.ludum.conta.enums.TipoConta;

import java.util.Objects;

public class Conta {
    private ContaId id;
    private String nome;
    private String senhaHash;
    private TipoConta tipo;
    private StatusConta status;

    public Conta(ContaId id, String nome, String senhaHash, TipoConta tipo, StatusConta status) {
        this.id = Objects.requireNonNull(id);
        this.nome = Objects.requireNonNull(nome);
        this.senhaHash = Objects.requireNonNull(senhaHash);
        this.tipo = Objects.requireNonNull(tipo);
        this.status = Objects.requireNonNull(status);
    }

    public void publicarJogo() {
        // TODO: Implementar lógica de publicação de jogo
    }

    public ContaId getId() {
        return id;
    }

    public void setId(ContaId id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }

    public TipoConta getTipo() {
        return tipo;
    }

    public void setTipo(TipoConta tipo) {
        this.tipo = tipo;
    }

    public StatusConta getStatus() {
        return status;
    }

    public void setStatus(StatusConta status) {
        this.status = status;
    }
}
