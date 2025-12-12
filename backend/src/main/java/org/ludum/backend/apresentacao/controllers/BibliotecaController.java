package org.ludum.backend.apresentacao.controllers;

import org.ludum.backend.apresentacao.dto.AdicionarJogoRequest;
import org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.dominio.catalogo.biblioteca.services.BibliotecaService;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.PacoteZip;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/biblioteca")
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
}
