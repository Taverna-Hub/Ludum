package org.ludum.dominio.identidade.bloqueio.repositories;

import org.ludum.dominio.identidade.bloqueio.entities.Bloqueio;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.util.Optional;

public interface BloqueioRepository {
    void salvar(Bloqueio bloqueio);
    
    void remover(Bloqueio bloqueio);
    
    Optional<Bloqueio> buscar(ContaId bloqueadorId, ContaId alvoId);
}
