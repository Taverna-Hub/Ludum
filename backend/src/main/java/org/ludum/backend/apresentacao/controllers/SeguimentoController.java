package org.ludum.backend.apresentacao.controllers;

import org.ludum.aplicacao.identidade.seguimento.SeguimentoResumo;
import org.ludum.aplicacao.identidade.seguimento.SeguimentoServicoAplicacao;
import org.ludum.backend.apresentacao.dto.SeguimentoResponse;
import org.ludum.backend.apresentacao.dto.SeguirRequest;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.seguimento.entities.AlvoId;
import org.ludum.dominio.identidade.seguimento.services.RelacionamentoService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seguimentos")
public class SeguimentoController {

    private final RelacionamentoService relacionamentoService;
    private final SeguimentoServicoAplicacao seguimentoServicoAplicacao;

    public SeguimentoController(
            RelacionamentoService relacionamentoService,
            SeguimentoServicoAplicacao seguimentoServicoAplicacao) {
        this.relacionamentoService = relacionamentoService;
        this.seguimentoServicoAplicacao = seguimentoServicoAplicacao;
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
            @PathVariable("alvoId") String alvoId,
            HttpServletRequest httpRequest) {
        
        String userId = (String) httpRequest.getAttribute("userId");
        ContaId seguidorId = new ContaId(userId);
        AlvoId aId = new AlvoId(alvoId);
        
        relacionamentoService.deixarDeSeguirAlvo(seguidorId, aId);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/seguindo")
    public ResponseEntity<List<SeguimentoResponse>> listarSeguindo(HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        List<SeguimentoResumo> seguimentos = seguimentoServicoAplicacao.listarSeguindo(userId);
        List<SeguimentoResponse> response = seguimentos.stream()
                .map(SeguimentoResponse::fromResumo)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/seguidores")
    public ResponseEntity<List<SeguimentoResponse>> listarSeguidores(HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        List<SeguimentoResumo> seguimentos = seguimentoServicoAplicacao.listarSeguidores(userId);
        List<SeguimentoResponse> response = seguimentos.stream()
                .map(SeguimentoResponse::fromResumo)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verificar/{alvoId}")
    public ResponseEntity<Boolean> verificarSeguindo(
            @PathVariable("alvoId") String alvoId,
            HttpServletRequest httpRequest) {
        String userId = (String) httpRequest.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        
        boolean estaSeguindo = seguimentoServicoAplicacao.estaSeguindo(userId, alvoId);
        return ResponseEntity.ok(estaSeguindo);
    }

    @GetMapping("/contar/{alvoId}")
    public ResponseEntity<Long> contarSeguidores(@PathVariable("alvoId") String alvoId) {
        long total = seguimentoServicoAplicacao.contarSeguidores(alvoId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/contar-seguindo/{seguidorId}")
    public ResponseEntity<Long> contarSeguindo(@PathVariable("seguidorId") String seguidorId) {
        long total = seguimentoServicoAplicacao.contarSeguindo(seguidorId);
        return ResponseEntity.ok(total);
    }
}
