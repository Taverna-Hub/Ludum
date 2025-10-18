package org.ludum.catalogo.jogo.entidades;

import org.ludum.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.catalogo.tag.entidades.Tag;
import org.ludum.catalogo.tag.entidades.TagId;
import org.ludum.identidade.conta.entities.ContaId;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Jogo {

    private JogoId id;
    private ContaId desenvolvedoraId;
    private Slug slug;
    private URL capaOficial;
    private StatusPublicacao status;
    private String titulo;
    private String descricao;
    private List<URL> screenshots;
    private List<URL> videos;
    private List<Tag> tags;
    private boolean isNSFW;
    private LocalDate dataDeLancamento;
    private final List<Versao> versaoHistory;

    public Jogo(JogoId id, ContaId desenvolvedoraId, String titulo, String descricao,
            URL capaOficial, List<Tag> tags, boolean isNSFW, LocalDate dataDeLancamento) {

        this.id = Objects.requireNonNull(id);
        this.desenvolvedoraId = Objects.requireNonNull(desenvolvedoraId);
        this.titulo = Objects.requireNonNull(titulo);
        this.descricao = Objects.requireNonNull(descricao);

        validarTitulo(titulo);
        validarDescricao(descricao);

        this.slug = Slug.criar(titulo);
        this.capaOficial = capaOficial;
        this.status = StatusPublicacao.AGUARDANDO_VALIDACAO;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.isNSFW = isNSFW;
        this.dataDeLancamento = dataDeLancamento;
        this.screenshots = new ArrayList<>();
        this.videos = new ArrayList<>();
        this.versaoHistory = new ArrayList<Versao>();
    }

    public Slug montarSlug(String titulo) {
        Objects.requireNonNull(titulo, "Título não pode ser nulo");
        return Slug.criar(titulo);
    }

    private void validarTitulo(String titulo) {
        // TODO: IMPLEMENTAÇÃO
    }

    private void validarDescricao(String descricao) {
        // TODO: IMPLEMENTAÇÃO
    }

    public void adicionarScreenshot(URL screenshot) {
        // TODO: IMPLEMENTAÇÃO
    }

    public void removerScreenshot(URL screenshot) {
        // TODO: IMPLEMENTAÇÃO
    }

    public void adicionarVideo(URL video) {
        // TODO: IMPLEMENTAÇÃO
    }

    public void removerVideo(URL video) {
        // TODO: IMPLEMENTAÇÃO
    }

    public void adicionarTag(Tag tag) {
        // TODO: IMPLEMENTAÇÃO;
    }

    public void removerTag(TagId tagId) {
        // TODO: IMPLEMENTAÇÃO
    }

    public void atualizarTitulo(String novoTitulo) {
        // TODO: IMPLEMENTAÇÃO
    }

    public void atualizarDescricao(String novaDescricao) {
        // TODO: IMPLEMENTAÇÃO
    }

    public void publicar() {
        // TODO: IMPLEMENTAÇÃO
    }

    public void rejeitar() {
        // TODO: IMPLEMENTAÇÃO
    }

    public void arquivar() {
        // TODO: IMPLEMENTAÇÃO
    }

    public void aguardarValidacao() {
        // TODO: IMPLEMENTAÇÃO
    }

    public void adicionarVersao(PacoteZip pacote, VersaoId versaoId, String nomeVersao, String descVersao) {
        Versao newVersao = new Versao(pacote, this.id, versaoId ,nomeVersao, descVersao);
        if (this.versaoHistory.stream().anyMatch(v -> v.getId().equals(versaoId))) {
            throw new IllegalStateException("Versão com mesmo id já existe");
        }

        if(this.versaoHistory.stream().anyMatch(v -> v.getNomeVersao().equalsIgnoreCase(nomeVersao))){
            throw new IllegalStateException("Versão com mesmo título já existe");
        }
        this.versaoHistory.add(newVersao);
    }

    public JogoId getId() {
        return id;
    }

    public ContaId getDesenvolvedoraId() {
        return desenvolvedoraId;
    }

    public Slug getSlug() {
        return slug;
    }

    public URL getCapaOficial() {
        return capaOficial;
    }

    public StatusPublicacao getStatus() {
        return status;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<URL> getScreenshots() {
        return new ArrayList<>(screenshots);
    }

    public List<URL> getVideos() {
        return new ArrayList<>(videos);
    }

    public List<Tag> getTags() {
        return new ArrayList<>(tags);
    }

    public boolean isNSFW() {
        return isNSFW;
    }

    public LocalDate getDataDeLancamento() {
        return dataDeLancamento;
    }

    public List<Versao> getVersaoHistory() {
        return List.copyOf(versaoHistory);}

    public void setCapaOficial(URL capaOficial) {
        this.capaOficial = capaOficial;
    }

    public void setNSFW(boolean NSFW) {
        isNSFW = NSFW;
    }

    public void setDataDeLancamento(LocalDate dataDeLancamento) {
        this.dataDeLancamento = dataDeLancamento;
    }

    @Override
    public String toString() {
        return "Jogo{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", slug=" + slug +
                ", status=" + status +
                ", desenvolvedoraId=" + desenvolvedoraId +
                ", isNSFW=" + isNSFW +
                '}';
    }
}
