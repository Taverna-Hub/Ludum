package org.ludum.dominio.oficina.mod.entidades;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.oficina.mod.enums.StatusMod;


public class Mod {
    
    private final String id;
    private final JogoId jogoId;
    private final ContaId autorId;
    private String nome;
    private String descricao;
    private StatusMod status;
    private final List<VersaoMod> versoes;

    public Mod(JogoId jogoId, ContaId autorId, String nome, String descricao) {
        this.id = UUID.randomUUID().toString();
        this.jogoId = Objects.requireNonNull(jogoId, "O ID do jogo não pode ser nulo.");
        this.autorId = Objects.requireNonNull(autorId, "O ID do autor não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "O nome não pode ser nulo.");
        this.descricao = Objects.requireNonNull(descricao, "A descrição não pode ser nula.");
        this.status = StatusMod.ATIVO;
        this.versoes = new ArrayList<>();
    }

    public void adicionarNovaVersao(String notas, byte[] arquivo) {
        VersaoMod novaVersao = new VersaoMod(notas, arquivo);
        this.versoes.add(novaVersao);
    }

    public void remover() {
        this.status = StatusMod.REMOVIDO;
    }

    public void atualizarDetalhes(String novoNome, String novaDescricao) {
        this.nome = Objects.requireNonNull(novoNome, "O nome não pode ser nulo.");
        this.descricao = Objects.requireNonNull(novaDescricao, "A descrição não pode ser nula.");
    }

    public String getId() {
        return id;
    }

    public JogoId getJogoId() {
        return jogoId;
    }

    public ContaId getAutorId() {
        return autorId;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public StatusMod getStatus() {
        return status;
    }

    public List<VersaoMod> getVersoes() {
        return new ArrayList<>(versoes); // Retorna cópia das versões -> Protege a lista.
    }
}
