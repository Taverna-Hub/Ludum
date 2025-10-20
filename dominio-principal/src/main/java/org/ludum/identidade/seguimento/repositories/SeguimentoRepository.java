package org.ludum.identidade.seguimento.repositories;

import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.identidade.seguimento.entities.AlvoId;
import org.ludum.identidade.seguimento.entities.Seguimento;

import java.util.List;
import java.util.Optional;

public interface SeguimentoRepository {

    void salvar(Seguimento seguimento);

    void remover(Seguimento seguimento);

    Optional<Seguimento> obter(ContaId seguidorId, AlvoId seguidoId);

    List<Seguimento> obterSeguidoresDe(AlvoId seguidoId);

    List<Seguimento> obterSeguidosPor(ContaId seguidorId);
}
