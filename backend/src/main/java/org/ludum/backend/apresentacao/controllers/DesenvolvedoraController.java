package org.ludum.backend.apresentacao.controllers;

import org.ludum.aplicacao.identidade.conta.DesenvolvedoraResumo;
import org.ludum.aplicacao.identidade.conta.DesenvolvedoraServicoConsulta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/desenvolvedoras")
public class DesenvolvedoraController {

    private final DesenvolvedoraServicoConsulta desenvolvedoraServicoConsulta;

    public DesenvolvedoraController(DesenvolvedoraServicoConsulta desenvolvedoraServicoConsulta) {
        this.desenvolvedoraServicoConsulta = desenvolvedoraServicoConsulta;
    }

    @GetMapping
    public ResponseEntity<List<DesenvolvedoraResumo>> listarTodas() {
        List<DesenvolvedoraResumo> desenvolvedoras = desenvolvedoraServicoConsulta.listarTodas();
        return ResponseEntity.ok(desenvolvedoras);
    }
}
