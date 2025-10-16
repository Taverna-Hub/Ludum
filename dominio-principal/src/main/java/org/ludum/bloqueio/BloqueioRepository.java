package org.ludum.bloqueio;

import org.ludum.bloqueio.entidades.Bloqueio;
import org.ludum.conta.entidades.ContaId;

public interface BloqueioRepository {
    void salvar(Bloqueio bloqueio);
    
    void remover(Bloqueio bloqueio);
    
    boolean verificarBloqueio(ContaId bloqueadorId, ContaId alvoId);
}
