package org.ludum.backend.apresentacao.controllers;

import jakarta.transaction.Transactional;
import org.ludum.backend.apresentacao.dto.CriarJogoRequest;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.services.PublicacaoService;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/jogos")
public class PublicacaoController {
    
    private final PublicacaoService publicacaoService;
    
    public PublicacaoController(PublicacaoService publicacaoService) {
        this.publicacaoService = publicacaoService;
    }
    
    @PostMapping("/publicar")
    @Transactional
    public ResponseEntity<Map<String, Object>> publicarJogo(@RequestBody CriarJogoRequest request) throws Exception {
        // TODO: Extrair devId do usuário autenticado via SecurityContext
        ContaId devId = new ContaId("dev-mock-id");
        
        URL capaOficial = parseUrl(request.getCapaOficial());
        List<URL> screenshots = parseUrls(request.getScreenshots());
        List<URL> videos = parseUrls(request.getVideos());
        List<Tag> tags = parseTags(request.getTags());
        
        Jogo jogo = publicacaoService.publicarJogo(
            devId,
            request.getTitulo(),
            request.getDescricao(),
            capaOficial,
            tags,
            screenshots,
            videos,
            request.isNSFW(),
            request.getDataDeLancamento()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
            "sucesso", true,
            "jogoId", jogo.getId().getValue(),
            "mensagem", "Jogo publicado com sucesso",
            "status", jogo.getStatus().toString()
        ));
    }
    
    @PostMapping("/{id}/rejeitar")
    @Transactional
    public ResponseEntity<Map<String, Object>> rejeitar(
            @PathVariable String id,
            @RequestBody Map<String, String> body) {
        JogoId jogoId = new JogoId(id);
        String motivo = body.get("motivo");
        
        publicacaoService.rejeitarJogo(jogoId, motivo);
        
        return ResponseEntity.ok(Map.of(
            "sucesso", true,
            "mensagem", "Jogo rejeitado com sucesso"
        ));
    }
    
    @PostMapping("/{id}/arquivar")
    @Transactional
    public ResponseEntity<Map<String, Object>> arquivar(@PathVariable String id) {
        // TODO: Extrair devId do usuário autenticado via SecurityContext
        ContaId devId = new ContaId("dev-mock-id");
        JogoId jogoId = new JogoId(id);
        
        publicacaoService.arquivarJogo(devId, jogoId);
        
        return ResponseEntity.ok(Map.of(
            "sucesso", true,
            "mensagem", "Jogo arquivado com sucesso"
        ));
    }
    
    private URL parseUrl(String urlString) throws Exception {
        return urlString != null ? URI.create(urlString).toURL() : null;
    }
    
    private List<URL> parseUrls(List<String> urlStrings) throws Exception {
        List<URL> urls = new ArrayList<>();
        if (urlStrings != null) {
            for (String urlString : urlStrings) {
                urls.add(parseUrl(urlString));
            }
        }
        return urls;
    }
    
    private List<Tag> parseTags(List<String> tagNames) {
        List<Tag> tags = new ArrayList<>();
        if (tagNames != null) {
            for (String tagNome : tagNames) {
                TagId tagId = new TagId(UUID.randomUUID().toString());
                tags.add(new Tag(tagId, tagNome));
            }
        }
        return tags;
    }
}
