package org.ludum.comunidade.post.services;

import org.ludum.comunidade.post.entidades.*;
import org.ludum.comunidade.post.enums.PostStatus;
import org.ludum.comunidade.post.repositorios.PostRepository;
import org.ludum.identidade.conta.entidades.ContaId;

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

    // TODO ADICIONAR O JOGOID
    public Post publicarPost(ContaId autorId, String titulo, String conteudo,
            URL imagem, List<String> tags) {

        // Validações de entrada
        Objects.requireNonNull(autorId, "AutorId não pode ser nulo");
        Objects.requireNonNull(titulo, "Título não pode ser nulo");
        Objects.requireNonNull(conteudo, "Conteúdo não pode ser nulo");

        if (titulo.isBlank()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }

        // Regra 2: Conteúdo deve ter no máximo 500 caracteres
        validarConteudo(conteudo);

        // Regra 3: Tags devem ter entre 1 e 5 elementos
        validarTags(tags);

        // TODO: Integração com serviços externos (camada de aplicação/infraestrutura)
        // - Verificar malware nas imagens (serviço externo de segurança)
        // - Comprimir imagens se necessário (serviço de processamento)
        // - Detectar conteúdo +18 (serviço de moderação)
        // Exemplo:
        // if (imagem != null) {
        // imagemService.verificarMalware(imagem);
        // imagemService.comprimirSeNecessario(imagem);
        // moderacaoService.verificarConteudoInapropriado(conteudo, imagem);
        // }

        // Criar e publicar post
        PostId postId = new PostId(UUID.randomUUID().toString());
        Post post = new Post(postId, autorId, titulo, conteudo,
                LocalDateTime.now(), imagem, PostStatus.PUBLICADO, tags);

        postRepository.salvar(post);
        return post;
    }

    // TODO: ADICIONAR JOGO ID
    public Post criarRascunho(ContaId autorId, String titulo, String conteudo,
            URL imagem, List<String> tags) {

        // Validações de entrada
        Objects.requireNonNull(autorId, "AutorId não pode ser nulo");
        Objects.requireNonNull(titulo, "Título não pode ser nulo");
        Objects.requireNonNull(conteudo, "Conteúdo não pode ser nulo");

        if (titulo.isBlank()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }

        // Validar regras de negócio
        validarConteudo(conteudo);
        validarTags(tags);

        // Criar rascunho
        PostId postId = new PostId(UUID.randomUUID().toString());
        Post post = new Post(postId, autorId, titulo, conteudo,
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

        // Editar usando método do agregado (quando implementado)
        // Temporariamente usando setters
        post.setTitulo(novoTitulo);
        post.setConteudo(novoConteudo);
        postRepository.salvar(post);
    }

    public void curtirPost(PostId postId, ContaId contaId) {
        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(contaId, "ContaId não pode ser nulo");

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        // Regra 4: Verificar se já curtiu (evitar duplicatas)
        // Compara usando equals() do Value Object Curtida
        Curtida novaCurtida = new Curtida(postId, contaId);
        boolean jaCurtiu = post.getCurtidas().stream()
                .anyMatch(curtidaExistente -> curtidaExistente.getContaId().equals(contaId));

        if (jaCurtiu) {
            throw new IllegalArgumentException(
                    "Conta " + contaId.getValue() + " já curtiu este post");
        }

        // Adicionar curtida
        // Quando Post.adicionarCurtida() estiver implementado, use:
        // post.adicionarCurtida(novaCurtida);
        post.getCurtidas().add(novaCurtida);
        postRepository.salvar(post);
    }

    public void descurtirPost(PostId postId, ContaId contaId) {
        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(contaId, "ContaId não pode ser nulo");

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        // Regra 5: Verificar se curtida existe antes de remover
        boolean removido = post.getCurtidas().removeIf(
                curtida -> curtida.getContaId().equals(contaId));

        if (!removido) {
            throw new IllegalArgumentException(
                    "Conta " + contaId.getValue() + " não curtiu este post");
        }

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

        post.getComentarios().add(comentario);
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

        // Buscar comentário
        Comentario comentario = post.getComentarios().stream()
                .filter(c -> c.getId().equals(comentarioId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Comentário " + comentarioId + " não encontrado"));

        // Regra 7: Verificar autorização (autor do comentário OU autor do post)
        boolean ehAutorComentario = comentario.getAutorId().equals(solicitanteId);
        boolean ehAutorPost = post.getAutorId().equals(solicitanteId);

        if (!ehAutorComentario && !ehAutorPost) {
            throw new IllegalArgumentException(
                    "Apenas o autor do comentário ou do post pode remover este comentário");
        }

        post.getComentarios().remove(comentario);
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

        // Validação de estado
        if (post.getStatus() != PostStatus.EM_RASCUNHO) {
            throw new IllegalStateException("Apenas posts em rascunho podem ser agendados");
        }

        try {
            // Agendar post
            post.setStatus(PostStatus.AGENDADO);
            // TODO: Adicionar campo dataAgendamento no Post.java
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

        // Validação de autorização
        if (!post.getAutorId().equals(autorId)) {
            throw new IllegalStateException("Apenas o autor pode publicar o post");
        }

        // Regra 8: Validação de estado
        if (post.getStatus() != PostStatus.EM_RASCUNHO &&
                post.getStatus() != PostStatus.AGENDADO) {
            throw new IllegalStateException(
                    "Apenas posts em rascunho ou agendados podem ser publicados");
        }

        // Publicar post
        post.setStatus(PostStatus.PUBLICADO);
        post.setDataPublicacao(LocalDateTime.now());
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

    private void validarTags(List<String> tags) {
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
