package org.ludum.dominio.financeiro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.util.Date;

import org.ludum.financeiro.carteira.OperacoesFinanceirasService;
import org.ludum.financeiro.carteira.entidades.Carteira;
import org.ludum.financeiro.carteira.entidades.Saldo;
import org.ludum.identidade.conta.entities.ContaId;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ComprarJogoFuncionalidade {

    private ContaId conta = new ContaId("comprador");
    private Saldo saldo = new Saldo();
    private Carteira carteira = new Carteira(conta, saldo);

    private ContaId contaDesenvolvedor = new ContaId("desenvolvedor");
    private Saldo saldoDesenvolvedor = new Saldo();
    private Carteira carteiraDesenvolvedor = new Carteira(contaDesenvolvedor, saldoDesenvolvedor);

    private OperacoesFinanceirasService operacoesService;
    private boolean operacaoSucesso;
    private BigDecimal valorJogo;
    private boolean jogoPublicado;
    private boolean arquivoDisponivel;
    private boolean formaPagamentoValida;
    private boolean jaPossuiJogo;
    private boolean jogoBaixado;
    private Date dataCompra;

    // Compra com saldo + pagamento complementar (positivo)
    @Given("que sou um usuário com saldo de R$20")
    public void que_sou_um_usuario_com_saldo_de_r_20() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(20), true);
    }

    @And("o jogo está publicado e custa R$50")
    public void o_jogo_esta_publicado_e_custa_r_50() {
        valorJogo = BigDecimal.valueOf(50);
        jogoPublicado = true;
    }

    @And("minha forma de pagamento complementar autoriza a cobrança de R$30")
    public void minha_forma_de_pagamento_complementar_autoriza_a_cobranca_de_r_30() {
        formaPagamentoValida = true;
    }

    @When("finalizo a compra usando R$20 de saldo e cobrando R$30 no cartão")
    public void finalizo_a_compra_usando_r_20_de_saldo_e_cobrando_r_30_no_cartao() {
        operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(30) ,true);
        operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, BigDecimal.valueOf(20));
    }

    @Then("a compra é concluída, o jogo é adicionado à minha biblioteca e meu saldo é zerado")
    public void a_compra_e_concluida_o_jogo_e_adicionado_a_minha_biblioteca_e_meu_saldo_e_zerado() {
        assertTrue(operacaoSucesso);
        assertEquals(BigDecimal.ZERO, carteira.getSaldo().getDisponivel());
        // verificar se jogo foi adicionado à biblioteca 
    }

    // Falha na forma de pagamento complementar (negativo)
    @And("a tentativa de cobrar R$30 no cartão é recusada")
    public void a_tentativa_de_cobrar_r_30_no_cartao_e_recusada() {
        formaPagamentoValida = false;
    }

    @When("finalizo a compra")
    public void finalizo_a_compra() {
        if (!formaPagamentoValida) {
            operacaoSucesso = false;
        } else {
            operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, valorJogo);
        }
    }

    @Then("a compra deve falhar e meu saldo permanece R$20")
    public void a_compra_deve_falhar_e_meu_saldo_permanece_r_20() {
        assertFalse(operacaoSucesso);
        assertEquals(BigDecimal.valueOf(20), carteira.getSaldo().getDisponivel());
    }

    // Scenario: Compra de jogo publicado com arquivo disponível (positivo)
    @Given("que o jogo está publicado, disponível para venda e o arquivo do jogo está presente no repositório")
    public void que_o_jogo_esta_publicado_disponivel_para_venda_e_o_arquivo_do_jogo_esta_presente_no_repositorio() {
        jogoPublicado = true;
        arquivoDisponivel = true;
    }

    @And("eu tenho forma de pagamento válida")
    public void eu_tenho_forma_de_pagamento_valida() {
        formaPagamentoValida = true;
    }

    @When("eu realizo a compra no valor de R$20")
    public void eu_realizo_a_compra() {
        operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, valorJogo);
    }

    @Then("a compra é concluída com sucesso e recebo acesso para baixar o jogo")
    public void a_compra_e_concluida_com_sucesso_e_recebo_acesso_para_baixar_o_jogo() {
        assertTrue(operacaoSucesso);
        // verificar acesso ao download
    }

    // Scenario: Tentativa de comprar jogo publicado sem arquivo disponível (negativo)
    @And("o arquivo binário/instalador do jogo NÃO está disponível no repositório (arquivo ausente)")
    public void o_arquivo_binario_instalador_do_jogo_nao_esta_disponivel_no_repositorio_arquivo_ausente() {
        arquivoDisponivel = false;
    }

    @When("tento realizar a compra")
    public void tento_realizar_a_compra() {
        if (!arquivoDisponivel) {
            operacaoSucesso = false;
        } else {
            operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, valorJogo);
        }
    }

    @Then("a transação é bloqueada e nenhuma cobrança é realizada")
    public void a_transacao_e_bloqueada_e_nenhuma_cobranca_e_realizada() {
        assertFalse(operacaoSucesso);
    }

    // Primeiro pagamento — usuário não possui o jogo (positivo)
    @Given("que não sou proprietário do jogo")
    public void que_nao_sou_proprietario_do_jogo() {
        jaPossuiJogo = false;
    }

    @And("o jogo está publicado e custa R$30")
    public void o_jogo_esta_publicado_e_custa_r_30() {
        valorJogo = BigDecimal.valueOf(30);
        jogoPublicado = true;
    }

    @When("compro o jogo")
    public void compro_o_jogo() {
        operacoesService.adicionarSaldo(carteira, valorJogo, true);
        operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, valorJogo);
    }

    @Then("a compra é confirmada e o jogo aparece na minha biblioteca")
    public void a_compra_e_confirmada_e_o_jogo_aparece_na_minha_biblioteca() {
        assertTrue(operacaoSucesso);
        // verificar biblioteca
    }

    // Tentar comprar jogo já adquirido (negativo)
    @Given("que já sou proprietário do jogo")
    public void que_ja_sou_proprietario_do_jogo() {
        jaPossuiJogo = true;
    }

    @When("tento comprar o mesmo jogo novamente")
    public void tento_comprar_o_mesmo_jogo_novamente() {
        if (jaPossuiJogo) {
            operacaoSucesso = false;
        } else {
        operacoesService.adicionarSaldo(carteira, valorJogo, true);
            operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, valorJogo);
        }
    }

    @Then("a plataforma retorna erro e nenhuma cobrança é feita")
    public void a_plataforma_retorna_erro_e_nenhuma_cobranca_e_feita() {
        assertFalse(operacaoSucesso);
    }

    // Reembolso válido (menos de 24h e não baixado) (positivo)
    @Given("que comprei um jogo há menos de 24h")
    public void que_comprei_um_jogo_ha_menos_de_24h() {
        dataCompra = new Date(System.currentTimeMillis() - 23 * 60 * 60 * 1000); 
    }

    @And("ainda não baixei o jogo")
    public void ainda_nao_baixei_o_jogo() {
        jogoBaixado = false;
    }

    @When("solicito reembolso")
    public void solicito_reembolso() {
        operacaoSucesso = operacoesService.solicitarReembolso(carteira, valorJogo, dataCompra);
    }

    @Then("devo receber o valor de volta pela forma original (ou crédito, conforme política) e a compra deve ser registrada como reembolsada")
    public void devo_receber_o_valor_de_volta_pela_forma_original_ou_credito_conforme_politica_e_a_compra_deve_ser_registrada_como_reembolsada() {
        assertTrue(operacaoSucesso);
        assertEquals(carteira.getSaldo().getDisponivel().add(valorJogo), carteira.getSaldo().getDisponivel());
    }

    // Reembolso negado após download (negativo)
    @Given("que já baixei o jogo")
    public void que_ja_baixei_o_jogo() {
        jogoBaixado = true;
    }

    @Then("o sistema deve impedir o reembolso")
    public void o_sistema_deve_impedir_o_reembolso() {
        assertFalse(operacaoSucesso);
    }

    // Reembolso fora do prazo (>24h) (negativo)
    @Given("que comprei o jogo há mais de 24h")
    public void que_comprei_o_jogo_ha_mais_de_24h() {
        dataCompra = new Date(System.currentTimeMillis() - 25 * 60 * 60 * 1000); // 25 horas atrás
    }

    @Then("o sistema deve impedir o reembolso")
    public void o_sistema_deve_impedir_o_reembolso_2() {
        assertFalse(operacaoSucesso);
    }
}
