package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.ludum.dominio.identidade.bloqueio.entities.Bloqueio;
import org.ludum.dominio.identidade.bloqueio.entities.BloqueioId;
import org.ludum.dominio.identidade.bloqueio.repositories.BloqueioRepository;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Entity
@Table(name = "BLOQUEIO")
class BloqueioJpa {
    @Id
    String id;
    String bloqueadorId;
    String bloqueadoId;
}

interface BloqueioJpaRepository extends JpaRepository<BloqueioJpa, String> {
    Optional<BloqueioJpa> findByBloqueadorIdAndBloqueadoId(String bloqueadorId, String bloqueadoId);
}

@Repository
class BloqueioRepositoryImpl implements BloqueioRepository {
    @Autowired
    BloqueioJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    @Override
    public void salvar(Bloqueio bloqueio) {
        var jpa = mapeador.map(bloqueio, BloqueioJpa.class);
        repositorio.save(jpa);
    }

    @Override
    public void remover(Bloqueio bloqueio) {
        repositorio.deleteById(bloqueio.getId().getValue());
    }

    @Override
    public Optional<Bloqueio> buscar(ContaId bloqueadorId, ContaId alvoId) {
        return repositorio.findByBloqueadorIdAndBloqueadoId(bloqueadorId.getValue(), alvoId.getValue())
                .map(jpa -> mapeador.map(jpa, Bloqueio.class));
    }
}
