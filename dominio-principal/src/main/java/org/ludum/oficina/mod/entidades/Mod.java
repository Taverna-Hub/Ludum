package org.ludum.oficina.mod.entidades;

import org.ludum.oficina.mod.enums.StatusMod;
import java.util.UUID;

public class Mod {
    private final String id;
    private String nome;
    private String descricao;
    private String autorId;
    private StatusMod status;

    public Mod(String nome, String descricao, String autorId) {
        this.id = UUID.randomUUID().toString();
        this.nome = nome;
        this.descricao = descricao;
        this.autorId = autorId;
        this.status = StatusMod.ATIVO;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getAutorId() {
        return autorId;
    }

    public StatusMod getStatus() {
        return status;
    }

    public void remover() {
        this.status = StatusMod.REMOVIDO;
    }

    public void atualizarDescricao(String novaDescricao) {
        this.descricao = novaDescricao;
    }
}
