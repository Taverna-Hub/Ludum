package org.ludum.infraestrutura.persistencia.jpa;

import org.ludum.aplicacao.oficina.mod.ModRepositorioAplicacao;
import org.ludum.aplicacao.oficina.mod.ModResumo;
import org.ludum.dominio.oficina.mod.enums.StatusMod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ModRepositorioAplicacaoImpl implements ModRepositorioAplicacao {

    @Autowired
    private ModJpaRepository modJpaRepository;

    @Autowired
    private ContaJpaRepository contaJpaRepository;

    @Override
    public List<ModResumo> pesquisarResumosPorJogo(String jogoId) {
        return modJpaRepository.findByJogoId(jogoId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public List<ModResumo> pesquisarResumosPorAutor(String autorId) {
        return modJpaRepository.findByAutorId(autorId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public ModResumo buscarResumoPorId(String id) {
        return modJpaRepository.findById(id)
                .map(this::toResumo)
                .orElse(null);
    }

    private ModResumo toResumo(ModJpa modJpa) {
        String nomeAutor = contaJpaRepository.findById(modJpa.getAutorId())
                .map(ContaJpa::getNome)
                .orElse("Desconhecido");

        return new ModResumoImpl(
                modJpa.getId(),
                modJpa.getNome(),
                modJpa.getDescricao(),
                StatusMod.valueOf(modJpa.getStatus()),
                nomeAutor
        );
    }

    private record ModResumoImpl(
            String id,
            String nome,
            String descricao,
            StatusMod status,
            String nomeAutor
    ) implements ModResumo {
        @Override
        public String getId() { return id; }
        @Override
        public String getNome() { return nome; }
        @Override
        public String getDescricao() { return descricao; }
        @Override
        public StatusMod getStatus() { return status; }
        @Override
        public String getNomeAutor() { return nomeAutor; }
    }
}
