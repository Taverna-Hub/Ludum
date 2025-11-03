package org.ludum.dominio.comunidade.post.entidades;

import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.comunidade.post.enums.PostStatus;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post {
    private PostId id;
    private JogoId jogoId;
    private ContaId autorId;
    private String titulo;
    private String conteudo;
    private LocalDateTime dataPublicacao;
    private LocalDateTime dataAgendamento;
    private URL imagem;
    private PostStatus status;
    private List<Tag> tags;
    private List<Comentario> comentarios;
    private List<Curtida> curtidas;

    public Post(PostId id, JogoId jogoId, ContaId autorId, String titulo, String conteudo,
            LocalDateTime dataPublicacao, URL imagem, PostStatus status,
            List<Tag> tags) {
        this.id = Objects.requireNonNull(id);
        this.jogoId = Objects.requireNonNull(jogoId);
        this.autorId = Objects.requireNonNull(autorId);
        this.titulo = Objects.requireNonNull(titulo);
        this.conteudo = Objects.requireNonNull(conteudo);
        this.dataPublicacao = dataPublicacao;
        this.imagem = imagem;
        this.status = Objects.requireNonNull(status);
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.comentarios = new ArrayList<>();
        this.curtidas = new ArrayList<>();
    }

    public void editarConteudo(String novoTitulo, String novoConteudo) {
        Objects.requireNonNull(novoTitulo, "Novo título não pode ser nulo");
        Objects.requireNonNull(novoConteudo, "Novo conteúdo não pode ser nulo");

        if (novoTitulo.isBlank()) {
            throw new IllegalArgumentException("Título não pode ser vazio");
        }

        this.titulo = novoTitulo;
        this.conteudo = novoConteudo;
    }

    public void adicionarCurtida(ContaId contaId) {
        Objects.requireNonNull(contaId, "ContaId não pode ser nulo");

        // Verificar se já curtiu
        boolean jaCurtiu = curtidas.stream()
                .anyMatch(c -> c.getContaId().equals(contaId));

        if (jaCurtiu) {
            throw new IllegalStateException("Usuário já curtiu este post");
        }

        curtidas.add(new Curtida(this.id, contaId));
    }

    public void removerCurtida(ContaId contaId) {
        Objects.requireNonNull(contaId, "ContaId não pode ser nulo");

        boolean removido = curtidas.removeIf(c -> c.getContaId().equals(contaId));

        if (!removido) {
            throw new IllegalStateException("Usuário não curtiu este post");
        }
    }

    public void adicionarComentario(Comentario comentario) {
        Objects.requireNonNull(comentario, "Comentário não pode ser nulo");

        if (comentario.getTexto() == null || comentario.getTexto().isBlank()) {
            throw new IllegalArgumentException("Texto do comentário não pode ser vazio");
        }

        comentarios.add(comentario);
    }

    public void removerComentario(ComentarioId comentarioId, ContaId solicitanteId) {
        Objects.requireNonNull(comentarioId, "ComentarioId não pode ser nulo");
        Objects.requireNonNull(solicitanteId, "SolicitanteId não pode ser nulo");

        Comentario comentario = comentarios.stream()
                .filter(c -> c.getId().equals(comentarioId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Comentário não encontrado"));

        // Validar autorização: autor do comentário ou autor do post
        if (!comentario.getAutorId().equals(solicitanteId) && !this.autorId.equals(solicitanteId)) {
            throw new IllegalStateException("Apenas o autor do comentário ou do post pode remover este comentário");
        }

        // Soft delete - oculta, não remove permanentemente
        comentario.ocultar();
    }

    public void agendarPost(LocalDateTime dataAgendamento) {
        Objects.requireNonNull(dataAgendamento, "Data de agendamento não pode ser nula");

        if (this.status != PostStatus.EM_RASCUNHO) {
            throw new IllegalStateException("Apenas posts em rascunho podem ser agendados");
        }

        this.dataAgendamento = dataAgendamento;
        this.status = PostStatus.AGENDADO;
    }

    public void publicarPost() {
        if (this.status != PostStatus.EM_RASCUNHO && this.status != PostStatus.AGENDADO) {
            throw new IllegalStateException(
                    "Apenas posts em rascunho ou agendados podem ser publicados");
        }

        this.status = PostStatus.PUBLICADO;

        // Se tinha agendamento, usa a data agendada, senão usa agora
        if (this.dataAgendamento != null) {
            this.dataPublicacao = this.dataAgendamento;
        } else {
            this.dataPublicacao = LocalDateTime.now();
        }
    }

    public boolean deveSerPublicadoAgora() {
        return status == PostStatus.AGENDADO &&
                dataAgendamento != null &&
                LocalDateTime.now().isAfter(dataAgendamento);
    }

    // ================================================================
    // GET AND SETTERS
    // ================================================================

    public void setId(PostId id) {
        this.id = id;
    }

    public PostId getId() {
        return id;
    }

    public void set(JogoId jogoId) {
        this.jogoId = jogoId;
    }

    public JogoId getJogoId() {
        return jogoId;
    }

    public void setAutorId(ContaId autorId) {
        this.autorId = autorId;
    }

    public ContaId getAutorId() {
        return autorId;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataAgendamento(LocalDateTime dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalDateTime getDataAgendamento() {
        return dataAgendamento;
    }

    public void setImagem(URL imagem) {
        this.imagem = imagem;
    }

    public URL getImagem() {
        return imagem;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public PostStatus getStatus() {
        return status;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setCurtidas(List<Curtida> curtidas) {
        this.curtidas = curtidas;
    }

    public List<Curtida> getCurtidas() {
        return curtidas;
    }
}
