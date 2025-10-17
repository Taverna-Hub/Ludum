package org.ludum.identidade.conta.repositories;

import org.ludum.identidade.conta.entities.Conta;
import org.ludum.identidade.conta.entities.ContaId;

public interface ContaRepository {
    void salvar(Conta conta);
    Conta obterPorId(ContaId id);
}
