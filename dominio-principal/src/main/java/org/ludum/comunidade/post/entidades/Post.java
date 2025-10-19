package org.ludum.comunidade.post.entidades;
import org.ludum.comunidade.post.enums.PostStatus;

import org.ludum.identidade.conta.entidades.ContaId;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post {
    private PostId id;
    // private JogoId jogoId; //TODO
    private ContaId autorId;
    private String titulo;
    private String conteudo;
    private LocalDateTime dataPublicacao;
    private URL imagem;
    private PostStatus status;
    private List<String> tags;
    private List<Comentario> comentarios;
    private List<Curtida> curtidas;

    public Post(PostId id, ContaId autorId, String titulo, String conteudo,
            LocalDateTime dataPublicacao, URL imagem, PostStatus status,
            List<String> tags) {
        this.id = Objects.requireNonNull(id);
        // this.jogoId = Objects.requireNonNull(jogoId);
        this.autorId = Objects.requireNonNull(autorId);
        this.titulo = Objects.requireNonNull(titulo);
        this.conteudo = Objects.requireNonNull(conteudo);
        this.dataPublicacao = Objects.requireNonNull(dataPublicacao);
        this.imagem = imagem;
        this.status = Objects.requireNonNull(status);
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.comentarios = new ArrayList<>();
        this.curtidas = new ArrayList<>();
    }

    public void editarConteudo(String novoTitulo, String novoConteudo) {
        // TODO: Implementar lógica de edição de conteúdo
    }

    public void adicionarCurtida(ContaId contaId) {
        // TODO: Implementar lógica de adição de curtida
        // - Validar se conta já curtiu
        // - Adicionar curtida à lista
    }

    public void removerCurtida(ContaId contaId) {
        // TODO: Implementar lógica de remoção de curtida
        // - Validar se curtida existe
        // - Remover curtida da lista
    }

    public void adicionarComentario(Comentario comentario) {
        // TODO: Implementar lógica de adição de comentário
        // - Validar comentário
        // - Adicionar à lista
    }

    public void removerComentario(ComentarioId comentarioId, ContaId solicitanteId) {
        // TODO: Implementar lógica de remoção de comentário
        // - Validar autorização (autor do comentário ou do post)
        // - Remover comentário
    }

    public void setId(PostId id) {
        this.id = id;
    }

    public PostId getId() {
        return id;
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

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTags() {
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
