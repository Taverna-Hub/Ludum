package org.ludum.backend.apresentacao.controllers;

import org.ludum.backend.apresentacao.dto.AdicionarJogoRequest;
import org.ludum.backend.apresentacao.dto.JogoResponse;
import org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.dominio.catalogo.biblioteca.services.BibliotecaService;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.PacoteZip;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/biblioteca")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class BibliotecaController {

    private final BibliotecaService bibliotecaService;

    public BibliotecaController(BibliotecaService bibliotecaService) {
        this.bibliotecaService = bibliotecaService;
    }

    @PostMapping("/adicionar")
    public ResponseEntity<Void> adicionarJogo(@RequestBody AdicionarJogoRequest request) {
        ModeloDeAcesso modelo = ModeloDeAcesso.valueOf(request.getModeloDeAcesso());
        JogoId jogoId = new JogoId(request.getJogoId());
        ContaId contaId = new ContaId(request.getContaId());
        TransacaoId transacaoId = request.getTransacaoId() != null ? new TransacaoId(request.getTransacaoId()) : null;

        bibliotecaService.adicionarJogo(modelo, jogoId, contaId, transacaoId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/download/{jogoId}")
    public ResponseEntity<Resource> downloadJogo(
            @PathVariable("jogoId") String jogoId,
            @RequestParam("contaId") String contaId) {

        JogoId id = new JogoId(jogoId);
        ContaId cId = new ContaId(contaId);

        PacoteZip pacote = bibliotecaService.processarDownload(cId, id);

        ByteArrayResource resource = new ByteArrayResource(pacote.getConteudo());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"jogo.zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(pacote.getConteudo().length)
                .body(resource);
    }

    @GetMapping("/tem-jogo/{jogoId}")
    public ResponseEntity<Boolean> verificarPosse(
            @PathVariable("jogoId") String jogoId,
            @RequestParam("contaId") String contaId) {
        JogoId jId = new JogoId(jogoId);
        ContaId cId = new ContaId(contaId);
        boolean possui = bibliotecaService.verificarPosse(cId, jId);
        return ResponseEntity.ok(possui);
    }

    @GetMapping
    public ResponseEntity<List<JogoResponse>> obterBiblioteca(
            @RequestParam("contaId") String contaId) {
        ContaId cId = new ContaId(contaId);
        List<Jogo> jogos = bibliotecaService
                .obterJogosEmBiblioteca(cId);

        List<JogoResponse> response = jogos.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private JogoResponse mapToResponse(
            Jogo jogo) {
        JogoResponse dto = new JogoResponse();
        dto.setId(jogo.getId().toString());
        dto.setTitle(jogo.getTitulo());
        dto.setSlug(jogo.getSlug().getValor());
        dto.setDescription(jogo.getDescricao());
        dto.setPrice(0.0);
        dto.setCoverImage(jogo.getCapaOficial() != null ? jogo.getCapaOficial().toString() : null);

        List<String> screenshotUrls = jogo.getScreenshots().stream()
                .map(java.net.URL::toString)
                .collect(Collectors.toList());
        dto.setScreenshots(screenshotUrls);

        List<String> tagNames = jogo.getTags().stream()
                .map(Tag::getNome)
                .collect(Collectors.toList());
        dto.setTags(tagNames);

        dto.setDeveloperId(jogo.getDesenvolvedoraId().toString());
        dto.setDeveloperName("Developer " + jogo.getDesenvolvedoraId().toString());

        dto.setReleaseDate(jogo.getDataDeLancamento() != null ? jogo.getDataDeLancamento().toString() : "");
        dto.setHasAdultContent(jogo.isNSFW());

        dto.setRating(5.0);
        dto.setReviewCount(0);
        dto.setEarlyAccess(false);
        dto.setModsEnabled(true);
        dto.setDownloadCount(0);

        return dto;
    }
}
