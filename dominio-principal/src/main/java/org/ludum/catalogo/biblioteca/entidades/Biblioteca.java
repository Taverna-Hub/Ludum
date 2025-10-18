package org.ludum.catalogo.biblioteca.entidades;

import org.ludum.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.financeiro.carteira.entidades.ContaId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Biblioteca {

    private final ContaId contaId;
    private List<ItemBiblioteca> itens;

    public Biblioteca(ContaId contaId) {
        this.contaId = Objects.requireNonNull(contaId);
        this.itens = new ArrayList<ItemBiblioteca>();
    }

    public Optional<ItemBiblioteca> buscarJogoEmBiblioteca(JogoId jogoId){
        for(int i = 0; i < this.itens.size(); i++){
            if(itens.get(i).getJogoId().equals(jogoId)){
                return Optional.ofNullable(itens.get(i));
            }
        }
        return Optional.empty();
    }

    public void adicionarJogo(ModeloDeAcesso modeloDeAcesso, JogoId jogoId){
        if(buscarJogoEmBiblioteca(jogoId).orElse(null) != null){
            throw new IllegalArgumentException("Jogo já está presente na biblioteca");
        }
        itens.add(new ItemBiblioteca(modeloDeAcesso, jogoId));

    }

    public void removerJogo(JogoId jogoId){
        ItemBiblioteca item = buscarJogoEmBiblioteca(jogoId).orElse(null);
        if(item == null){
            throw new IllegalArgumentException("Jogo não está na biblioteca");
        }

        itens.remove(item);

    }

    public List<ItemBiblioteca> getItens() {
        return List.copyOf(itens);
    }

    public ContaId getContaId() {
        return contaId;
    }
}
