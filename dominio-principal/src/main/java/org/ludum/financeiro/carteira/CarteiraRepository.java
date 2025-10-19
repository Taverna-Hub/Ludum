package org.ludum.financeiro.carteira;

import org.ludum.financeiro.carteira.entidades.Carteira;
import org.ludum.financeiro.carteira.entidades.ContaId;

public interface CarteiraRepository {
    Carteira obterPorContaId(ContaId contaId);
    void salvar(Carteira carteira);
}
