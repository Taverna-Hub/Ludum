package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.ludum.aplicacao.catalogo.jogo.JogoRepositorioConsulta;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.Slug;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.catalogo.jogo.entidades.Versao;
import org.ludum.dominio.catalogo.jogo.entidades.VersaoId;
import org.ludum.dominio.catalogo.jogo.entidades.PacoteZip;

import java.lang.reflect.Field;
import java.net.URL;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "jogo_tags", joinColumns = @JoinColumn(name = "jogo_id"))
    @Column(name = "tag_id")
    List<String> tagIds = new ArrayList<>();

    @Column(nullable = false)
    boolean isNSFW;

    @OneToMany(mappedBy = "jogo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
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

    @Query("SELECT j FROM JogoJpa j WHERE j.status = 'PUBLICADO'")
    List<JogoJpa> findAllPublicados();

    List<JogoJpa> findByDesenvolvedoraId(String desenvolvedoraId);
}

@Repository
class JogoRepositoryImpl implements JogoRepository, JogoRepositorioConsulta {
    @Autowired
    JogoJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    @Autowired
    TagRepositoryImpl tagRepository;

    // ========== Métodos do JogoRepository (Domínio) ==========

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
        return jogoJpa.map(this::reconstruirJogo).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Jogo obterPorSlug(Slug slug) {
        Optional<JogoJpa> jogoJpa = repositorio.findBySlug(slug.getValor());
        return jogoJpa.map(this::reconstruirJogo).orElse(null);
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
                .map(this::reconstruirJogo)
                .collect(Collectors.toList());
    }

    // ========== Métodos do JogoRepositorioConsulta (Aplicação) ==========

    @Override
    @Transactional(readOnly = true)
    public List<Jogo> listarTodosPublicados() {
        List<JogoJpa> jogosJpa = repositorio.findAllPublicados();
        return jogosJpa.stream()
                .map(this::reconstruirJogo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Jogo> listarPorDesenvolvedora(ContaId devId) {
        List<JogoJpa> jogosJpa = repositorio.findByDesenvolvedoraId(devId.getValue());
        return jogosJpa.stream()
                .map(this::reconstruirJogo)
                .collect(Collectors.toList());
    }

    private Jogo reconstruirJogo(JogoJpa jpa) {
        if (jpa == null)
            return null;
        try {
            URL capa = jpa.capaOficial != null ? new URL(jpa.capaOficial) : null;
            ContaId devId = new ContaId(jpa.desenvolvedoraId);
            JogoId jogoId = new JogoId(jpa.id);

            List<Tag> tags = new ArrayList<>();
            if (jpa.tagIds != null && !jpa.tagIds.isEmpty()) {
                for (String tagId : jpa.tagIds) {
                    Tag tag = tagRepository.obterPorId(new TagId(tagId));
                    if (tag != null) {
                        tags.add(tag);
                    }
                }
            }

            Jogo jogo = new Jogo(
                    jogoId,
                    devId,
                    jpa.titulo,
                    jpa.descricao,
                    capa,
                    tags,
                    jpa.isNSFW,
                    jpa.dataDeLancamento);

            if ("PUBLICADO".equals(jpa.status.name())) {
                Field statusField = Jogo.class.getDeclaredField("status");
                statusField.setAccessible(true);
                statusField.set(jogo, StatusPublicacao.PUBLICADO);
            }

            for (String url : jpa.screenshots) {
                jogo.adicionarScreenshot(new URL(url));
            }

            if (jpa.versoes != null) {
                Field versaoHistoryField = Jogo.class.getDeclaredField("versaoHistory");
                versaoHistoryField.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<Versao> versaoList = (List<Versao>) versaoHistoryField.get(jogo);

                for (VersaoJpa vJpa : jpa.versoes) {
                    PacoteZip pacote = new PacoteZip(vJpa.conteudo);
                    Versao versao = new Versao(
                            pacote,
                            new JogoId(jpa.id),
                            new VersaoId(vJpa.id),
                            vJpa.nomeVersao,
                            vJpa.descricaoVersao,
                            vJpa.dataUpload);
                    versaoList.add(versao);
                }
            }

            return jogo;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // ou lançar runtime
        }
    }
}
