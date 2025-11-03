package org.ludum.dominio.financeiro.transacao;

import org.ludum.dominio.financeiro.transacao.entidades.Recibo;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;

public interface TransacaoRepository {
    Transacao obterPorId(TransacaoId id);
//     List<Transacao> obterHistoricoPorConta(ContaId contaId, DatePeriodo periodo);
    void salvarRecibo(Recibo recibo);
    void salvar(Transacao transacao);
}
