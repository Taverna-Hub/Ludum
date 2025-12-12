package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.enums.StatusConta;
import org.ludum.dominio.identidade.conta.enums.TipoConta;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Entity
@Table(name = "CONTA")
class ContaJpa {
    @Id
    String id;
    String nome;
    String senhaHash;
    @Enumerated(EnumType.STRING)
    TipoConta tipo;
    @Enumerated(EnumType.STRING)
    StatusConta status;
}

interface ContaJpaRepository extends JpaRepository<ContaJpa, String> {
    java.util.Optional<ContaJpa> findByNome(String nome);
}

@Repository
class ContaRepositoryImpl implements ContaRepository {
    @Autowired
    ContaJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    @Override
    public void salvar(Conta conta) {
        var jpa = mapeador.map(conta, ContaJpa.class);
        repositorio.save(jpa);
    }

    @Override
    public Conta obterPorId(ContaId id) {
        return repositorio.findById(id.getValue())
                .map(jpa -> mapeador.map(jpa, Conta.class))
                .orElse(null);
    }

    @Override
    public Conta obterPorNome(String nome) {
        return repositorio.findByNome(nome)
                .map(jpa -> mapeador.map(jpa, Conta.class))
                .orElse(null);
    }
}
