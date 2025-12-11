package org.ludum.backend.apresentacao.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pagamento")
public class PagamentoController {

  @GetMapping("/status/{transacaoId}")
  public ResponseEntity<Map<String, Object>> consultarStatusPagamento(
      @PathVariable String transacaoId) {

    Map<String, Object> response = new HashMap<>();
    response.put("transacaoId", transacaoId);
    response.put("status", "PROCESSANDO");
    response.put("mensagem", "Consulte o status do pagamento diretamente no gateway");

    return ResponseEntity.ok(response);
  }

  @GetMapping("/metodos")
  public ResponseEntity<Map<String, Object>> listarMetodosPagamento() {
    Map<String, Object> response = new HashMap<>();
    response.put("cartaoCredito", true);
    response.put("pix", true);
    response.put("boleto", false);
    response.put("gateway", "Asaas");

    return ResponseEntity.ok(response);
  }
}
