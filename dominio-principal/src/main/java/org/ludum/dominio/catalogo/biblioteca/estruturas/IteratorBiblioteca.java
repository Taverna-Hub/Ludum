package org.ludum.dominio.catalogo.biblioteca.estruturas;

import java.util.function.Consumer;

public class IteratorBiblioteca<T> {
    private Celula<T> celulaAtual;
    private Celula<T> celulaAnterior;
    private Celula<T> ultimoRetornado;
    private Celula<T> anteriorAoUltimo;
    private Consumer<Celula<T>> gerenciadorDeCabeca;

    public IteratorBiblioteca(Celula<T> primeira, Consumer<Celula<T>> gerenciadorDeCabeca) {
        this.celulaAtual = primeira;
        this.celulaAnterior = null;
        this.ultimoRetornado = null;
        this.anteriorAoUltimo = null;
        this.gerenciadorDeCabeca = gerenciadorDeCabeca;
    }

    public boolean existeProximo() {
        return celulaAtual != null;
    }

    public T proximo() {
        if (!existeProximo()) {
            throw new IllegalStateException("Não há próximo elemento");
        }
        T conteudo = celulaAtual.getConteudo();

        anteriorAoUltimo = celulaAnterior;
        ultimoRetornado = celulaAtual;

        celulaAnterior = celulaAtual;
        celulaAtual = celulaAtual.getProxima();

        return conteudo;
    }

    public void remover() {
        if (ultimoRetornado == null) {
            throw new IllegalStateException(
                    "Não é possível remover: proximo() não foi chamado ou elemento já removido");
        }

        if (anteriorAoUltimo == null) {
            gerenciadorDeCabeca.accept(celulaAtual);
        } else {
            anteriorAoUltimo.setProxima(celulaAtual);
        }

        celulaAnterior = anteriorAoUltimo;
        ultimoRetornado = null;
    }
}
