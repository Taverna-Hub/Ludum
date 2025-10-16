package org.ludum.seguimento;

import org.ludum.conta.entidades.ContaId;
import org.ludum.seguimento.entidades.AlvoId;
import org.ludum.seguimento.entidades.Seguimento;

import java.util.List;
import java.util.Optional;

public interface SeguimentoRepository {

    void salvar(Seguimento seguimento);

    void remover(Seguimento seguimento);

    Optional<Seguimento> obter(ContaId seguidorId, AlvoId seguidoId);

    List<Seguimento> obterSeguidoresDe(AlvoId seguidoId);

    List<Seguimento> obterSeguidosPor(ContaId seguidorId);
}
