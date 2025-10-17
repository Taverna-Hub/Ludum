package org.ludum.oficina.mod.repositorios;

import org.ludum.oficina.mod.entidades.Mod;
import java.util.List;

public interface ModRepository {
    void salvar(Mod mod);
    Mod buscarPorId(String id);
    List<Mod> listarPorAutor(String autorId);
    List<Mod> listarTodos();
}
