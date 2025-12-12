package org.ludum.dominio.identidade.conta.repositories;

import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;

public interface ContaRepository {
    void salvar(Conta conta);
    Conta obterPorId(ContaId id);
    Conta obterPorNome(String nome);
}
