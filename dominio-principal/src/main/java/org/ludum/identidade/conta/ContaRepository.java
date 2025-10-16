package org.ludum.identidade.conta;

import org.ludum.identidade.conta.entidades.Conta;
import org.ludum.identidade.conta.entidades.ContaId;

public interface ContaRepository {
    void salvar(Conta conta);
    Conta obterPorId(ContaId id);
}
