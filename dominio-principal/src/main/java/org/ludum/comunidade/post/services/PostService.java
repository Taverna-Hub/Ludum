package org.ludum.comunidade.post.services;

import org.ludum.comunidade.post.entidades.*;
import org.ludum.comunidade.post.enums.PostStatus;
import org.ludum.comunidade.post.repositorios.PostRepository;
import org.ludum.identidade.conta.entities.ContaId;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PostService {

    // Constantes para regras de negócio
    private static final int MAX_CONTEUDO_CARACTERES = 500;
    private static final int MIN_TAGS = 1;
    private static final int MAX_TAGS = 5;
    private static final long MIN_HORAS_AGENDAMENTO = 1;
    private static final long MAX_HORAS_AGENDAMENTO = 24;

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = Objects.requireNonNull(postRepository,
                "PostRepository não pode ser nulo");
    }

    public Post publicarPost(JogoId jogoId, ContaId autorId, String titulo, String conteudo,
            URL imagem, List<Tag> tags) {

        Objects.requireNonNull(jogoId, "JogoId não pode ser nulo");
        Objects.requireNonNull(autorId, "AutorId não pode ser nulo");
        Objects.requireNonNull(titulo, "Título não pode ser nulo");
        Objects.requireNonNull(conteudo, "Conteúdo não pode ser nulo");

        if (titulo.isBlank()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }

        validarConteudo(conteudo);
        validarTags(tags);

        if (jogoInfo != null) {
            validarTagsDoJogo(jogoId, tags);
        }

        if (imagem != null) {
            imagem = processarImagem(imagem, jogoId, autorId);
        }

        PostId postId = new PostId(UUID.randomUUID().toString());
        Post post = new Post(postId, jogoId, autorId, titulo, conteudo,
                LocalDateTime.now(), imagem, PostStatus.PUBLICADO, tags);

        postRepository.salvar(post);
        return post;
    }

    public Post criarRascunho(JogoId jogoId, ContaId autorId, String titulo, String conteudo,
            URL imagem, List<Tag> tags) {

        Objects.requireNonNull(jogoId, "JogoId não pode ser nulo");
        Objects.requireNonNull(autorId, "AutorId não pode ser nulo");
        Objects.requireNonNull(titulo, "Título não pode ser nulo");
        Objects.requireNonNull(conteudo, "Conteúdo não pode ser nulo");

        if (titulo.isBlank()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }

        validarConteudo(conteudo);
        validarTags(tags);

        // Criar rascunho
        PostId postId = new PostId(UUID.randomUUID().toString());
        Post post = new Post(postId, jogoId, autorId, titulo, conteudo,
                null, imagem, PostStatus.EM_RASCUNHO, tags);

        postRepository.salvar(post);
        return post;
    }

    public void editarPost(PostId postId, ContaId solicitanteId,
            String novoTitulo, String novoConteudo) {

        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(solicitanteId, "SolicitanteId não pode ser nulo");
        Objects.requireNonNull(novoTitulo, "Novo título não pode ser nulo");
        Objects.requireNonNull(novoConteudo, "Novo conteúdo não pode ser nulo");

        if (novoTitulo.isBlank()) {
            throw new IllegalArgumentException("Novo título não pode ser vazio");
        }

        // Validar regra de conteúdo
        validarConteudo(novoConteudo);

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        // Validação de autorização
        if (!post.getAutorId().equals(solicitanteId)) {
            throw new IllegalStateException("Apenas o autor pode editar o post");
        }

        post.editarConteudo(novoTitulo, novoConteudo);
        postRepository.salvar(post);
    }

    public void curtirPost(PostId postId, ContaId contaId) {
        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(contaId, "ContaId não pode ser nulo");

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        // Adicionar Curtida
        post.adicionarCurtida(contaId);
        postRepository.salvar(post);
    }

    public void descurtirPost(PostId postId, ContaId contaId) {
        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(contaId, "ContaId não pode ser nulo");

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        // Remover Curtida
        post.removerCurtida(contaId);
        postRepository.salvar(post);
    }

    public void comentarPost(PostId postId, ContaId autorId, String texto) {
        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(autorId, "AutorId não pode ser nulo");
        Objects.requireNonNull(texto, "Texto não pode ser nulo");

        if (texto.isBlank()) {
            throw new IllegalArgumentException("Texto do comentário não pode ser vazio");
        }

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        // Criar e adicionar comentário
        ComentarioId comentarioId = new ComentarioId(UUID.randomUUID().toString());
        Comentario comentario = new Comentario(comentarioId, postId, autorId,
                texto, LocalDateTime.now());

        post.adicionarComentario(comentario);
        postRepository.salvar(post);
    }

    public void removerComentario(PostId postId, ComentarioId comentarioId,
            ContaId solicitanteId) {

        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(comentarioId, "ComentarioId não pode ser nulo");
        Objects.requireNonNull(solicitanteId, "SolicitanteId não pode ser nulo");

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        // Remoção de comentário
        post.removerComentario(comentarioId, solicitanteId);
        postRepository.salvar(post);
    }

    public void removerPost(PostId postId, ContaId solicitanteId) {
        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(solicitanteId, "SolicitanteId não pode ser nulo");

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        // Validação de autorização
        if (!post.getAutorId().equals(solicitanteId)) {
            throw new IllegalStateException("Apenas o autor pode remover o post");
        }

        postRepository.remover(post);
    }

    public void agendarPost(PostId postId, LocalDateTime dataAgendamento) {
        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(dataAgendamento, "Data de agendamento não pode ser nula");

        // Validação específica: janela de agendamento (1h a 24h)
        LocalDateTime agora = LocalDateTime.now();
        long horasAteAgendamento = Duration.between(agora, dataAgendamento).toHours();

        if (horasAteAgendamento < MIN_HORAS_AGENDAMENTO) {
            throw new IllegalArgumentException(
                    "Data de agendamento deve ser pelo menos " + MIN_HORAS_AGENDAMENTO +
                            " hora(s) no futuro");
        }

        if (horasAteAgendamento > MAX_HORAS_AGENDAMENTO) {
            throw new IllegalArgumentException(
                    "Data de agendamento não pode ultrapassar " + MAX_HORAS_AGENDAMENTO +
                            " horas no futuro");
        }

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        try {
            // Agendar post
            post.agendarPost(dataAgendamento);
            postRepository.salvar(post);

        } catch (Exception e) {
            // Fallback: se falhar, retorna para RASCUNHO
            post.setStatus(PostStatus.EM_RASCUNHO);
            postRepository.salvar(post);
            throw new IllegalStateException(
                    "Falha ao agendar post. Retornado para rascunho. Motivo: " + e.getMessage(), e);
        }
    }

    public void publicarRascunho(PostId postId, ContaId autorId) {
        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(autorId, "AutorId não pode ser nulo");

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        if (!post.getAutorId().equals(autorId)) {
            throw new IllegalStateException("Apenas o autor pode publicar o post");
        }

        // Publicar post
        post.publicarPost();
        postRepository.salvar(post);
    }

    public List<Post> obterPostsPorAutor(ContaId autorId) {
        Objects.requireNonNull(autorId, "AutorId não pode ser nulo");
        return postRepository.obterPorAutor(autorId);
    }

    public List<Post> buscarPostsPorTag(String tag) {
        Objects.requireNonNull(tag, "Tag não pode ser nula");

        if (tag.isBlank()) {
            throw new IllegalArgumentException("Tag não pode ser vazia");
        }

        return postRepository.buscarPorTag(tag);
    }

    public List<Post> obterPostsPorStatus(PostStatus status) {
        Objects.requireNonNull(status, "Status não pode ser nulo");
        return postRepository.obterPorStatus(status);
    }

    // ==================== MÉTODOS PRIVADOS DE VALIDAÇÃO ====================

    private void validarConteudo(String conteudo) {
        Objects.requireNonNull(conteudo, "Conteúdo não pode ser nulo");

        if (conteudo.length() > MAX_CONTEUDO_CARACTERES) {
            throw new IllegalArgumentException(
                    "Conteúdo excede o limite de " + MAX_CONTEUDO_CARACTERES +
                            " caracteres (atual: " + conteudo.length() + ")");
        }
    }

    private void validarTags(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            throw new IllegalArgumentException(
                    "Post deve ter pelo menos " + MIN_TAGS + " tag");
        }

        if (tags.size() > MAX_TAGS) {
            throw new IllegalArgumentException(
                    "Post não pode ter mais de " + MAX_TAGS + " tags (atual: " + tags.size() + ")");
        }
    }
}
