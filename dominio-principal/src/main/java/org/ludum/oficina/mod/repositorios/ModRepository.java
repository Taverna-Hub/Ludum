package org.ludum.oficina.mod.repositorios;

import java.util.List;
import java.util.Optional;

import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.oficina.mod.entidades.Mod;

public interface ModRepository {
    
    void salvar(Mod mod);
    
    Optional<Mod> buscarPorId(String id);
    
    List<Mod> listarPorJogo(JogoId jogoId);
    
    List<Mod> listarPorAutor(ContaId autorId);

    void remover(Mod mod);
}
