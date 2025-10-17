package org.ludum.financeiro.transacao;

import java.util.List;

import org.ludum.financeiro.carteira.entidades.ContaId;
import org.ludum.financeiro.transacao.entidades.Recibo;
import org.ludum.financeiro.transacao.entidades.Transacao;
import org.ludum.financeiro.transacao.entidades.TransacaoId;

public interface TransacaoRepository {
    Transacao obterPorId(TransacaoId id);
    // List<Transacao> obterHistoricoPorConta(ContaId contaId, DatePeriodo periodo);
    void salvarRecibo(Recibo recibo);
    void salvar(Transacao transacao);
}
