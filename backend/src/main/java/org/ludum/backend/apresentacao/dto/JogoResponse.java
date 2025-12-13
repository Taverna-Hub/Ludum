package org.ludum.backend.apresentacao.dto;

import org.ludum.aplicacao.catalogo.jogo.JogoResumo;

import java.util.List;

/**
 * DTO de resposta HTTP para jogos.
 * Apenas converte do JogoResumo da camada de aplicação.
 */
public class JogoResponse {
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

    public JogoResponse() {}

    /**
     * Converte um JogoResumo da camada de aplicação para JogoResponse.
     */
    public static JogoResponse fromResumo(JogoResumo resumo) {
        JogoResponse response = new JogoResponse();
        response.id = resumo.getId();
        response.title = resumo.getTitle();
        response.slug = resumo.getSlug();
        response.description = resumo.getDescription();
        response.price = resumo.getPrice();
        response.originalPrice = resumo.getOriginalPrice();
        response.coverImage = resumo.getCoverImage();
        response.screenshots = resumo.getScreenshots();
        response.tags = resumo.getTags();
        response.developerId = resumo.getDeveloperId();
        response.developerName = resumo.getDeveloperName();
        response.rating = resumo.getRating();
        response.reviewCount = resumo.getReviewCount();
        response.releaseDate = resumo.getReleaseDate();
        response.isEarlyAccess = resumo.isEarlyAccess();
        response.hasAdultContent = resumo.isHasAdultContent();
        response.modsEnabled = resumo.isModsEnabled();
        response.downloadCount = resumo.getDownloadCount();
        return response;
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
