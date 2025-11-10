package org.ludum.dominio.oficina.mod.repositorios;

import java.util.List;
import java.util.Optional;

import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.oficina.mod.entidades.Mod;

public interface ModRepository {
    
    void salvar(Mod mod);
    
    Optional<Mod> buscarPorId(String id);
    
    List<Mod> listarPorJogo(JogoId jogoId);
    
    List<Mod> listarPorAutor(ContaId autorId);

    void remover(Mod mod);
}
