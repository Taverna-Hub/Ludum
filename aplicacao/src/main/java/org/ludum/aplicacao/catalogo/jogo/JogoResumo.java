package org.ludum.aplicacao.catalogo.jogo;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.dominio.catalogo.tag.entidades.Tag;

import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO de resumo de jogo para a camada de aplicação.
 * Contém informações agregadas do jogo com dados de outras entidades.
 */
public class JogoResumo {
    
    private String id;
    private String title;
    private String slug;
    private String description;
    private double price;
    private Double originalPrice;
    private String coverImage;
    private List<String> screenshots;
    private List<String> tags;
    private String developerId;
    private String developerName;
    private double rating;
    private int reviewCount;
    private String releaseDate;
    private boolean isEarlyAccess;
    private boolean hasAdultContent;
    private boolean modsEnabled;
    private int downloadCount;

    public JogoResumo() {}

    /**
     * Factory method para criar um JogoResumo a partir de uma entidade Jogo
     * com dados agregados de outras fontes.
     */
    public static JogoResumo fromJogo(Jogo jogo, String developerName, double rating, int reviewCount) {
        JogoResumo resumo = new JogoResumo();
        resumo.id = jogo.getId().getValue();
        resumo.title = jogo.getTitulo();
        resumo.slug = jogo.getSlug().getValor();
        resumo.description = jogo.getDescricao();
        resumo.price = 0; // TODO: implementar preço quando houver
        resumo.originalPrice = null;
        resumo.coverImage = jogo.getCapaOficial() != null ? jogo.getCapaOficial().toString() : null;
        resumo.screenshots = jogo.getScreenshots().stream()
                .map(URL::toString)
                .collect(Collectors.toList());
        resumo.tags = jogo.getTags().stream()
                .map(Tag::getNome)
                .collect(Collectors.toList());
        resumo.developerId = jogo.getDesenvolvedoraId().getValue();
        resumo.developerName = developerName;
        resumo.rating = rating;
        resumo.reviewCount = reviewCount;
        resumo.releaseDate = jogo.getDataDeLancamento() != null 
                ? jogo.getDataDeLancamento().toString() 
                : null;
        resumo.isEarlyAccess = jogo.getStatus() == StatusPublicacao.AGUARDANDO_VALIDACAO;
        resumo.hasAdultContent = jogo.isNSFW();
        resumo.modsEnabled = true; // TODO: implementar quando houver
        resumo.downloadCount = 0; // TODO: implementar quando houver
        return resumo;
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Double getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(Double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public List<String> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getDeveloperName() {
        return developerName;
    }

    public void setDeveloperName(String developerName) {
        this.developerName = developerName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public boolean isEarlyAccess() {
        return isEarlyAccess;
    }

    public void setEarlyAccess(boolean earlyAccess) {
        isEarlyAccess = earlyAccess;
    }

    public boolean isHasAdultContent() {
        return hasAdultContent;
    }

    public void setHasAdultContent(boolean hasAdultContent) {
        this.hasAdultContent = hasAdultContent;
    }

    public boolean isModsEnabled() {
        return modsEnabled;
    }

    public void setModsEnabled(boolean modsEnabled) {
        this.modsEnabled = modsEnabled;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }
}
