package org.ludum.backend.apresentacao.controllers;

import org.ludum.aplicacao.catalogo.PublicarJogoUseCase;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.services.ResultadoPublicacao;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/jogos")
public class JogoController {
    
    private final PublicarJogoUseCase publicarJogoUseCase;
    
    public JogoController(PublicarJogoUseCase publicarJogoUseCase) {
        this.publicarJogoUseCase = publicarJogoUseCase;
    }
    
    @PostMapping("/{id}/publicar")
    public ResponseEntity<Map<String, Object>> publicar(@PathVariable String id) {
        // TODO: Extrair devId do usuário autenticado via SecurityContext
        ContaId devId = new ContaId("dev-mock-id");
        JogoId jogoId = new JogoId(id);
        
        ResultadoPublicacao resultado = publicarJogoUseCase.executar(devId, jogoId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("sucesso", resultado.isSucesso());
        response.put("mensagem", resultado.getMensagem());
        
        if (resultado.isFalha()) {
            response.put("erros", resultado.getErros());
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}/validar-publicacao")
    public ResponseEntity<Map<String, Object>> validarPublicacao(@PathVariable String id) {
        JogoId jogoId = new JogoId(id);
        ResultadoPublicacao resultado = publicarJogoUseCase.validar(jogoId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("valido", resultado.isSucesso());
        response.put("mensagem", resultado.getMensagem());
        
        if (resultado.temErros()) {
            response.put("erros", resultado.getErros());
        }
        
        return ResponseEntity.ok(response);
    }
}
