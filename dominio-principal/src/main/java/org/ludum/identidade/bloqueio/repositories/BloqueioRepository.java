package org.ludum.identidade.bloqueio.repositories;

import org.ludum.identidade.bloqueio.entities.Bloqueio;
import org.ludum.identidade.conta.entities.ContaId;

import java.util.Optional;

public interface BloqueioRepository {
    void salvar(Bloqueio bloqueio);
    
    void remover(Bloqueio bloqueio);
    
    Optional<Bloqueio> buscar(ContaId bloqueadorId, ContaId alvoId);
}
