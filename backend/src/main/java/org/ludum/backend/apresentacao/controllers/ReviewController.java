package org.ludum.backend.apresentacao.controllers;

import org.ludum.backend.apresentacao.dto.CriarReviewRequest;
import org.ludum.backend.apresentacao.dto.EditarReviewRequest;
import org.ludum.backend.apresentacao.dto.ReviewResponse;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.comunidade.review.entidades.Review;
import org.ludum.dominio.comunidade.review.entidades.ReviewId;
import org.ludum.dominio.comunidade.review.services.ReviewService;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class ReviewController {

    private final ReviewService reviewService;
    private final ContaRepository contaRepository;

    public ReviewController(ReviewService reviewService, ContaRepository contaRepository) {
        this.reviewService = reviewService;
        this.contaRepository = contaRepository;
    }

    private String obterUserIdAutenticado(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }
        return userId;
    }

    @PostMapping("/jogos/{jogoId}/reviews")
    public ResponseEntity<Void> avaliarJogo(
            @PathVariable("jogoId") String jogoId,
            @RequestBody CriarReviewRequest request,
            HttpServletRequest httpRequest) {
        
        String userId = obterUserIdAutenticado(httpRequest);
        ContaId autorId = new ContaId(userId);
        
        reviewService.avaliarJogo(
                new JogoId(jogoId),
                autorId,
                request.getNota(),
                request.getTitulo(),
                request.getTexto(),
                request.isRecomenda()
        );
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> editarReview(
            @PathVariable("reviewId") String reviewId,
            @RequestBody EditarReviewRequest request,
            HttpServletRequest httpRequest) {

        String userId = obterUserIdAutenticado(httpRequest);
        ContaId autorId = new ContaId(userId);

        reviewService.editarAvaliacao(
                new ReviewId(reviewId),
                autorId,
                request.getTitulo(),
                request.getTexto(),
                request.getNota(),
                request.isRecomenda()
        );
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> removerReview(
            @PathVariable("reviewId") String reviewId,
            HttpServletRequest httpRequest) {

        String userId = obterUserIdAutenticado(httpRequest);
        ContaId autorId = new ContaId(userId);

        reviewService.removerReview(new ReviewId(reviewId), autorId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/jogos/{jogoId}/reviews")
    public ResponseEntity<List<ReviewResponse>> listarReviews(
            @PathVariable("jogoId") String jogoId,
            @RequestParam(value = "nota", required = false) Integer nota,
            @RequestParam(value = "ordenarPorData", required = false) Boolean ordenarPorData,
            @RequestParam(value = "maisRecentes", required = false, defaultValue = "true") Boolean maisRecentes) {
        
        JogoId jId = new JogoId(jogoId);
        List<Review> reviews;

        if (nota != null) {
            reviews = reviewService.filtrarReviewsPorNota(jId, nota);
        } else if (Boolean.TRUE.equals(ordenarPorData)) {
            reviews = reviewService.ordenarReviewsPorData(jId, maisRecentes);
        } else {
            reviews = reviewService.obterReviewsDoJogo(jId);
        }
        
        List<ReviewResponse> response = reviews.stream()
                .map(review -> {
                    Conta autor = contaRepository.obterPorId(review.getAutorId());
                    String autorNome = autor != null ? autor.getNome() : "Usuário";
                    return ReviewResponse.fromReview(review, autorNome);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/jogos/{jogoId}/reviews/resumo")
    public ResponseEntity<Map<String, Object>> obterResumo(@PathVariable("jogoId") String jogoId) {
        JogoId jId = new JogoId(jogoId);
        
        Map<String, Object> resumo = new HashMap<>();
        resumo.put("mediaEstrelas", reviewService.calcularMediaEstrelas(jId));
        resumo.put("totalRecomendacoes", reviewService.obterTotalRecomendacoes(jId));
        resumo.put("porcentagemRecomendacoes", reviewService.calcularPorcentagemRecomendacoes(jId));
        
        return ResponseEntity.ok(resumo);
    }
}
