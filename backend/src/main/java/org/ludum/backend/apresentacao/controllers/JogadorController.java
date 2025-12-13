package org.ludum.backend.apresentacao.controllers;

import org.ludum.aplicacao.identidade.conta.JogadorResumo;
import org.ludum.aplicacao.identidade.conta.JogadorServicoConsulta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/jogadores")
public class JogadorController {

    private final JogadorServicoConsulta jogadorServicoConsulta;

    public JogadorController(JogadorServicoConsulta jogadorServicoConsulta) {
        this.jogadorServicoConsulta = jogadorServicoConsulta;
    }

    @GetMapping
    public ResponseEntity<List<JogadorResumo>> listarTodos() {
        List<JogadorResumo> jogadores = jogadorServicoConsulta.listarTodos();
        return ResponseEntity.ok(jogadores);
    }
}
