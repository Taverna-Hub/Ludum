package org.ludum.backend.apresentacao.controllers;

import jakarta.validation.Valid;

import org.ludum.backend.apresentacao.dto.AdicionarSaldoRequest;
import org.ludum.backend.apresentacao.dto.CarteiraResponse;
import org.ludum.backend.apresentacao.dto.PagamentoResponse;
import org.ludum.dominio.financeiro.carteira.OperacoesFinanceirasService;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/carteira")
public class CarteiraController {

  private final OperacoesFinanceirasService operacoesFinanceirasService;

  public CarteiraController(OperacoesFinanceirasService operacoesFinanceirasService) {
    this.operacoesFinanceirasService = operacoesFinanceirasService;
  }

  @PostMapping("/{contaId}/adicionar-saldo")
  public ResponseEntity<PagamentoResponse> adicionarSaldo(
      @PathVariable("contaId") String contaIdStr,
      @Valid @RequestBody AdicionarSaldoRequest request) {

    try {
      ContaId contaId = new ContaId(contaIdStr);

      boolean sucesso = operacoesFinanceirasService.adicionarSaldo(
          contaId,
          request.getValor(),
          "BRL",
          "Adição de saldo via cartão",
          request.getNomeCliente(),
          request.getCpfCnpjCliente(),
          request.getEmailCliente(),
          request.getTelefoneCliente());

      if (sucesso) {
        PagamentoResponse response = PagamentoResponse.sucesso(request.getValor());
        return ResponseEntity.ok(response);
      } else {
        PagamentoResponse response = PagamentoResponse.falha("Falha ao processar pagamento");
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
      }
    } catch (IllegalArgumentException e) {
      PagamentoResponse errorResponse = PagamentoResponse.falha(e.getMessage());
      return ResponseEntity.badRequest().body(errorResponse);
    } catch (Exception e) {
      PagamentoResponse errorResponse = PagamentoResponse.falha(
          "Erro ao processar pagamento: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
  }

  @GetMapping("/{contaId}")
  public ResponseEntity<CarteiraResponse> obterCarteira(@PathVariable("contaId") String contaIdStr) {
    try {
      ContaId contaId = new ContaId(contaIdStr);
      Carteira carteira = operacoesFinanceirasService.obterCarteira(contaId);

      if (carteira == null) {
        return ResponseEntity.ok(new CarteiraResponse(
            contaIdStr,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            false));
      }

      return ResponseEntity.ok(new CarteiraResponse(
          carteira.getId().getValue(),
          carteira.getSaldo().getDisponivel(),
          carteira.getSaldo().getBloqueado(),
          carteira.isContaExternaValida()));
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @PostMapping("/{contaId}/bloquear-saldo")
  public ResponseEntity<String> bloquearSaldo(
      @PathVariable("contaId") String contaIdStr,
      @RequestParam BigDecimal valor) {

    try {
      ContaId contaId = new ContaId(contaIdStr);
      Carteira carteira = operacoesFinanceirasService.obterOuCriarCarteira(contaId);
      operacoesFinanceirasService.bloquearSaldo(carteira, valor);
      return ResponseEntity.ok("Saldo bloqueado com sucesso");
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Erro ao bloquear saldo: " + e.getMessage());
    }
  }

  @PostMapping("/{contaId}/liberar-saldo")
  public ResponseEntity<String> liberarSaldoBloqueado(@PathVariable("contaId") String contaIdStr) {
    try {
      ContaId contaId = new ContaId(contaIdStr);
      Carteira carteira = operacoesFinanceirasService.obterCarteira(contaId);

      if (carteira == null) {
        return ResponseEntity.badRequest().body("Carteira não encontrada");
      }

      operacoesFinanceirasService.liberarSaldoBloqueado(carteira);
      return ResponseEntity.ok("Saldo liberado com sucesso");
    } catch (IllegalStateException e) {
      return ResponseEntity.badRequest().body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Erro ao liberar saldo: " + e.getMessage());
    }
  }
}
