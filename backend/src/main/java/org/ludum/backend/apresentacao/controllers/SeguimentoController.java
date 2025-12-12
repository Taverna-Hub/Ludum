package org.ludum.backend.apresentacao.controllers;

import org.ludum.backend.apresentacao.dto.SeguirRequest;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.seguimento.entities.AlvoId;
import org.ludum.dominio.identidade.seguimento.services.RelacionamentoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seguimentos")
public class SeguimentoController {

    private final RelacionamentoService relacionamentoService;

    public SeguimentoController(RelacionamentoService relacionamentoService) {
        this.relacionamentoService = relacionamentoService;
    }

    @PostMapping
    public ResponseEntity<Void> seguir(
            @RequestBody SeguirRequest request,
            HttpServletRequest httpRequest) {
        
        String userId = (String) httpRequest.getAttribute("userId");
        ContaId seguidorId = new ContaId(userId);
        AlvoId alvoId = new AlvoId(request.getAlvoId());
        
        relacionamentoService.seguirAlvo(seguidorId, alvoId, request.getTipoAlvo());
        
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{alvoId}")
    public ResponseEntity<Void> deixarDeSeguir(
            @PathVariable String alvoId,
            HttpServletRequest httpRequest) {
        
        String userId = (String) httpRequest.getAttribute("userId");
        ContaId seguidorId = new ContaId(userId);
        AlvoId aId = new AlvoId(alvoId);
        
        relacionamentoService.deixarDeSeguirAlvo(seguidorId, aId);
        
        return ResponseEntity.ok().build();
    }
}
