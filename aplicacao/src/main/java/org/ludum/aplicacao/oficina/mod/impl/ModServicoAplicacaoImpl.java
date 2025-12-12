package org.ludum.aplicacao.oficina.mod.impl;

import java.util.List;
import java.util.Objects;

import org.ludum.aplicacao.oficina.mod.ModRepositorioAplicacao;
import org.ludum.aplicacao.oficina.mod.ModResumo;
import org.ludum.aplicacao.oficina.mod.ModServicoAplicacao;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.oficina.mod.services.ModsService;
import org.springframework.stereotype.Service;

@Service("modServicoAplicacaoBase")
public class ModServicoAplicacaoImpl implements ModServicoAplicacao {
    
    private final ModRepositorioAplicacao repositorioAplicacao;
    private final ModsService servicoDominio;

    public ModServicoAplicacaoImpl(ModRepositorioAplicacao repositorioAplicacao, ModsService servicoDominio) {
        this.repositorioAplicacao = Objects.requireNonNull(repositorioAplicacao, "O repositório de aplicação não pode ser nulo.");
        this.servicoDominio = Objects.requireNonNull(servicoDominio, "O serviço de domínio não pode ser nulo.");
    }

    @Override
    public void publicarNovoMod(String jogoId, String autorId, String nome, String descricao, String notas, byte[] arquivo) {
        servicoDominio.enviarNovoMod(new JogoId(jogoId), new ContaId(autorId), nome, descricao, notas, arquivo);
    }

    @Override
    public void lancarNovaVersao(String modId, String autorId, String notas, byte[] arquivo) {
        servicoDominio.adicionarVersaoAoMod(modId, new ContaId(autorId), notas, arquivo);
    }

    @Override
    public void atualizarDetalhes(String modId, String autorId, String novoNome, String novaDescricao) {
        servicoDominio.atualizarMod(modId, new ContaId(autorId), novoNome, novaDescricao);
    }

    @Override
    public void removerMod(String modId, String autorId) {
        servicoDominio.removerMod(modId, new ContaId(autorId));
    }

    @Override
    public List<ModResumo> pesquisarResumosPorJogo(String jogoId) {
        return repositorioAplicacao.pesquisarResumosPorJogo(jogoId);
    }

    @Override
    public ModResumo buscarPorId(String modId) {
        return repositorioAplicacao.buscarResumoPorId(modId);
    }
}
