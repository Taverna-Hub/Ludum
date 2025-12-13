package org.ludum.backend.apresentacao.controllers;

import org.ludum.backend.apresentacao.dto.ComprarJogoRequest;
import org.ludum.backend.apresentacao.dto.ComprarJogoResponse;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.OperacoesFinanceirasService;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/jogos")
public class ComprarJogosController {

    private static final Logger logger = LoggerFactory.getLogger(ComprarJogosController.class);

    private final OperacoesFinanceirasService operacoesFinanceirasService;
    private final CarteiraRepository carteiraRepository;

    public ComprarJogosController(OperacoesFinanceirasService operacoesFinanceirasService,
                                  CarteiraRepository carteiraRepository) {
        this.operacoesFinanceirasService = operacoesFinanceirasService;
        this.carteiraRepository = carteiraRepository;
    }

    @PostMapping(value = "/comprar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ComprarJogoResponse> comprarJogo(@RequestBody ComprarJogoRequest request) {
        logger.info("Recebendo requisição de compra: jogoId={}, compradorId={}, desenvolvedoraId={}, valor={}",
                request.getJogoId(), request.getCompradorId(), request.getDesenvolvedoraId(), request.getValor());

        try {
            ContaId compradorId = new ContaId(request.getCompradorId());
            ContaId desenvolvedoraId = new ContaId(request.getDesenvolvedoraId());
            JogoId jogoId = new JogoId(request.getJogoId());

            // Obter ou criar carteiras
            Carteira carteiraComprador = operacoesFinanceirasService.obterOuCriarCarteira(compradorId);
            Carteira carteiraDesenvolvedora = operacoesFinanceirasService.obterOuCriarCarteira(desenvolvedoraId);

            // Realizar compra (inclui adição à biblioteca)
            boolean sucesso = operacoesFinanceirasService.comprarJogo(
                    carteiraComprador,
                    carteiraDesenvolvedora,
                    request.getValor(),
                    jogoId);

            if (sucesso) {
                logger.info("Compra realizada com sucesso para jogo: {}", request.getJogoId());
                return ResponseEntity.ok(ComprarJogoResponse.sucesso(request.getJogoId()));
            } else {
                // Calcular saldo faltante
                BigDecimal saldoAtual = carteiraComprador.getSaldo().getDisponivel();
                BigDecimal valorFaltante = request.getValor().subtract(saldoAtual);
                
                logger.warn("Saldo insuficiente para compra do jogo: {}. Saldo: {}, Faltante: {}", 
                        request.getJogoId(), saldoAtual, valorFaltante);
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ComprarJogoResponse.saldoInsuficiente(saldoAtual, valorFaltante));
            }

        } catch (Exception e) {
            logger.error("Erro ao processar compra do jogo {}: {}", request.getJogoId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ComprarJogoResponse.falha("Erro ao processar compra: " + e.getMessage()));
        }
    }
}
