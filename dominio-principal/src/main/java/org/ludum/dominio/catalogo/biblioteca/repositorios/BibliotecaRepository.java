package org.ludum.dominio.catalogo.biblioteca.repositorios;

import org.ludum.dominio.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.dominio.identidade.conta.entities.ContaId;

public interface BibliotecaRepository {
    Biblioteca obterPorJogador(ContaId contaId);
    void salvar(Biblioteca biblioteca);
}
