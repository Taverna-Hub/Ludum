package org.ludum.backend.apresentacao.controllers;

import jakarta.validation.Valid;

import org.ludum.backend.apresentacao.dto.AdicionarSaldoRequest;
import org.ludum.backend.apresentacao.dto.CarteiraResponse;
import org.ludum.backend.apresentacao.dto.PagamentoResponse;
import org.ludum.backend.apresentacao.dto.SaqueRequest;
import org.ludum.backend.apresentacao.dto.TransacaoResponse;
import org.ludum.dominio.financeiro.carteira.OperacoesFinanceirasService;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.enums.TipoTransacao;

import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/carteira")
public class CarteiraController {

  private static final Logger logger = LoggerFactory.getLogger(CarteiraController.class);

  private final OperacoesFinanceirasService operacoesFinanceirasService;
  private final TransacaoRepository transacaoRepository;

  public CarteiraController(OperacoesFinanceirasService operacoesFinanceirasService,
                          TransacaoRepository transacaoRepository) {
    this.operacoesFinanceirasService = operacoesFinanceirasService;
    this.transacaoRepository = transacaoRepository;
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

  @GetMapping(value = "/{contaId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CarteiraResponse> obterCarteira(@PathVariable("contaId") String contaIdStr) {
    logger.info("Recebendo requisição para obter carteira da conta: {}", contaIdStr);
    try {
      ContaId contaId = new ContaId(contaIdStr);
      Carteira carteira = operacoesFinanceirasService.obterCarteira(contaId);

      if (carteira == null) {
        logger.warn("Carteira não encontrada para conta: {}", contaIdStr);
        return ResponseEntity.ok(new CarteiraResponse(
            contaIdStr,
            BigDecimal.ZERO,
            BigDecimal.ZERO,
            false,
                null));
      }

      CarteiraResponse response = new CarteiraResponse(
          carteira.getId().getValue(),
          carteira.getSaldo().getDisponivel(),
          carteira.getSaldo().getBloqueado(),
          carteira.isContaExternaValida(),
              carteira.getContaExterna());
      logger.info("Carteira obtida com sucesso: id={}, disponivel={}, bloqueado={}", 
          response.getId(), response.getDisponivel(), response.getBloqueado());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      logger.error("Erro ao obter carteira para conta {}: {}", contaIdStr, e.getMessage(), e);
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

  @PutMapping("/{contaId}/chave-pix")
  public ResponseEntity<?> atualizarChavePix(
      @PathVariable("contaId") String contaIdStr,
      @RequestParam("chavePix") String chavePix) {
    
    logger.info("Atualizando chave PIX para conta: {}, chave: {}", contaIdStr, chavePix);
    
    try {
      ContaId contaId = new ContaId(contaIdStr);
      operacoesFinanceirasService.atualizarChavePix(contaId, chavePix);
      logger.info("Chave PIX atualizada com sucesso para conta: {}", contaIdStr);
      
      java.util.Map<String, String> response = new java.util.HashMap<>();
      response.put("mensagem", "Chave PIX atualizada com sucesso");
      return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
      logger.error("Erro de validação ao atualizar chave PIX: {}", e.getMessage());
      java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
      errorResponse.put("mensagem", e.getMessage());
      return ResponseEntity.badRequest().body(errorResponse);
    } catch (Exception e) {
      logger.error("Erro ao atualizar chave PIX: {}", e.getMessage(), e);
      java.util.Map<String, String> errorResponse = new java.util.HashMap<>();
      errorResponse.put("mensagem", "Erro ao atualizar chave PIX: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
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

  @PostMapping("/{contaId}/sacar")
  public ResponseEntity<PagamentoResponse> solicitarSaque(
      @PathVariable("contaId") String contaIdStr,
      @Valid @RequestBody SaqueRequest request) {
    
    logger.info("Recebendo solicitação de saque para conta: {}, valor: {}", contaIdStr, request.getValor());
    
    try {
      ContaId contaId = new ContaId(contaIdStr);
      Carteira carteira = operacoesFinanceirasService.obterCarteira(contaId);

      if (carteira == null) {
        logger.warn("Carteira não encontrada para conta: {}", contaIdStr);
        PagamentoResponse errorResponse = PagamentoResponse.falha("Carteira não encontrada");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
      }

      // Utiliza a data atual como data da venda (pode ser ajustado conforme necessidade)
      java.util.Date dataVenda = request.getDataVenda() != null ? 
          request.getDataVenda() : new java.util.Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);

      boolean sucesso = operacoesFinanceirasService.solicitarSaque(
          carteira,
          request.getValor(),
          dataVenda,
          request.isCrowdfunding(),
          request.isMetaAtingida()
      );

      if (sucesso) {
        logger.info("Saque realizado com sucesso para conta: {}", contaIdStr);
        PagamentoResponse response = PagamentoResponse.sucesso(request.getValor());
        return ResponseEntity.ok(response);
      } else {
        logger.warn("Falha ao realizar saque para conta: {}", contaIdStr);
        PagamentoResponse response = PagamentoResponse.falha("Falha ao processar saque");
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
      }
      
    } catch (IllegalArgumentException | IllegalStateException e) {
      logger.error("Erro de validação ao processar saque: {}", e.getMessage());
      PagamentoResponse errorResponse = PagamentoResponse.falha(e.getMessage());
      return ResponseEntity.badRequest().body(errorResponse);
    } catch (RuntimeException e) {
      logger.error("Erro ao processar saque: {}", e.getMessage(), e);
      PagamentoResponse errorResponse = PagamentoResponse.falha(
          "Erro ao processar saque: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
  }

  @GetMapping(value = "/{contaId}/transacoes", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<TransacaoResponse>> obterTransacoes(@PathVariable("contaId") String contaIdStr) {
    logger.info("Recebendo requisição para obter transações da conta: {}", contaIdStr);
    try {
      ContaId contaId = new ContaId(contaIdStr);
      List<Transacao> transacoes = transacaoRepository.obterPorContaId(contaId);

      List<TransacaoResponse> responses = transacoes.stream()
          .filter(t -> isTransacaoRelevante(t, contaId))
          .map(t -> {
            String descricao = gerarDescricao(t, contaId);
            return new TransacaoResponse(
                t.getTransacaoId().getValue(),
                t.getTipo().name(),
                t.getStatus().name(),
                t.getData(),
                t.getValor(),
                t.getContaOrigem() != null ? t.getContaOrigem().getValue() : null,
                t.getContaDestino() != null ? t.getContaDestino().getValue() : null,
                descricao
            );
          })
          .collect(Collectors.toList());

      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  private boolean isTransacaoRelevante(Transacao transacao, ContaId contaAtual) {
    if (transacao.getTipo() == TipoTransacao.CREDITO) {
      return transacao.getContaDestino() != null && transacao.getContaDestino().equals(contaAtual);
    }
    
    if (transacao.getTipo() == TipoTransacao.DEBITO ||
        transacao.getTipo() == TipoTransacao.SAQUE) {
      return transacao.getContaOrigem() != null && transacao.getContaOrigem().equals(contaAtual);
    }
    
    return true;
  }

  private String gerarDescricao(Transacao transacao, ContaId contaAtual) {
    switch (transacao.getTipo()) {
      case CREDITO:
        if (transacao.getContaOrigem() == null) {
          return "Adição de saldo";
        }
        return "Recebimento";
      case DEBITO:
        return "Compra de jogo";
      case SAQUE:
        return "Saque";
      default:
        return "Transação";
    }
  }
}
