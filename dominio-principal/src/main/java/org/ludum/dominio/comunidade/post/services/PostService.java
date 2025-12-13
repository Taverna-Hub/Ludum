package org.ludum.dominio.comunidade.post.services;

import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.comunidade.post.entidades.Comentario;
import org.ludum.dominio.comunidade.post.entidades.ComentarioId;
import org.ludum.dominio.comunidade.post.entidades.Post;
import org.ludum.dominio.comunidade.post.entidades.PostId;
import org.ludum.dominio.comunidade.post.enums.PostStatus;
import org.ludum.dominio.comunidade.post.repositorios.PostRepository;
import org.ludum.dominio.identidade.conta.entities.ContaId;

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
    private final MalwareScanner malwareScanner;
    private final ImagemCompressor imagemCompressor;
    private final ConteudoAdultoValidator conteudoAdultoValidator;
    private final JogoInfo jogoInfo;
    private final NotificacaoService notificacaoService;

    public PostService(PostRepository postRepository) {
        this(postRepository, null, null, null, null, null);
    }

    public PostService(PostRepository postRepository,
            MalwareScanner malwareScanner,
            ImagemCompressor imagemCompressor,
            ConteudoAdultoValidator conteudoAdultoValidator,
            JogoInfo jogoInfo,
            NotificacaoService notificacaoService) {
        this.postRepository = Objects.requireNonNull(postRepository,
                "PostRepository não pode ser nulo");
        this.malwareScanner = malwareScanner;
        this.imagemCompressor = imagemCompressor;
        this.conteudoAdultoValidator = conteudoAdultoValidator;
        this.jogoInfo = jogoInfo;
        this.notificacaoService = notificacaoService;
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

        if (jogoInfo != null) {
            validarTagsDoJogo(jogoId, tags);
        }

        PostId postId = new PostId(UUID.randomUUID().toString());
        Post post = new Post(postId, jogoId, autorId, titulo, conteudo,
                null, imagem, PostStatus.EM_RASCUNHO, tags);

        postRepository.salvar(post);
        return post;
    }

    public Post editarPost(PostId postId, ContaId solicitanteId,
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
        return post;
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

    public Post agendarPost(PostId postId, LocalDateTime dataAgendamento) {
        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Objects.requireNonNull(dataAgendamento, "Data de agendamento não pode ser nula");

        // Validação específica: janela de agendamento (1h a 24h)
        LocalDateTime agora = LocalDateTime.now();
        long horasAteAgendamento = Duration.between(agora, dataAgendamento).toHours();

        if (horasAteAgendamento < MIN_HORAS_AGENDAMENTO) {
            String mensagemErro = "Data de agendamento deve ser pelo menos " + MIN_HORAS_AGENDAMENTO +
                    " hora(s) no futuro";

            // Notificar sobre falha
            Post post = postRepository.obterPorId(postId);
            if (post != null && notificacaoService != null) {
                notificacaoService.notificarFalhaAgendamento(post.getAutorId(), postId, mensagemErro);
            }

            throw new IllegalArgumentException(mensagemErro);
        }

        if (horasAteAgendamento > MAX_HORAS_AGENDAMENTO) {
            String mensagemErro = "Data de agendamento não pode ultrapassar " + MAX_HORAS_AGENDAMENTO +
                    " horas no futuro";

            // Notificar sobre falha
            Post post = postRepository.obterPorId(postId);
            if (post != null && notificacaoService != null) {
                notificacaoService.notificarFalhaAgendamento(post.getAutorId(), postId, mensagemErro);
            }

            throw new IllegalArgumentException(mensagemErro);
        }

        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado");
        }

        try {
            // Agendar post
            post.agendarPost(dataAgendamento);
            postRepository.salvar(post);
            return post;

        } catch (Exception e) {
            // Notificar sobre falha genérica
            if (notificacaoService != null) {
                notificacaoService.notificarFalhaAgendamento(post.getAutorId(), postId, e.getMessage());
            }

            throw new IllegalStateException(
                    "Falha ao agendar post. Motivo: " + e.getMessage(), e);
        }
    }

    public Post publicarRascunho(PostId postId, ContaId autorId) {
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
        return post;
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

    public List<Post> obterTodosOsPosts() {
        return postRepository.obterTodosPosts();
    }

    public Post obterPostPorId(PostId postId) {
        Objects.requireNonNull(postId, "PostId não pode ser nulo");
        Post post = postRepository.obterPorId(postId);
        if (post == null) {
            throw new IllegalArgumentException("Post não encontrado: " + postId.getId());
        }
        return post;
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

    private void validarTagsDoJogo(JogoId jogoId, List<Tag> tags) {
        List<Tag> tagsDoJogo = jogoInfo.obterTagsDoJogo(jogoId);

        for (Tag tag : tags) {
            if (!tagsDoJogo.contains(tag)) {
                throw new IllegalArgumentException(
                        "Tag '" + tag + "' não está associada ao jogo");
            }
        }
    }

    private URL processarImagem(URL imagem, JogoId jogoId, ContaId autorId) {
        // 1. Verificar malware
        if (malwareScanner != null && malwareScanner.contemMalware(imagem)) {
            throw new SecurityException("Falha na publicação: malware detectado na imagem");
        }

        // 2. Verificar conteúdo +18
        if (conteudoAdultoValidator != null && jogoInfo != null) {
            boolean jogoAdulto = jogoInfo.isJogoAdulto(jogoId);
            boolean imagemAdulta = conteudoAdultoValidator.contemConteudoAdulto(imagem);

            if (!jogoAdulto && imagemAdulta) {
                // Criar PostId temporário para notificação
                PostId postIdTemp = new PostId(UUID.randomUUID().toString());

                // Notificar usuário sobre bloqueio
                if (notificacaoService != null) {
                    notificacaoService.notificarImagemBloqueada(autorId, postIdTemp);
                }

                throw new SecurityException(
                        "Imagem bloqueada: conteúdo adulto não permitido em jogo não-adulto");
            }
        }

        // 3. Compactar se necessário
        if (imagemCompressor != null && imagemCompressor.excedeLimit(imagem)) {
            imagem = imagemCompressor.compactar(imagem);
        }

        return imagem;
    }
}
