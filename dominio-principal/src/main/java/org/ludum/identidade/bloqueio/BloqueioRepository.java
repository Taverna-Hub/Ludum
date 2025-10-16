package org.ludum.identidade.bloqueio;

import org.ludum.identidade.bloqueio.entidades.Bloqueio;
import org.ludum.identidade.conta.entidades.ContaId;

import java.util.Optional;

public interface BloqueioRepository {
    void salvar(Bloqueio bloqueio);
    
    void remover(Bloqueio bloqueio);
    
    Optional<Bloqueio> buscar(ContaId bloqueadorId, ContaId alvoId);
}
