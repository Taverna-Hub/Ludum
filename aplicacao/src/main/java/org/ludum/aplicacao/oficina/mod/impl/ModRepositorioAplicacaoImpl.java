package org.ludum.aplicacao.oficina.mod.impl;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.ludum.aplicacao.oficina.mod.ModDetalhadoDto;
import org.ludum.aplicacao.oficina.mod.ModRepositorioAplicacao;
import org.ludum.aplicacao.oficina.mod.ModResumo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.oficina.mod.entidades.Mod;
import org.ludum.dominio.oficina.mod.repositorios.ModRepository;
import org.springframework.stereotype.Service;

@Service
public class ModRepositorioAplicacaoImpl implements ModRepositorioAplicacao {

    private final ModRepository modRepository;

    public ModRepositorioAplicacaoImpl(ModRepository modRepository) {
        this.modRepository = modRepository;
    }

    @Override
    public Optional<ModDetalhadoDto> buscarDetalhado(String modId) {
        return modRepository.buscarPorId(modId).map(this::toDetalhado);
    }

    @Override
    public List<ModResumo> listarPorJogo(String jogoId) {
        return modRepository.listarPorJogo(new JogoId(jogoId)).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public List<ModResumo> pesquisarResumosPorJogo(String jogoId) {
        return listarPorJogo(jogoId);
    }

    @Override
    public ModResumo buscarResumoPorId(String modId) {
        return modRepository.buscarPorId(modId)
                .map(this::toResumo)
                .orElse(null);
    }

    private ModResumo toResumo(Mod mod) {
        var versoes = mod.getVersoes();
        var ultimaData = versoes.stream()
                .map(v -> v.getDataDeEnvio())
                .max(Comparator.naturalOrder())
                .orElse(null);
        
        return new ModResumoDto(
            mod.getId(),
            mod.getNome(),
            mod.getDescricao(),
            mod.getAutorId().getValue(),
            mod.getStatus().name(),
            versoes.size(),
            ultimaData
        );
    }

    private ModDetalhadoDto toDetalhado(Mod mod) {
        ModDetalhadoDto dto = new ModDetalhadoDto();
        dto.id = mod.getId();
        dto.jogoId = mod.getJogoId().getValue();
        dto.autorId = mod.getAutorId().getValue();
        dto.nome = mod.getNome();
        dto.descricao = mod.getDescricao();
        dto.status = mod.getStatus().name();

        dto.versoes = mod.getVersoes().stream().map(v -> {
            ModDetalhadoDto.VersaoDto vd = new ModDetalhadoDto.VersaoDto();
            vd.notasDeAtualizacao = v.getNotasDeAtualizacao();
            vd.dataDeEnvio = v.getDataDeEnvio();
            return vd;
        }).toList();

        return dto;
    }
}
