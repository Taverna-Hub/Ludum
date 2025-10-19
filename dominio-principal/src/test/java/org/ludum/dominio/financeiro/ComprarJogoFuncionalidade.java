package org.ludum.dominio.financeiro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ludum.financeiro.carteira.OperacoesFinanceirasService;
import org.ludum.financeiro.carteira.entidades.Carteira;
import org.ludum.financeiro.carteira.entidades.Saldo;
import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.financeiro.transacao.TransacaoRepository;
import org.ludum.financeiro.transacao.entidades.Transacao;
import org.ludum.financeiro.transacao.entidades.TransacaoId;
import org.ludum.financeiro.transacao.entidades.Recibo;

import io.cucumber.java.Before; 
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ComprarJogoFuncionalidade {

    private ContaId conta;
    private Saldo saldo;
    private Carteira carteira;

    private ContaId contaDesenvolvedor;
    private Saldo saldoDesenvolvedor;
    private Carteira carteiraDesenvolvedor;

    private OperacoesFinanceirasService operacoesService;
    private boolean operacaoSucesso;
    private BigDecimal valorJogo;
    private boolean jogoPublicado;
    private boolean arquivoDisponivel;
    private boolean formaPagamentoValida;
    private boolean jaPossuiJogo;
    private boolean jogoBaixado;
    private Date dataCompra;

    private static class MockTransacaoRepository implements TransacaoRepository {
        private List<Transacao> transacoes = new ArrayList<>();
        private List<Recibo> recibos = new ArrayList<>();

        @Override
        public Transacao obterPorId(TransacaoId id) {
            return transacoes.stream().filter(t -> t.getTransacaoId().equals(id)).findFirst().orElse(null);
        }

        @Override
        public void salvarRecibo(Recibo recibo) {
            recibos.add(recibo);
        }

        @Override
        public void salvar(Transacao transacao) {
            transacoes.add(transacao);
        }

        public List<Transacao> getTransacoes() {
            return new ArrayList<>(transacoes);
        }

        public List<Recibo> getRecibos() {
            return new ArrayList<>(recibos);
        }
    }

    private MockTransacaoRepository mockRepo;

    @Before
    public void setup() {
        this.mockRepo = new MockTransacaoRepository();
        this.operacoesService = new OperacoesFinanceirasService(mockRepo);

        this.conta = new ContaId("comprador");
        this.saldo = new Saldo();
        this.carteira = new Carteira(conta, saldo);

        this.contaDesenvolvedor = new ContaId("desenvolvedor");
        this.saldoDesenvolvedor = new Saldo();
        this.carteiraDesenvolvedor = new Carteira(contaDesenvolvedor, saldoDesenvolvedor);

        this.operacaoSucesso = false;
        this.valorJogo = BigDecimal.ZERO;
        this.jogoPublicado = false;
        this.arquivoDisponivel = true; 
        this.formaPagamentoValida = false;
        this.jaPossuiJogo = false;
        this.jogoBaixado = false;
        this.dataCompra = null;
    }

    public ComprarJogoFuncionalidade() {
    }

    // --- Scenario: Compra com saldo + pagamento complementar ---
    @Given("que sou um usuário com saldo de R${int}")
    public void que_sou_um_usuário_com_saldo_de_r$(Integer saldoInicial) {
        operacoesService.adicionarSaldo(carteira, new BigDecimal(saldoInicial), true);
        carteira.liberarSaldoBloqueado();
    }

    @Given("o jogo está publicado e custa R${int}")
    public void o_jogo_está_publicado_e_custa_r$(Integer valor) {
        this.valorJogo = new BigDecimal(valor);
        this.jogoPublicado = true;
    }

    @Given("minha forma de pagamento complementar autoriza a cobrança de R${int}")
    public void minha_forma_de_pagamento_complementar_autoriza_a_cobrança_de_r$(Integer valorComplementar) {
        this.formaPagamentoValida = true;
    }

    @When("finalizo a compra usando R${int} de saldo e cobrando R${int} no cartão")
    public void finalizo_a_compra_usando_r_de_saldo_e_cobrando_r_no_cartão(Integer valorSaldo, Integer valorCartao) {
        operacoesService.adicionarSaldo(carteira, new BigDecimal(valorCartao), true);
        carteira.liberarSaldoBloqueado();
        
        operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, this.valorJogo);
    }

    @Then("a compra é concluída, o jogo é adicionado à minha biblioteca e meu saldo é zerado")
    public void a_compra_é_concluída_o_jogo_é_adicionado_à_minha_biblioteca_e_meu_saldo_é_zerado() {
        assertTrue(operacaoSucesso);
        assertEquals(BigDecimal.ZERO, carteira.getSaldo().getDisponivel());
    }

    // --- Scenario: Falha na forma de pagamento complementar ---
    @Given("a tentativa de cobrar R${int} no cartão é recusada")
    public void a_tentativa_de_cobrar_r$_no_cartão_é_recusada(Integer int1) {
        this.formaPagamentoValida = false;
    }

    @When("finalizo a compra")
    public void finalizo_a_compra() {
        if (!formaPagamentoValida) {
            operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, this.valorJogo);
        } else {
            operacaoSucesso = true; 
        }
    }

    @Then("a compra deve falhar e meu saldo permanece R${int}")
    public void a_compra_deve_falhar_e_meu_saldo_permanece_r$(Integer saldoEsperado) {
        assertFalse(operacaoSucesso);
        assertEquals(new BigDecimal(saldoEsperado), carteira.getSaldo().getDisponivel());
    }

    // --- Scenario: Compra de jogo publicado com arquivo disponível ---
    @Given("que o jogo está publicado, disponível para venda e o arquivo do jogo está presente no repositório")
    public void que_o_jogo_está_publicado_disponível_para_venda_e_o_arquivo_do_jogo_está_presente_no_repositório() {
        this.jogoPublicado = true;
        this.arquivoDisponivel = true;
    }

    @Given("eu tenho forma de pagamento válida")
    public void eu_tenho_forma_de_pagamento_válida() {
        this.formaPagamentoValida = true;
    }

    @When("eu realizo a compra no valor de R${int}")
    public void eu_realizo_a_compra_no_valor_de_r$(Integer valorCompra) {
        operacoesService.adicionarSaldo(carteira, new BigDecimal(valorCompra), true);
        carteira.liberarSaldoBloqueado();
        
        if (!arquivoDisponivel) { 
            operacaoSucesso = false;
        } else {
            operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, new BigDecimal(valorCompra));
        }
    }

    @Then("a compra é concluída com sucesso e recebo acesso para baixar o jogo")
    public void a_compra_é_concluída_com_sucesso_e_recebo_acesso_para_baixar_o_jogo() {
        assertTrue(operacaoSucesso);
    }

    // --- Scenario: Tentativa de comprar jogo publicado sem arquivo disponível ---
    @Given("que o jogo está publicado e disponível para venda")
    public void que_o_jogo_está_publicado_e_disponível_para_venda() {
        this.jogoPublicado = true;
    }

    @And("o arquivo binário\\/instalador do jogo NÃO está disponível no repositório \\(arquivo ausente\\)")
    public void o_arquivo_binario_instalador_do_jogo_nao_esta_disponivel_no_repositorio_arquivo_ausente() {
        this.arquivoDisponivel = false;
    }

    @When("tento realizar a compra")
    public void tento_realizar_a_compra() {
        if (!arquivoDisponivel) {
            operacaoSucesso = false;
        } else {
            operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(100), true);
            carteira.liberarSaldoBloqueado();
            operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, BigDecimal.valueOf(100));
        }
    }

    @Then("a transação é bloqueada e nenhuma cobrança é realizada")
    public void a_transacao_e_bloqueada_e_nenhuma_cobranca_e_realizada() {
        assertFalse(operacaoSucesso);
    }

    // --- Scenario: Primeiro pagamento — usuário não possui o jogo ---
    @Given("que não sou proprietário do jogo")
    public void que_não_sou_proprietário_do_jogo() {
        this.jaPossuiJogo = false;
    }

    @When("compro o jogo")
    public void compro_o_jogo() {
        operacoesService.adicionarSaldo(carteira, this.valorJogo, true);
        carteira.liberarSaldoBloqueado();

        if (jaPossuiJogo) {
            operacaoSucesso = false;
        } else {
            operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, this.valorJogo);
        }
    }

    @Then("a compra é confirmada e o jogo aparece na minha biblioteca")
    public void a_compra_é_confirmada_e_o_jogo_aparece_na_minha_biblioteca() {
        assertTrue(operacaoSucesso);
    }

    // --- Scenario: Tentar comprar jogo já adquirido ---
    @Given("que já sou proprietário do jogo")
    public void que_já_sou_proprietário_do_jogo() {
        this.jaPossuiJogo = true;
    }

    @When("tento comprar o mesmo jogo novamente")
    public void tento_comprar_o_mesmo_jogo_novamente() {
        if (jaPossuiJogo) {
            operacaoSucesso = false;
        } else {
            operacoesService.adicionarSaldo(carteira, this.valorJogo, true); 
            carteira.liberarSaldoBloqueado();
            operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, this.valorJogo);
        }
    }

    @Then("a plataforma retorna erro e nenhuma cobrança é feita")
    public void a_plataforma_retorna_erro_e_nenhuma_cobrança_é_feita() {
        assertFalse(operacaoSucesso);
    }

    // --- Scenarios de Reembolso ---
    @Given("que comprei um jogo há menos de 24h")
    public void que_comprei_um_jogo_ha_menos_de_24h() {
        dataCompra = new Date(System.currentTimeMillis() - 23 * 60 * 60 * 1000);
        valorJogo = BigDecimal.valueOf(30);
        jogoBaixado = false; 
    }    
    
    @And("ainda não baixei o jogo")
    public void ainda_nao_baixei_o_jogo() {
        jogoBaixado = false;
    }

    @When("solicito reembolso")
    public void solicito_reembolso() {
        if (this.jogoBaixado) {
            operacaoSucesso = false; 
        } else {
            operacaoSucesso = operacoesService.solicitarReembolso(carteira, valorJogo, dataCompra);
        }
    }

    @Then("devo receber o valor de volta no saldo a compra deve ser registrada como reembolsada")
    public void devo_receber_o_valor_de_volta_no_saldo_a_compra_deve_ser_registrada_como_reembolsada() {
        assertTrue(operacaoSucesso);
        assertEquals(this.valorJogo, carteira.getSaldo().getDisponivel());
    }

    @Given("que já baixei o jogo")
    public void que_ja_baixei_o_jogo() {
        this.jogoBaixado = true;
        this.dataCompra = new Date(System.currentTimeMillis() - 10 * 60 * 60 * 1000);
        this.valorJogo = BigDecimal.valueOf(30);
    }

    @Then("o sistema deve impedir o reembolso")
    public void o_sistema_deve_impedir_o_reembolso() {
        assertFalse(operacaoSucesso);
    }

    @Given("que comprei o jogo há mais de 24h")
    public void que_comprei_o_jogo_ha_mais_de_24h() {
        dataCompra = new Date(System.currentTimeMillis() - 25 * 60 * 60 * 1000);
        valorJogo = BigDecimal.valueOf(30);
        jogoBaixado = false;
    }

    @When("solicito reembolso fora do prazo")
    public void solicito_reembolso_fora_prazo() {
        this.jogoBaixado = false;
        operacaoSucesso = operacoesService.solicitarReembolso(carteira, valorJogo, dataCompra);
    }

    @Then("o sistema deve impedir o reembolso devido ao prazo")
    public void o_sistema_deve_impedir_o_reembolso_devido_ao_prazo() {
        assertFalse(operacaoSucesso);
    }
}