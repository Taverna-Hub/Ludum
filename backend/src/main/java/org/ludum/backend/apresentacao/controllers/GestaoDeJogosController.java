package org.ludum.backend.apresentacao.controllers;

import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.PacoteZip;
import org.ludum.dominio.catalogo.jogo.entidades.VersaoId;
import org.ludum.dominio.catalogo.jogo.services.GestaoDeJogosService;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/jogos")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*")
public class GestaoDeJogosController {

    private final GestaoDeJogosService gestaoDeJogosService;

    public GestaoDeJogosController(GestaoDeJogosService gestaoDeJogosService) {
        this.gestaoDeJogosService = gestaoDeJogosService;
    }

    @PostMapping("/upload/{jogoId}")
    public ResponseEntity<Void> uploadJogo(
            @PathVariable("jogoId") String jogoId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("nomeVersao") String nomeVersao,
            @RequestParam("descricao") String descricao,
            @RequestParam("contaId") String contaId) {

        try {
            JogoId jId = new JogoId(jogoId);
            ContaId cId = new ContaId(contaId);
            VersaoId vId = new VersaoId(UUID.randomUUID().toString());

            byte[] conteudo = file.getBytes();
            PacoteZip pacote = new PacoteZip(conteudo);

            gestaoDeJogosService.processarUpload(cId, jId, pacote, vId, nomeVersao, descricao);

            return ResponseEntity.ok().build();

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
