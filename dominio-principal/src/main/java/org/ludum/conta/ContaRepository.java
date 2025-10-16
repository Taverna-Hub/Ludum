package org.ludum.conta;

import org.ludum.conta.entidades.Conta;

public interface ContaRepository {
    void salvar(Conta conta);
    Conta obterPorId(int id);
}
