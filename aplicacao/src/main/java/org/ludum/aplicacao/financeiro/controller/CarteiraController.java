package org.ludum.aplicacao.financeiro.controller;

import jakarta.validation.Valid;
import org.ludum.aplicacao.financeiro.dto.AdicionarSaldoRequest;
import org.ludum.aplicacao.financeiro.dto.CarteiraResponse;
import org.ludum.aplicacao.financeiro.dto.PagamentoResponse;
import org.ludum.aplicacao.financeiro.service.CarteiraService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/carteira")
public class CarteiraController {

  private final CarteiraService carteiraService;

  public CarteiraController(CarteiraService carteiraService) {
    this.carteiraService = carteiraService;
  }

  @PostMapping("/{contaId}/adicionar-saldo")
  public ResponseEntity<PagamentoResponse> adicionarSaldo(
      @PathVariable("contaId") String contaId,
      @Valid @RequestBody AdicionarSaldoRequest request) {

    try {
      PagamentoResponse response = carteiraService.adicionarSaldo(contaId, request);

      if (response.isSucesso()) {
        return ResponseEntity.ok(response);
      } else {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
      }
    } catch (IllegalArgumentException e) {
      PagamentoResponse errorResponse = PagamentoResponse.falha(null, e.getMessage());
      return ResponseEntity.badRequest().body(errorResponse);
    } catch (Exception e) {
      PagamentoResponse errorResponse = PagamentoResponse.falha(null,
          "Erro ao processar pagamento: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
  }

  @GetMapping("/{contaId}")
  public ResponseEntity<CarteiraResponse> obterCarteira(@PathVariable("contaId") String contaId) {
    try {
      CarteiraResponse response = carteiraService.obterCarteira(contaId);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/{contaId}/bloquear-saldo")
  public ResponseEntity<String> bloquearSaldo(
      @PathVariable("contaId") String contaId,
      @RequestParam BigDecimal valor) {

    try {
      carteiraService.bloquearSaldo(contaId, valor);
      return ResponseEntity.ok("Saldo bloqueado com sucesso");
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Erro ao bloquear saldo: " + e.getMessage());
    }
  }

  @PostMapping("/{contaId}/liberar-saldo")
  public ResponseEntity<String> liberarSaldoBloqueado(@PathVariable String contaId) {
    try {
      carteiraService.liberarSaldoBloqueado(contaId);
      return ResponseEntity.ok("Saldo liberado com sucesso");
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Erro ao liberar saldo: " + e.getMessage());
    }
  }
}
