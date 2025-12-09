package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.seguimento.entities.AlvoId;
import org.ludum.dominio.identidade.seguimento.entities.Seguimento;
import org.ludum.dominio.identidade.seguimento.entities.SeguimentoId;
import org.ludum.dominio.identidade.seguimento.enums.TipoAlvo;
import org.ludum.dominio.identidade.seguimento.repositories.SeguimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "SEGUIMENTO")
class SeguimentoJpa {
    @Id
    String id;
    String seguidorId;
    String seguidoId;
    @Enumerated(EnumType.STRING)
    TipoAlvo tipoAlvo;
}

interface SeguimentoJpaRepository extends JpaRepository<SeguimentoJpa, String> {
    Optional<SeguimentoJpa> findBySeguidorIdAndSeguidoId(String seguidorId, String seguidoId);
    List<SeguimentoJpa> findBySeguidoId(String seguidoId);
    List<SeguimentoJpa> findBySeguidorId(String seguidorId);
}

@Repository
class SeguimentoRepositoryImpl implements SeguimentoRepository {
    @Autowired
    SeguimentoJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    @Override
    public void salvar(Seguimento seguimento) {
        var seguimentoJpa = mapeador.map(seguimento, SeguimentoJpa.class);
        repositorio.save(seguimentoJpa);
    }

    @Override
    public void remover(Seguimento seguimento) {
        repositorio.deleteById(seguimento.getId().getValue());
    }

    @Override
    public Optional<Seguimento> obter(ContaId seguidorId, AlvoId seguidoId) {
        return repositorio.findBySeguidorIdAndSeguidoId(seguidorId.getValue(), seguidoId.getValue())
                .map(s -> mapeador.map(s, Seguimento.class));
    }

    @Override
    public List<Seguimento> obterSeguidoresDe(AlvoId seguidoId) {
        return repositorio.findBySeguidoId(seguidoId.getValue()).stream()
                .map(s -> mapeador.map(s, Seguimento.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Seguimento> obterSeguidosPor(ContaId seguidorId) {
        return repositorio.findBySeguidorId(seguidorId.getValue()).stream()
                .map(s -> mapeador.map(s, Seguimento.class))
                .collect(Collectors.toList());
    }
}
