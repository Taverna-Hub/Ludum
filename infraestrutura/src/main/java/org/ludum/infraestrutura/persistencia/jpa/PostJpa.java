package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.ludum.dominio.comunidade.post.entidades.*;
import org.ludum.dominio.comunidade.post.enums.PostStatus;
import org.ludum.dominio.comunidade.post.repositorios.PostRepository;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.TagRepository;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "POST")
class PostJpa {
    @Id
    String id;

    @Column(nullable = false)
    String jogoId;

    @Column(nullable = false)
    String autorId;

    @Column(nullable = false)
    String titulo;

    @Column(nullable = false, length = 5000)
    String conteudo;

    LocalDateTime dataPublicacao;

    LocalDateTime dataAgendamento;

    String imagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    PostStatus status;

    @ElementCollection
    @CollectionTable(name = "POST_TAGS", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag_id")
    List<String> tagIds = new ArrayList<>();

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<ComentarioJpa> comentarios = new ArrayList<>();

    @OneToMany(mappedBy = "postId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<CurtidaJpa> curtidas = new ArrayList<>();
}

@Entity
@Table(name = "COMENTARIO")
class ComentarioJpa {
    @Id
    String id;

    @Column(nullable = false)
    String postId;

    @Column(nullable = false)
    String autorId;

    @Column(nullable = false, length = 2000)
    String texto;

    @Column(nullable = false)
    LocalDateTime data;

    @Column(nullable = false)
    boolean oculto;
}

@Entity
@Table(name = "CURTIDA", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "post_id", "conta_id" })
})
class CurtidaJpa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(name = "post_id", nullable = false)
    String postId;

    @Column(name = "conta_id", nullable = false)
    String contaId;
}

interface PostJpaRepository extends JpaRepository<PostJpa, String> {
    List<PostJpa> findByAutorId(String autorId);

    @Query("SELECT p FROM PostJpa p JOIN p.tagIds t WHERE t = :tagId")
    List<PostJpa> findByTagId(@Param("tagId") String tagId);

    List<PostJpa> findByStatus(PostStatus status);
}

interface ComentarioJpaRepository extends JpaRepository<ComentarioJpa, String> {
}

interface CurtidaJpaRepository extends JpaRepository<CurtidaJpa, String> {
}

@Repository
class PostRepositoryImpl implements PostRepository {
    @Autowired
    PostJpaRepository repositorio;

    @Autowired
    ComentarioJpaRepository comentarioRepository;

    @Autowired
    CurtidaJpaRepository curtidaRepository;

    // Tirar dúvida sobre implementar a injeção de dependência ou usar uma querySQL
    @Autowired
    TagRepository tagRepository;


    @Autowired
    JpaMapeador mapeador;    
    @Override
    public void salvar(Post post) {
        PostJpa postJpa = mapeador.map(post, PostJpa.class);
        repositorio.save(postJpa);
    }

    @Override
    public Post obterPorId(PostId id) {
        Optional<PostJpa> postJpa = repositorio.findById(id.getId());
        return postJpa.map(jpa -> mapeador.map(jpa, Post.class)).orElse(null);
    }

    @Override
    public void remover(Post post) {
        repositorio.deleteById(post.getId().getId());
    }

    @Override
    public List<Post> obterTodosPosts() {
        return repositorio.findAll().stream()
                .map(jpa -> mapeador.map(jpa, Post.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> obterPorAutor(ContaId autorId) {
        return repositorio.findByAutorId(autorId.getValue()).stream()
                .map(jpa -> mapeador.map(jpa, Post.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> buscarPorTag(String tag) {
        Tag tagEntity = tagRepository.obterPorNome(tag);
        if (tagEntity == null) {
            return new ArrayList<>();
        }
        
        String tagId = tagEntity.getId().getValue();
        return repositorio.findByTagId(tagId).stream()
                .map(jpa -> mapeador.map(jpa, Post.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> obterPorStatus(PostStatus status) {
        return repositorio.findByStatus(status).stream()
                .map(jpa -> mapeador.map(jpa, Post.class))
                .collect(Collectors.toList());
    }
}
