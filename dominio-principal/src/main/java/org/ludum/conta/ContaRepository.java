package org.ludum.conta;

import org.ludum.conta.entidades.Conta;
import org.ludum.conta.entidades.ContaId;

public interface ContaRepository {
    void salvar(Conta conta);
    Conta obterPorId(ContaId id);
}
