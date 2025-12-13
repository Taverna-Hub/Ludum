package org.ludum.backend.apresentacao.controllers;

import org.ludum.aplicacao.catalogo.jogo.JogoResumo;
import org.ludum.aplicacao.catalogo.jogo.JogoServicoAplicacao;
import org.ludum.backend.apresentacao.dto.JogoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/jogos")
public class JogoController {

    private final JogoServicoAplicacao jogoServicoAplicacao;

    public JogoController(JogoServicoAplicacao jogoServicoAplicacao) {
        this.jogoServicoAplicacao = jogoServicoAplicacao;
    }

    @GetMapping
    public ResponseEntity<List<JogoResponse>> listarJogos() {
        List<JogoResumo> jogos = jogoServicoAplicacao.listarJogosPublicados();
        
        List<JogoResponse> response = jogos.stream()
                .map(JogoResponse::fromResumo)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{idOuSlug}")
    public ResponseEntity<JogoResponse> obterJogo(@PathVariable("idOuSlug") String idOuSlug) {
        return jogoServicoAplicacao.obterPorIdOuSlug(idOuSlug)
                .map(JogoResponse::fromResumo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
