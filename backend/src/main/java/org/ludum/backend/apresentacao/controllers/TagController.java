package org.ludum.backend.apresentacao.controllers;

import org.ludum.aplicacao.tag.TagResumo;
import org.ludum.aplicacao.tag.TagServicoConsulta;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagServicoConsulta tagServicoConsulta;

    public TagController(TagServicoConsulta tagServicoConsulta) {
        this.tagServicoConsulta = tagServicoConsulta;
    }

    @GetMapping
    public ResponseEntity<List<TagResumo>> listarTodas() {
        List<TagResumo> tags = tagServicoConsulta.listarTodas();
        return ResponseEntity.ok(tags);
    }
}
