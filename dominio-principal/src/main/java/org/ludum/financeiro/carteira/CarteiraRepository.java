package org.ludum.financeiro.carteira;

import org.ludum.financeiro.carteira.entidades.Carteira;
import org.ludum.identidade.conta.entities.ContaId;

public interface CarteiraRepository {
    Carteira obterPorContaId(ContaId contaId);
    void salvar(Carteira carteira);
}
