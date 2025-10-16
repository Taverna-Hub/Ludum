package org.ludum.identidade.seguimento;

import org.ludum.identidade.conta.entidades.ContaId;
import org.ludum.identidade.seguimento.entidades.AlvoId;
import org.ludum.identidade.seguimento.entidades.Seguimento;

import java.util.List;
import java.util.Optional;

public interface SeguimentoRepository {

    void salvar(Seguimento seguimento);

    void remover(Seguimento seguimento);

    Optional<Seguimento> obter(ContaId seguidorId, AlvoId seguidoId);

    List<Seguimento> obterSeguidoresDe(AlvoId seguidoId);

    List<Seguimento> obterSeguidosPor(ContaId seguidorId);
}
