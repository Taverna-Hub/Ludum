package org.ludum.dominio.financeiro.carteira;

import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.identidade.conta.entities.ContaId;

public interface CarteiraRepository {
    Carteira obterPorContaId(ContaId contaId);
    void salvar(Carteira carteira);
}
