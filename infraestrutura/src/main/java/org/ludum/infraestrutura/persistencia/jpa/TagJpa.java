package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.ludum.aplicacao.tag.TagRepositorioConsulta;
import org.ludum.aplicacao.tag.TagResumo;
import org.ludum.dominio.catalogo.tag.TagRepository;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "TAG")
class TagJpa {
    @Id
    String id;

    @Column(nullable = false, unique = true, length = 30)
    String nome;
}

interface TagJpaRepository extends JpaRepository<TagJpa, String> {
    Optional<TagJpa> findByNome(String nome);
}

@Repository
class TagRepositoryImpl implements TagRepository, TagRepositorioConsulta {
    @Autowired
    TagJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    // ========== Métodos do TagRepository (Domínio) ==========

    @Override
    public Tag obterPorNome(String nome) {
        Optional<TagJpa> tagJpa = repositorio.findByNome(nome);
        return tagJpa.map(jpa -> mapeador.map(jpa, Tag.class)).orElse(null);
    }

    @Override
    public void salvar(Tag tag) {
        TagJpa tagJpa = mapeador.map(tag, TagJpa.class);
        repositorio.save(tagJpa);
    }

    @Override
    public Tag obterPorId(TagId id) {
        Optional<TagJpa> tagJpa = repositorio.findById(id.getValue());
        return tagJpa.map(jpa -> mapeador.map(jpa, Tag.class)).orElse(null);
    }

    @Override
    public List<Tag> obterTodas() {
        return repositorio.findAll().stream()
                .map(jpa -> mapeador.map(jpa, Tag.class))
                .collect(Collectors.toList());
    }

    @Override
    public void remover(Tag tag) {
        repositorio.deleteById(tag.getId().getValue());
    }

    // ========== Métodos do TagRepositorioConsulta (Aplicação) ==========

    @Override
    public List<TagResumo> listarTodas() {
        return repositorio.findAll().stream()
                .map(jpa -> new TagResumo(jpa.id, jpa.nome))
                .collect(Collectors.toList());
    }
}
