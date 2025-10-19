package org.ludum.catalogo.biblioteca.repositorios;

import org.ludum.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.identidade.conta.entities.ContaId;

public interface BibliotecaRepository {
    Biblioteca obterPorJogador(ContaId contaId);
    void salvar(Biblioteca biblioteca);
}
