package org.ludum.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.comunidade.review.entidades.Review;
import org.ludum.dominio.comunidade.review.entidades.ReviewId;
import org.ludum.dominio.comunidade.review.enums.StatusReview;
import org.ludum.dominio.comunidade.review.repositorios.ReviewRepository;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Entity
@Table(name = "REVIEW")
class ReviewJpa {
    @Id
    String id;
    String jogoId;
    String autorId;
    int nota;
    String titulo;
    @Column(length = 4000)
    String texto;
    Date data;
    Date dataUltimaEdicao;
    boolean isRecomendado;
    @Enumerated(EnumType.STRING)
    StatusReview status;
}

interface ReviewJpaRepository extends JpaRepository<ReviewJpa, String> {
    List<ReviewJpa> findByJogoId(String jogoId, org.springframework.data.domain.Pageable pageable);
    List<ReviewJpa> findByAutorId(String autorId, org.springframework.data.domain.Pageable pageable);
    Optional<ReviewJpa> findByAutorIdAndJogoId(String autorId, String jogoId);
    List<ReviewJpa> findByJogoId(String jogoId);
}

@Repository
class ReviewRepositoryImpl implements ReviewRepository {
    @Autowired
    ReviewJpaRepository repositorio;

    @Autowired
    JpaMapeador mapeador;

    @Override
    public void salvar(Review review) {
        var reviewJpa = mapeador.map(review, ReviewJpa.class);
        repositorio.save(reviewJpa);
    }

    @Override
    public Review obterPorId(ReviewId id) {
        return repositorio.findById(id.getValue())
                .map(r -> mapeador.map(r, Review.class))
                .orElse(null);
    }

    @Override
    public List<Review> obterPorJogo(JogoId jogoId, int pagina, int tamanhoPagina) {
        var pageable = PageRequest.of(pagina, tamanhoPagina);
        return repositorio.findByJogoId(jogoId.getValue(), pageable).stream()
                .map(r -> mapeador.map(r, Review.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> obterPorAutor(ContaId autorId, int pagina, int tamanhoPagina) {
        var pageable = PageRequest.of(pagina, tamanhoPagina);
        return repositorio.findByAutorId(autorId.getValue(), pageable).stream()
                .map(r -> mapeador.map(r, Review.class))
                .collect(Collectors.toList());
    }

    @Override
    public void remover(Review review) {
        repositorio.deleteById(review.getId().getValue());
    }

    @Override
    public Optional<Review> obterPorAutorEJogo(ContaId autorId, JogoId jogoId) {
        return repositorio.findByAutorIdAndJogoId(autorId.getValue(), jogoId.getValue())
                .map(r -> mapeador.map(r, Review.class));
    }

    @Override
    public List<Review> obterTodasPorJogo(JogoId jogoId) {
        return repositorio.findByJogoId(jogoId.getValue()).stream()
                .map(r -> mapeador.map(r, Review.class))
                .collect(Collectors.toList());
    }
}
