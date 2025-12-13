package org.ludum.aplicacao.identidade.conta;

import org.ludum.dominio.identidade.conta.entities.Conta;

/**
 * DTO para resumo de desenvolvedora na camada de aplicação.
 */
public class DesenvolvedoraResumo {
    private String id;
    private String nome;

    public DesenvolvedoraResumo() {
    }

    public DesenvolvedoraResumo(String id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public static DesenvolvedoraResumo fromConta(Conta conta) {
        return new DesenvolvedoraResumo(
            conta.getId().getValue(),
            conta.getNome()
        );
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
