package org.ludum.oficina.mod.services;

import java.util.List;

import org.ludum.oficina.mod.entidades.Mod;
import org.ludum.oficina.mod.repositorios.ModRepository;

public class ModsService {
    private final ModRepository repository;

    public ModsService(ModRepository repository) {
        this.repository = repository;
    }

    public Mod criarMod(String nome, String descricao, String autorId) {
        Mod mod = new Mod(nome, descricao, autorId);
        repository.salvar(mod);
        return mod;
    }

    public List<Mod> listarMods() {
        return repository.listarTodos();
    }

    public void removerMod(String modId) {
        Mod mod = repository.buscarPorId(modId);
        if (mod != null) {
            mod.remover();
            repository.salvar(mod);
        }
    }
}
