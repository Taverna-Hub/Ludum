package org.ludum.backend.apresentacao.controllers;

import jakarta.transaction.Transactional;
import org.ludum.backend.apresentacao.dto.CriarJogoRequest;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.services.PublicacaoService;
import org.ludum.dominio.catalogo.tag.TagRepository;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/publicacoes")
public class PublicacaoController {

    private final PublicacaoService publicacaoService;
    private final TagRepository tagRepository;
    
    public PublicacaoController(PublicacaoService publicacaoService, TagRepository tagRepository) {
        this.publicacaoService = publicacaoService;
        this.tagRepository = tagRepository;
    }

    @PostMapping("/publicar")
    @Transactional
    public ResponseEntity<Map<String, Object>> publicarJogo(@RequestBody CriarJogoRequest request) throws Exception {
        ContaId devId = new ContaId(request.getDesenvolvedoraId());

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
                request.getDataDeLancamento());

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "sucesso", true,
                "jogoId", jogo.getId().getValue(),
                "mensagem", "Jogo publicado com sucesso",
                "status", jogo.getStatus().toString()));
    }

    @PostMapping("/{id}/validar")
    @Transactional
    public ResponseEntity<Map<String, Object>> validar(@PathVariable("id") String id) {
        JogoId jogoId = new JogoId(id);
        
        publicacaoService.validarEPublicar(jogoId);
        
        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Jogo validado e publicado com sucesso",
                "status", "PUBLICADO"));
    }

    @PostMapping("/{id}/rejeitar")
    @Transactional
    public ResponseEntity<Map<String, Object>> rejeitar(
            @PathVariable("id") String id,
            @RequestBody Map<String, String> body) {
        JogoId jogoId = new JogoId(id);
        String motivo = body.get("motivo");

        publicacaoService.rejeitarJogo(jogoId, motivo);

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Jogo rejeitado com sucesso"));
    }

    @PostMapping("/{id}/arquivar")
    @Transactional
    public ResponseEntity<Map<String, Object>> arquivar(
            @PathVariable("id") String id,
            @RequestBody Map<String, String> body) {
        ContaId devId = new ContaId(body.get("desenvolvedoraId"));
        JogoId jogoId = new JogoId(id);

        publicacaoService.arquivarJogo(devId, jogoId);

        return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", "Jogo arquivado com sucesso"));
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
                // Buscar tag existente no banco pelo nome
                Tag tag = tagRepository.obterPorNome(tagNome);
                if (tag == null) {
                    throw new IllegalArgumentException("Tag não encontrada: " + tagNome);
                }
                tags.add(tag);
            }
        }
        return tags;
    }
}
