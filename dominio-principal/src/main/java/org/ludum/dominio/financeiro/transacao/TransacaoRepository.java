package org.ludum.dominio.financeiro.transacao;

import org.ludum.dominio.financeiro.transacao.entidades.Recibo;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.util.List;

public interface TransacaoRepository {
    Transacao obterPorId(TransacaoId id);
    List<Transacao> obterPorContaId(ContaId contaId);
    void salvarRecibo(Recibo recibo);
    void salvar(Transacao transacao);
}
