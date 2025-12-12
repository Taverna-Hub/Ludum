package org.ludum.dominio.catalogo.biblioteca.entidades;

import org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.dominio.catalogo.biblioteca.estruturas.Celula;
import org.ludum.dominio.catalogo.biblioteca.estruturas.IteratorBiblioteca;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Biblioteca {

    private final ContaId contaId;
    private Celula<ItemBiblioteca> primeira;
    private List<ItemBiblioteca> itensBaixados;

    public Biblioteca(ContaId contaId) {
        this.contaId = Objects.requireNonNull(contaId);
        this.primeira = null;
        this.itensBaixados = new ArrayList<>();
    }

    public IteratorBiblioteca<ItemBiblioteca> criarIterator() {
        return new IteratorBiblioteca<>(this.primeira, novaCabeca -> this.primeira = novaCabeca);
    }

    public Optional<ItemBiblioteca> buscarJogoEmBiblioteca(JogoId jogoId) {
        IteratorBiblioteca<ItemBiblioteca> iterator = criarIterator();
        while (iterator.existeProximo()) {
            ItemBiblioteca item = iterator.proximo();
            if (item.getJogoId().equals(jogoId)) {
                return Optional.of(item);
            }
        }
        return Optional.empty();
    }

    public void adicionarJogo(ModeloDeAcesso modeloDeAcesso, JogoId jogoId) {
        if (buscarJogoEmBiblioteca(jogoId).isPresent()) {
            throw new IllegalArgumentException("Jogo já está presente na biblioteca");
        }
        ItemBiblioteca novoItem = new ItemBiblioteca(modeloDeAcesso, jogoId);
        if (this.primeira == null) {
            this.primeira = new Celula<>(novoItem);
        } else {
            Celula<ItemBiblioteca> atual = this.primeira;
            while (atual.getProxima() != null) {
                atual = atual.getProxima();
            }
            atual.setProxima(new Celula<>(novoItem));
        }
    }

    public void removerJogo(JogoId jogoId) {
        IteratorBiblioteca<ItemBiblioteca> iterator = criarIterator();
        while (iterator.existeProximo()) {
            ItemBiblioteca item = iterator.proximo();
            if (item.getJogoId().equals(jogoId)) {
                iterator.remover();
                return;
            }
        }
        throw new IllegalArgumentException("Jogo não está na biblioteca");
    }

    public void baixouJogo(ItemBiblioteca item) {
        if (buscarJogoEmBiblioteca(item.getJogoId()).isEmpty()) {
            throw new IllegalStateException("Jogo não presente na biblioteca");
        }
        itensBaixados.add(item);
    }

    public List<ItemBiblioteca> getItens() {
        List<ItemBiblioteca> lista = new ArrayList<>();
        IteratorBiblioteca<ItemBiblioteca> iterator = criarIterator();
        while (iterator.existeProximo()) {
            lista.add(iterator.proximo());
        }
        return lista;
    }

    public List<ItemBiblioteca> getItensBaixados() {
        return List.copyOf(itensBaixados);
    }

    public ContaId getContaId() {
        return contaId;
    }
}
