package org.ludum.dominio.oficina.mod.services;

import java.util.List;
import java.util.Objects;

import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.oficina.mod.entidades.Mod;
import org.ludum.dominio.oficina.mod.repositorios.ModRepository;


public class ModsService {
    private final ModRepository modRepository;

    public ModsService(ModRepository modRepository) {
        this.modRepository = Objects.requireNonNull(modRepository, "O repositório de mods não pode ser nulo.");
    }

    public Mod enviarNovoMod(JogoId jogoId, ContaId autorId, String nome, String descricao, String notasDaVersao, byte[] arquivo) {
        Objects.requireNonNull(jogoId, "O ID do jogo não pode ser nulo.");
        Objects.requireNonNull(autorId, "O ID do autor não pode ser nulo.");

        Mod novoMod = new Mod(jogoId, autorId, nome, descricao);
        novoMod.adicionarNovaVersao(notasDaVersao, arquivo);

        modRepository.salvar(novoMod);
        return novoMod;
    }

    public void atualizarMod(String modId, ContaId autorId, String novoNome, String novaDescricao) {
        Mod mod = modRepository.buscarPorId(modId)
                .orElseThrow(() -> new IllegalArgumentException("Mod não encontrado."));
        
        if (!mod.getAutorId().equals(autorId)) {
            throw new IllegalStateException("Apenas o autor pode atualizar o mod.");
        }

        mod.atualizarDetalhes(novoNome, novaDescricao);
        modRepository.salvar(mod);
    }

    public void adicionarVersaoAoMod(String modId, ContaId autorId, String notas, byte[] arquivo) {
        Mod mod = modRepository.buscarPorId(modId)
                .orElseThrow(() -> new IllegalArgumentException("Mod não encontrado."));
        
        if (!mod.getAutorId().equals(autorId)) {
            throw new IllegalStateException("Apenas o autor pode adicionar uma nova versão.");
        }
         
        mod.adicionarNovaVersao(notas, arquivo);
        modRepository.salvar(mod);
    }

    public void removerMod(String modId, ContaId autorId) {
        Mod mod = modRepository.buscarPorId(modId)
                .orElseThrow(() -> new IllegalArgumentException("Mod não encontrado."));
        
        if (!mod.getAutorId().equals(autorId)) {
            throw new IllegalStateException("Apenas o autor pode remover o mod.");
        }

        mod.remover();
        modRepository.salvar(mod);
    }

    public List<Mod> listarModsPorJogo(JogoId jogoId) {
        return modRepository.listarPorJogo(jogoId);
    }
}
