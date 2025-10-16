package org.ludum.bloqueio;

import org.ludum.bloqueio.entidades.Bloqueio;
import org.ludum.bloqueio.entidades.BloqueioId;
import org.ludum.conta.entidades.ContaId;

import java.util.Optional;

public interface BloqueioRepository {
    void salvar(Bloqueio bloqueio);
    
    void remover(Bloqueio bloqueio);
    
    Optional<Bloqueio> buscar(ContaId bloqueadorId, ContaId alvoId);
}
