package org.ludum.backend.apresentacao.controllers;

import org.ludum.backend.apresentacao.dto.Post.*;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.tag.TagRepository;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.comunidade.post.entidades.ComentarioId;
import org.ludum.dominio.comunidade.post.entidades.Post;
import org.ludum.dominio.comunidade.post.entidades.PostId;
import org.ludum.dominio.comunidade.post.enums.PostStatus;
import org.ludum.dominio.comunidade.post.services.PostService;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final TagRepository tagRepository;

    public PostController(
            PostService postService,
            TagRepository tagRepository) {
        this.postService = postService;
        this.tagRepository = tagRepository;
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> obterTodosOsPosts() {
        List<Post> posts = postService.obterTodosOsPosts();
        return ResponseEntity.ok(posts.stream()
                .sorted((p1, p2) -> {
                    if (p2.getDataPublicacao() == null) return -1;
                    if (p1.getDataPublicacao() == null) return 1;
                    return p2.getDataPublicacao().compareTo(p1.getDataPublicacao());
                })
                .map(this::toResponse)
                .collect(Collectors.toList()));
    }

    @PostMapping("/publicar")
    public ResponseEntity<PostResponse> publicarPost(@RequestBody CriarPostRequest request) {
        URL imagem = converterImagem(request.getImagemUrl());
        List<Tag> tags = parseTags(request.getTagIds());
        Post post = postService.publicarPost(
                new JogoId(request.getJogoId()),
                new ContaId(request.getAutorId()),
                request.getTitulo(),
                request.getConteudo(),
                imagem,
                tags);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(post));
    }

    @PostMapping("/rascunho")
    public ResponseEntity<PostResponse> criarRascunho(@RequestBody CriarPostRequest request) {
        URL imagem = converterImagem(request.getImagemUrl());
        List<Tag> tags = parseTags(request.getTagIds());
        Post post = postService.criarRascunho(
                new JogoId(request.getJogoId()),
                new ContaId(request.getAutorId()),
                request.getTitulo(),
                request.getConteudo(),
                imagem,
                tags);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(post));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> obterPostPorId(@PathVariable("id") String id) {
        Post post = postService.obterPostPorId(new PostId(id));
        return ResponseEntity.ok(toResponse(post));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> editarPost(
            @PathVariable("id") String id,
            @RequestHeader("X-Conta-Id") String contaId,
            @RequestBody EditarPostRequest request) {
        Post post = postService.editarPost(
                new PostId(id),
                new ContaId(contaId),
                request.getTitulo(),
                request.getConteudo());
        return ResponseEntity.ok(toResponse(post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPost(
            @PathVariable("id") String id,
            @RequestHeader("X-Conta-Id") String contaId) {
        postService.removerPost(new PostId(id), new ContaId(contaId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/curtir")
    public ResponseEntity<Void> curtirPost(
            @PathVariable("id") String id,
            @RequestHeader("X-Conta-Id") String contaId) {
        postService.curtirPost(new PostId(id), new ContaId(contaId));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/curtir")
    public ResponseEntity<Void> descurtirPost(
            @PathVariable("id") String id,
            @RequestHeader("X-Conta-Id") String contaId) {
        postService.descurtirPost(new PostId(id), new ContaId(contaId));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comentarios")
    public ResponseEntity<Void> comentarPost(
            @PathVariable("id") String id,
            @RequestBody ComentarPostRequest request) {
        postService.comentarPost(
                new PostId(id),
                new ContaId(request.getAutorId()),
                request.getTexto());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{postId}/comentarios/{comentarioId}")
    public ResponseEntity<Void> removerComentario(
            @PathVariable("postId") String postId,
            @PathVariable("comentarioId") String comentarioId,
            @RequestHeader("X-Conta-Id") String contaId) {
        postService.removerComentario(
                new PostId(postId),
                new ComentarioId(comentarioId),
                new ContaId(contaId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/agendar")
    public ResponseEntity<PostResponse> agendarPost(
            @PathVariable("id") String id,
            @RequestBody AgendarPostRequest request) {
        Post post = postService.agendarPost(new PostId(id), request.getDataAgendamento());
        return ResponseEntity.ok(toResponse(post));
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<PostResponse> publicarRascunho(
            @PathVariable("id") String id,
            @RequestHeader("X-Conta-Id") String contaId) {
        Post post = postService.publicarRascunho(new PostId(id), new ContaId(contaId));
        return ResponseEntity.ok(toResponse(post));
    }

    @GetMapping("/autor/{contaId}")
    public ResponseEntity<List<PostResponse>> obterPostsPorAutor(@PathVariable("contaId") String contaId) {
        List<Post> posts = postService.obterPostsPorAutor(new ContaId(contaId));
        return ResponseEntity.ok(posts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<PostResponse>> buscarPostsPorTag(@PathVariable("tag") String tag) {
        List<Post> posts = postService.buscarPostsPorTag(tag);
        return ResponseEntity.ok(posts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PostResponse>> obterPostsPorStatus(@PathVariable("status") PostStatus status) {
        List<Post> posts = postService.obterPostsPorStatus(status);
        return ResponseEntity.ok(posts.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()));
    }

    private URL converterImagem(String imagemUrl) {
        if (imagemUrl == null || imagemUrl.isBlank()) {
            return null;
        }
        try {
            return java.net.URI.create(imagemUrl).toURL();
        } catch (Exception e) {
            throw new IllegalArgumentException("URL da imagem inválida", e);
        }
    }

    private PostResponse toResponse(Post post) {
        PostResponse response = new PostResponse();
        response.setId(post.getId().getId());
        response.setJogoId(post.getJogoId().getValue());
        response.setAutorId(post.getAutorId().getValue());
        response.setTitulo(post.getTitulo());
        response.setConteudo(post.getConteudo());
        response.setDataPublicacao(post.getDataPublicacao());
        response.setDataAgendamento(post.getDataAgendamento());
        response.setImagemUrl(post.getImagem() != null ? post.getImagem().toString() : null);
        response.setStatus(post.getStatus());
        response.setTagIds(post.getTags().stream()
                .map(tag -> tag.getId().getValue())
                .collect(Collectors.toList()));
        response.setNumeroCurtidas(post.getCurtidas().size());
        response.setNumeroComentarios(post.getComentarios().size());
        return response;
    }

    private List<Tag> parseTags(List<String> tagNames) {
        List<Tag> tags = new ArrayList<>();
        if (tagNames != null) {
            for (String tagNome : tagNames) {
                // Buscar tag existente no banco pelo nome
                Tag tag = tagRepository.obterPorNome(tagNome);
                if (tag == null) {
                    throw new IllegalArgumentException("Tag não encontrada: " + tagNome);
                }
                tags.add(tag);
            }
        }
        return tags;
    }
}