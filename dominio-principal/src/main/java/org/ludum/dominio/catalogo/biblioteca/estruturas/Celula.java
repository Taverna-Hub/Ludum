package org.ludum.dominio.catalogo.biblioteca.estruturas;

public class Celula<T> {
    private T conteudo;
    private Celula<T> proxima;

    public Celula(T conteudo) {
        this.conteudo = conteudo;
        this.proxima = null;
    }

    public T getConteudo() {
        return conteudo;
    }

    public void setConteudo(T conteudo) {
        this.conteudo = conteudo;
    }

    public Celula<T> getProxima() {
        return proxima;
    }

    public void setProxima(Celula<T> proxima) {
        this.proxima = proxima;
    }
}
