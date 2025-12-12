package org.ludum.backend.apresentacao.dto;

import java.time.LocalDate;
import java.util.List;

public class CriarJogoRequest {
    
    private String titulo;
    private String descricao;
    private String capaOficial;
    private List<String> tags;
    private List<String> screenshots;
    private List<String> videos;
    private boolean isNSFW;
    private LocalDate dataDeLancamento;
    
    public CriarJogoRequest() {
    }
    
    public CriarJogoRequest(String titulo, String descricao, String capaOficial, 
                           List<String> tags, List<String> screenshots, 
                           List<String> videos, boolean isNSFW, LocalDate dataDeLancamento) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.capaOficial = capaOficial;
        this.tags = tags;
        this.screenshots = screenshots;
        this.videos = videos;
        this.isNSFW = isNSFW;
        this.dataDeLancamento = dataDeLancamento;
    }
    
    public String getTitulo() {
        return titulo;
    }
    
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public String getCapaOficial() {
        return capaOficial;
    }
    
    public void setCapaOficial(String capaOficial) {
        this.capaOficial = capaOficial;
    }
    
    public List<String> getTags() {
        return tags;
    }
    
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    
    public List<String> getScreenshots() {
        return screenshots;
    }
    
    public void setScreenshots(List<String> screenshots) {
        this.screenshots = screenshots;
    }
    
    public List<String> getVideos() {
        return videos;
    }
    
    public void setVideos(List<String> videos) {
        this.videos = videos;
    }
    
    public boolean isNSFW() {
        return isNSFW;
    }
    
    public void setNSFW(boolean NSFW) {
        isNSFW = NSFW;
    }
    
    public LocalDate getDataDeLancamento() {
        return dataDeLancamento;
    }
    
    public void setDataDeLancamento(LocalDate dataDeLancamento) {
        this.dataDeLancamento = dataDeLancamento;
    }
}
