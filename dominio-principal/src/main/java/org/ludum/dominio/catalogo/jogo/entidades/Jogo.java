package org.ludum.dominio.catalogo.jogo.entidades;

import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.identidade.conta.entities.ContaId;

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
        
        validarTitulo(titulo);
        validarDescricao(descricao);
        
        this.titulo = titulo;
        this.descricao = descricao;
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
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Jogo deve ter um título");
        }
        if (titulo.length() < 3) {
            throw new IllegalArgumentException("Título deve ter pelo menos 3 caracteres");
        }
        if (titulo.length() > 100) {
            throw new IllegalArgumentException("Título não pode exceder 100 caracteres");
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Jogo deve ter uma descrição");
        }
        if (descricao.length() < 10) {
            throw new IllegalArgumentException("Descrição deve ter pelo menos 10 caracteres");
        }
        if (descricao.length() > 5000) {
            throw new IllegalArgumentException("Descrição não pode exceder 5000 caracteres");
        }
    }

    public void adicionarScreenshot(URL screenshot) {
        Objects.requireNonNull(screenshot, "Screenshot não pode ser nulo");
        if (this.screenshots.contains(screenshot)) {
            throw new IllegalStateException("Screenshot já adicionado");
        }
        this.screenshots.add(screenshot);
    }

    public void removerScreenshot(URL screenshot) {
        Objects.requireNonNull(screenshot, "Screenshot não pode ser nulo");
        if (!this.screenshots.remove(screenshot)) {
            throw new IllegalStateException("Screenshot não encontrado");
        }
    }

    public void adicionarVideo(URL video) {
        Objects.requireNonNull(video, "Vídeo não pode ser nulo");
        if (this.videos.contains(video)) {
            throw new IllegalStateException("Vídeo já adicionado");
        }
        this.videos.add(video);
    }

    public void removerVideo(URL video) {
        Objects.requireNonNull(video, "Vídeo não pode ser nulo");
        if (!this.videos.remove(video)) {
            throw new IllegalStateException("Vídeo não encontrado");
        }
    }

    public void adicionarTag(Tag tag) {
        Objects.requireNonNull(tag, "Tag não pode ser nula");
        
        if (this.tags.size() >= 10) {
            throw new IllegalStateException("Jogo não pode ter mais de 10 tags");
        }
        
        // Verificar se já tem essa tag
        boolean jaTemTag = this.tags.stream()
                .anyMatch(t -> t.getId().equals(tag.getId()));
        
        if (jaTemTag) {
            throw new IllegalStateException("Tag já adicionada ao jogo");
        }
        
        this.tags.add(tag);
    }

    public void removerTag(TagId tagId) {
        Objects.requireNonNull(tagId, "TagId não pode ser nulo");
        
        boolean removido = this.tags.removeIf(t -> t.getId().equals(tagId));
        
        if (!removido) {
            throw new IllegalStateException("Tag não encontrada no jogo");
        }
    }

    public void atualizarTitulo(String novoTitulo) {
        validarTitulo(novoTitulo);
        this.titulo = novoTitulo;
        this.slug = Slug.criar(novoTitulo);
    }

    public void atualizarDescricao(String novaDescricao) {
        validarDescricao(novaDescricao);
        this.descricao = novaDescricao;
    }

    public void publicar() {
        if (this.status != StatusPublicacao.AGUARDANDO_VALIDACAO) {
            throw new IllegalStateException(
                "Apenas jogos aguardando validação podem ser publicados. Status atual: " + this.status);
        }
        
        validarParaPublicacao();
        
        this.status = StatusPublicacao.PUBLICADO;
    }

    public void rejeitar() {
        if (this.status != StatusPublicacao.AGUARDANDO_VALIDACAO) {
            throw new IllegalStateException(
                "Apenas jogos aguardando validação podem ser rejeitados. Status atual: " + this.status);
        }
        
        this.status = StatusPublicacao.REJEITADO;
    }

    public void arquivar() {
        if (this.status != StatusPublicacao.PUBLICADO) {
            throw new IllegalStateException(
                "Apenas jogos publicados podem ser arquivados. Status atual: " + this.status);
        }
        
        this.status = StatusPublicacao.ARQUIVADO;
    }

    public void aguardarValidacao() {
        if (this.status == StatusPublicacao.PUBLICADO) {
            throw new IllegalStateException("Jogo já está publicado");
        }
        
        this.status = StatusPublicacao.AGUARDANDO_VALIDACAO;
    }
    
    public void validarParaPublicacao() {
        // Validar título e descrição
        if (this.titulo == null || this.titulo.isBlank()) {
            throw new IllegalStateException("Jogo deve ter um título");
        }
        
        if (this.descricao == null || this.descricao.isBlank()) {
            throw new IllegalStateException("Jogo deve ter uma descrição");
        }
        
        // Validar capa oficial
        if (this.capaOficial == null) {
            throw new IllegalStateException("Jogo deve ter uma capa oficial");
        }
        
        // Validar pelo menos 1 screenshot ou vídeo
        if (this.screenshots.isEmpty() && this.videos.isEmpty()) {
            throw new IllegalStateException("Jogo deve ter pelo menos 1 screenshot ou vídeo");
        }
        
        // Validar tags (mínimo 1, máximo 10)
        if (this.tags.isEmpty()) {
            throw new IllegalStateException("Jogo deve ter pelo menos 1 tag");
        }
        
        if (this.tags.size() > 10) {
            throw new IllegalStateException("Jogo não pode ter mais de 10 tags");
        }
        
        // Se for NSFW, deve ter a tag +18
        if (this.isNSFW) {
            boolean temTag18 = this.tags.stream()
                    .anyMatch(tag -> tag.getNome().equalsIgnoreCase("+18") || 
                                   tag.getNome().equalsIgnoreCase("18+") ||
                                   tag.getNome().equalsIgnoreCase("adulto"));
            
            if (!temTag18) {
                throw new IllegalStateException("Jogo adulto deve ter a tag +18");
            }
        }
    }

    public void adicionarVersao(PacoteZip pacote, VersaoId versaoId, String nomeVersao, String descVersao) {
        Versao newVersao = new Versao(pacote, this.id, versaoId ,nomeVersao, descVersao);
        for(int i = 0; i < this.versaoHistory.size(); i++){
            if(this.versaoHistory.get(i).getId().equals(versaoId)){
                throw new IllegalArgumentException("Versão com mesmo id já existente");
            }
            if (this.versaoHistory.get(i).getNomeVersao().equals(nomeVersao)) {
                throw new IllegalArgumentException("Versão com mesmo nome já existente");

            }
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
