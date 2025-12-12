package org.ludum.backend.apresentacao.controllers;

import org.ludum.aplicacao.tag.TagAppService;
import org.ludum.backend.apresentacao.dto.Post.*;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
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
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final TagAppService tagAppService;
    private final org.ludum.dominio.comunidade.post.repositorios.PostRepository postRepository;

    public PostController(
            PostService postService,
            TagAppService tagAppService,
            org.ludum.dominio.comunidade.post.repositorios.PostRepository postRepository) {
        this.postService = postService;
        this.tagAppService = tagAppService;
        this.postRepository = postRepository;
    }

    @PostMapping("/publicar")
    public ResponseEntity<PostResponse> publicarPost(@RequestBody CriarPostRequest request) {
        try {
            URL imagem = converterImagem(request.getImagemUrl());
            List<Tag> tags = tagAppService.obterTagsPorIds(request.getTagIds());
            Post post = postService.publicarPost(
                    new JogoId(request.getJogoId()),
                    new ContaId(request.getAutorId()),
                    request.getTitulo(),
                    request.getConteudo(),
                    imagem,
                    tags);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(post));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/rascunho")
    public ResponseEntity<PostResponse> criarRascunho(@RequestBody CriarPostRequest request) {
        try {
            URL imagem = converterImagem(request.getImagemUrl());
            List<Tag> tags = tagAppService.obterTagsPorIds(request.getTagIds());
            Post post = postService.criarRascunho(
                    new JogoId(request.getJogoId()),
                    new ContaId(request.getAutorId()),
                    request.getTitulo(),
                    request.getConteudo(),
                    imagem,
                    tags);
            return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(post));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> obterPostPorId(@PathVariable String id) {
        try {
            Post post = postRepository.obterPorId(new PostId(id));
            if (post == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(toResponse(post));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> editarPost(
            @PathVariable String id,
            @RequestHeader("X-Conta-Id") String contaId,
            @RequestBody EditarPostRequest request) {
        try {
            postService.editarPost(
                    new PostId(id),
                    new ContaId(contaId),
                    request.getTitulo(),
                    request.getConteudo());
            Post post = postRepository.obterPorId(new PostId(id));
            return ResponseEntity.ok(toResponse(post));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerPost(
            @PathVariable String id,
            @RequestHeader("X-Conta-Id") String contaId) {
        try {
            postService.removerPost(new PostId(id), new ContaId(contaId));
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/{id}/curtir")
    public ResponseEntity<Void> curtirPost(
            @PathVariable String id,
            @RequestHeader("X-Conta-Id") String contaId) {
        try {
            postService.curtirPost(new PostId(id), new ContaId(contaId));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}/curtir")
    public ResponseEntity<Void> descurtirPost(
            @PathVariable String id,
            @RequestHeader("X-Conta-Id") String contaId) {
        try {
            postService.descurtirPost(new PostId(id), new ContaId(contaId));
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/comentarios")
    public ResponseEntity<Void> comentarPost(
            @PathVariable String id,
            @RequestBody ComentarPostRequest request) {
        try {
            postService.comentarPost(
                    new PostId(id),
                    new ContaId(request.getAutorId()),
                    request.getTexto());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{postId}/comentarios/{comentarioId}")
    public ResponseEntity<Void> removerComentario(
            @PathVariable String postId,
            @PathVariable String comentarioId,
            @RequestHeader("X-Conta-Id") String contaId) {
        try {
            postService.removerComentario(
                    new PostId(postId),
                    new ComentarioId(comentarioId),
                    new ContaId(contaId));
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/{id}/agendar")
    public ResponseEntity<PostResponse> agendarPost(
            @PathVariable String id,
            @RequestBody AgendarPostRequest request) {
        try {
            postService.agendarPost(new PostId(id), request.getDataAgendamento());
            Post post = postRepository.obterPorId(new PostId(id));
            return ResponseEntity.ok(toResponse(post));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/{id}/publicar")
    public ResponseEntity<PostResponse> publicarRascunho(
            @PathVariable String id,
            @RequestHeader("X-Conta-Id") String contaId) {
        try {
            postService.publicarRascunho(new PostId(id), new ContaId(contaId));
            Post post = postRepository.obterPorId(new PostId(id));
            return ResponseEntity.ok(toResponse(post));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/autor/{contaId}")
    public ResponseEntity<List<PostResponse>> obterPostsPorAutor(@PathVariable String contaId) {
        try {
            List<Post> posts = postRepository.obterPorAutor(new ContaId(contaId));
            return ResponseEntity.ok(posts.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/tag/{tag}")
    public ResponseEntity<List<PostResponse>> buscarPostsPorTag(@PathVariable String tag) {
        try {
            List<Post> posts = postRepository.buscarPorTag(tag);
            return ResponseEntity.ok(posts.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PostResponse>> obterPostsPorStatus(@PathVariable PostStatus status) {
        try {
            List<Post> posts = postRepository.obterPorStatus(status);
            return ResponseEntity.ok(posts.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
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
}