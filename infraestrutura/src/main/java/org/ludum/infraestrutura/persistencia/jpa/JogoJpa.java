package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.Slug;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "JOGO")
class JogoJpa {
    @Id
    String id;

    @Column(nullable = false)
    String desenvolvedoraId;

    @Column(nullable = false, unique = true, length = 200)
    String slug;

    String capaOficial;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    StatusPublicacao status;

    @Column(nullable = false, length = 100)
    String titulo;

    @Column(nullable = false, length = 5000)
    String descricao;

    @ElementCollection
    @CollectionTable(name = "JOGO_SCREENSHOTS", joinColumns = @JoinColumn(name = "jogo_id"))
    @Column(name = "screenshot_url", length = 500)
    List<String> screenshots = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "JOGO_VIDEOS", joinColumns = @JoinColumn(name = "jogo_id"))
    @Column(name = "video_url", length = 500)
    List<String> videos = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "JOGO_TAGS", joinColumns = @JoinColumn(name = "jogo_id"))
    @Column(name = "tag_id")
    List<String> tagIds = new ArrayList<>();

    @Column(nullable = false)
    boolean isNSFW;

    @OneToMany(mappedBy = "jogo", cascade = CascadeType.ALL, orphanRemoval = true)
    List<VersaoJpa> versoes = new ArrayList<>();

    LocalDate dataDeLancamento;
}

@Entity
@Table(name = "VERSAO_JOGO")
class VersaoJpa {
    @Id
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jogo_id")
    JogoJpa jogo;

    @Lob
    @Column(nullable = false)
    byte[] conteudo;

    @Column(nullable = false)
    String nomeVersao;

    @Column(nullable = false)
    String descricaoVersao;

    @Column(nullable = false)
    java.time.LocalDateTime dataUpload;
}

interface JogoJpaRepository extends JpaRepository<JogoJpa, String> {
    Optional<JogoJpa> findBySlug(String slug);

    @Query("SELECT j FROM JogoJpa j WHERE j.desenvolvedoraId = :devId AND j.slug = :slug")
    Optional<JogoJpa> findByDesenvolvedoraIdAndSlug(@Param("devId") String devId, @Param("slug") String slug);

    @Query("SELECT j FROM JogoJpa j JOIN j.tagIds t WHERE t = :tagId")
    List<JogoJpa> findByTagId(@Param("tagId") String tagId);
}

@Repository
class JogoRepositoryImpl implements JogoRepository {
    @Autowired
    JogoJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    @Override
    @Transactional
    public void salvar(Jogo jogo) {
        JogoJpa jogoJpa = mapeador.map(jogo, JogoJpa.class);
        repositorio.save(jogoJpa);
    }

    @Override
    @Transactional(readOnly = true)
    public Jogo obterPorId(JogoId id) {
        Optional<JogoJpa> jogoJpa = repositorio.findById(id.getValue());
        return jogoJpa.map(jpa -> mapeador.map(jpa, Jogo.class)).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Jogo obterPorSlug(Slug slug) {
        Optional<JogoJpa> jogoJpa = repositorio.findBySlug(slug.getValor());
        return jogoJpa.map(jpa -> mapeador.map(jpa, Jogo.class)).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeSlugParaDesenvolvedora(ContaId devId, Slug slug) {
        Optional<JogoJpa> jogoJpa = repositorio.findByDesenvolvedoraIdAndSlug(
                devId.getValue(),
                slug.getValor());
        return jogoJpa.isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jogo> obterJogosPorTag(TagId tagId) {
        List<JogoJpa> jogosJpa = repositorio.findByTagId(tagId.getValue());
        return jogosJpa.stream()
                .map(jpa -> mapeador.map(jpa, Jogo.class))
                .collect(Collectors.toList());
    }
}
