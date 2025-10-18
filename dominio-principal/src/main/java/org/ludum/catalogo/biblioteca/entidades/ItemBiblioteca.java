package org.ludum.catalogo.biblioteca.entidades;


import org.ludum.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.catalogo.jogo.entidades.JogoId;

import java.time.LocalDateTime;
import java.util.Objects;

public class ItemBiblioteca {

    private final JogoId jogoId;
    private final LocalDateTime dataAdicao;
    private final ModeloDeAcesso modeloDeAcesso;

    public ItemBiblioteca(ModeloDeAcesso modeloDeAcesso, JogoId jogoId) {
        this.modeloDeAcesso = Objects.requireNonNull(modeloDeAcesso);
        this.jogoId = Objects.requireNonNull(jogoId);
        this.dataAdicao = LocalDateTime.now();
    }

    @Override
    public int hashCode() {
        return Objects.hash(jogoId, modeloDeAcesso);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemBiblioteca that = (ItemBiblioteca) o;
        return Objects.equals(jogoId, that.jogoId) && Objects.equals(modeloDeAcesso, that.modeloDeAcesso);
    }
    public LocalDateTime getDataAdicao() {
        return dataAdicao;
    }

    public ModeloDeAcesso getModeloDeAcesso() {
        return modeloDeAcesso;
    }

    public JogoId getJogoId() {
        return jogoId;
    }
}
