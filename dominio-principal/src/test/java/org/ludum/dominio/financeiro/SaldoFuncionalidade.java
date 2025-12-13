package org.ludum.dominio.financeiro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.OperacoesFinanceirasService;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.carteira.entidades.Saldo;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.financeiro.transacao.entidades.Recibo;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class SaldoFuncionalidade {

    private static final int VALOR_MENOR_AINDA = 20;
    private static final int VALOR_MENOR = 30;
    private static final int VALOR = 50;
    private static final int VALOR_MAIOR = 150;

    // Dados de cliente de teste
    private static final String NOME_CLIENTE_TESTE = "Cliente Teste";
    private static final String CPF_CLIENTE_TESTE = "12345678901";
    private static final String EMAIL_CLIENTE_TESTE = "cliente@teste.com";
    private static final String TELEFONE_CLIENTE_TESTE = "11999999999";

    private ContaId conta;
    private Saldo saldo;
    private Carteira carteira;

    private ContaId conta2;
    private Saldo saldo2;
    private Carteira carteira2;

    private OperacoesFinanceirasService operacoesService;
    private boolean operacaoSucesso;
    private boolean compraComSucesso;
    private BigDecimal valorJogo;
    private Date dataVenda;

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

    private static class MockCarteiraRepository implements CarteiraRepository {
        private List<Carteira> carteiras = new ArrayList<>();

        @Override
        public void salvar(Carteira carteira) {
            carteiras.removeIf(c -> c.getId().equals(carteira.getId()));
            carteiras.add(carteira);
        }

        @Override
        public Carteira obterPorContaId(ContaId contaId) {
            return carteiras.stream()
                    .filter(c -> c.getId().equals(contaId))
                    .findFirst()
                    .orElse(null);
        }
    }

    private static class MockProcessadorPagamento
            extends org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno {
        private boolean simulaSucesso = true;

        public MockProcessadorPagamento(TransacaoRepository transacaoRepository) {
            super(transacaoRepository);
        }

        public void setSimulaSucesso(boolean simulaSucesso) {
            this.simulaSucesso = simulaSucesso;
        }

        @Override
        protected void validarSolicitacao(ContaId contaId, java.math.BigDecimal valor, String moeda) {
            if (!simulaSucesso) {
                throw new IllegalArgumentException("Pagamento simulado como falha");
            }
        }

        @Override
        protected Object prepararDadosGateway(ContaId contaId, java.math.BigDecimal valor, String moeda,
                String descricao) {
            return new Object();
        }

        @Override
        protected String executarPagamentoNoGateway(Object dadosGateway, java.math.BigDecimal valor) throws Exception {
            return "mock-gateway-id";
        }

        @Override
        public String executarPayout(ContaId contaId, java.math.BigDecimal valor, String descricao) throws Exception {
            return "mock-payout-id";
        }
    }

    private MockTransacaoRepository mockTransacaoRepo;
    private MockCarteiraRepository mockCarteiraRepo;
    private MockProcessadorPagamento mockProcessadorPagamento;
    private MockBibliotecaService mockBibliotecaService;
    private MockProcessadorPayout mockProcessadorPayout;

    private static class MockProcessadorPayout extends org.ludum.dominio.financeiro.carteira.ProcessadorPayoutExterno {
        public MockProcessadorPayout(org.ludum.dominio.financeiro.transacao.TransacaoRepository transacaoRepository,
                                    org.ludum.dominio.financeiro.carteira.CarteiraRepository carteiraRepository) {
            super(transacaoRepository, carteiraRepository);
        }

        @Override
        protected void validarDadosPayout(org.ludum.dominio.financeiro.carteira.entidades.Carteira carteira, BigDecimal valor) {
            // Mock - validação simplificada
        }

        @Override
        protected org.ludum.dominio.financeiro.dto.DadosTransferencia prepararTransferencia(org.ludum.dominio.financeiro.carteira.entidades.Carteira carteira, 
                                                           BigDecimal valor, String descricao) {
            return new org.ludum.dominio.financeiro.dto.DadosTransferencia("mock-pix-key", valor, descricao);
        }

        @Override
        protected String executarTransferenciaNoGateway(org.ludum.dominio.financeiro.dto.DadosTransferencia dados) {
            return "mock-transfer-id";
        }
    }

    private static class MockBibliotecaService extends org.ludum.dominio.catalogo.biblioteca.services.BibliotecaService {
        public MockBibliotecaService() {
            super(null, null, null, null);
        }

        @Override
        public void adicionarJogo(org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso modeloDeAcesso, 
                                 org.ludum.dominio.catalogo.jogo.entidades.JogoId jogoId, 
                                 ContaId contaId, 
                                 org.ludum.dominio.financeiro.transacao.entidades.TransacaoId transacaoId) {
            // Mock - não faz nada
        }
    }

    @Before
    public void setup() {
        this.mockTransacaoRepo = new MockTransacaoRepository();
        this.mockCarteiraRepo = new MockCarteiraRepository();
        this.mockProcessadorPagamento = new MockProcessadorPagamento(mockTransacaoRepo);
        this.mockBibliotecaService = new MockBibliotecaService();
        this.mockProcessadorPayout = new MockProcessadorPayout(mockTransacaoRepo, mockCarteiraRepo);
        this.operacoesService = new OperacoesFinanceirasService(mockTransacaoRepo, mockCarteiraRepo, mockBibliotecaService);
        this.operacoesService.setProcessadorPagamento(mockProcessadorPagamento);
        this.operacoesService.setProcessadorPayout(mockProcessadorPayout);

        this.conta = new ContaId("comprador");
        this.saldo = new Saldo();
        this.carteira = new Carteira(conta, saldo);
        this.mockCarteiraRepo.salvar(carteira);

        this.conta2 = new ContaId("desenvolvedor");
        this.saldo2 = new Saldo();
        this.carteira2 = new Carteira(conta2, saldo2);
        this.mockCarteiraRepo.salvar(carteira2);

        this.operacaoSucesso = false;
        this.compraComSucesso = false;
        this.valorJogo = null;
        this.dataVenda = new Date();
    }

    public SaldoFuncionalidade() {
    }

    private void simularVendaDeJogo() {
        operacoesService.adicionarSaldo(conta, BigDecimal.valueOf(VALOR), "BRL", "Deposito teste",
                NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
        carteira = mockCarteiraRepo.obterPorContaId(conta);
        carteira.liberarSaldoBloqueado();
        mockCarteiraRepo.salvar(carteira);

        operacoesService.comprarJogo(carteira, carteira2, BigDecimal.valueOf(VALOR), 
                new org.ludum.dominio.catalogo.jogo.entidades.JogoId("jogo-teste"));
    }

    @Given("que adicionei R$50 na carteira")
    public void adicionei_d1_na_carteira() {
        operacaoSucesso = operacoesService.adicionarSaldo(conta, BigDecimal.valueOf(VALOR), "BRL", "Deposito R$50",
                NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
        carteira = mockCarteiraRepo.obterPorContaId(conta);
    }

    @And("o pagamento foi confirmado")
    public void o_pagamento_foi_confirmado() {
    }

    @When("verifico meu saldo")
    public void verifico_meu_saldo() {
        System.out.println(carteira.getSaldo().getDisponivel());
    }

    @Then("devo ver R$50 disponível")
    public void devo_ver_50_disponivel() {
        assertTrue(operacaoSucesso);
        Carteira carteiraAtual = mockCarteiraRepo.obterPorContaId(conta);
        assertEquals(BigDecimal.valueOf(VALOR), carteiraAtual.getSaldo().getDisponivel());
    }

    @Given("que tentei adicionar R$50 na carteira com pagamento pendente")
    public void adicionei_d2_na_carteira() {
        mockProcessadorPagamento.setSimulaSucesso(false);
        operacaoSucesso = operacoesService.adicionarSaldo(conta, BigDecimal.valueOf(VALOR), "BRL", "Deposito R$50",
                NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
        carteira = mockCarteiraRepo.obterPorContaId(conta);
        mockProcessadorPagamento.setSimulaSucesso(true);
    }

    @And("o pagamento está pendente OU foi recusado")
    public void o_pagamento_esta_pendente_ou_foi_recusado() {
    }

    @Then("o saldo disponível não deve ser alterado e a transação de depósito não deve ser confirmada")
    public void o_saldo_disponível_nao_deve_ser_alterado_e_a_transacao_de_deposito_nao_deve_ser_confirmada() {
        assertFalse(operacaoSucesso);
        Carteira carteiraAtual = mockCarteiraRepo.obterPorContaId(conta);
        assertEquals(BigDecimal.ZERO, carteiraAtual.getSaldo().getDisponivel());
    }

    @Given("que adicionei R$150 na carteira")
    public void adicionei_150_na_carteira() {
        operacaoSucesso = operacoesService.adicionarSaldo(conta, BigDecimal.valueOf(VALOR_MAIOR), "BRL",
                "Deposito R$150", NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
        carteira = mockCarteiraRepo.obterPorContaId(conta);
    }

    @And("o pagamento foi confirmado para o depósito de R$150")
    public void o_pagamento_foi_confirmado_150() {
    }

    @When("verifico meu saldo imediatamente")
    public void verifico_meu_saldo_imediatamente() {
        System.out.println(carteira.getSaldo().getDisponivel());
    }

    @Then("R${int} deve constar como bloqueado\\/pendente por 24h e o saldo disponível para uso não aumenta")
    public void r_deve_constar_como_bloqueado_pendente_por_24h_e_o_saldo_disponivel_para_uso_nao_aumenta(
            Integer valorBloqueado) {
        assertTrue(operacaoSucesso);
        Carteira carteiraAtual = mockCarteiraRepo.obterPorContaId(conta);
        assertEquals(BigDecimal.ZERO, carteiraAtual.getSaldo().getDisponivel());
        assertEquals(new BigDecimal(valorBloqueado), carteiraAtual.getSaldo().getBloqueado());
    }

    @Given("que tenho R$150 na carteira e está dentro do período de bloqueio de 24h")
    public void adicionei_150_na_carteira_compra_jogo() {
        operacaoSucesso = operacoesService.adicionarSaldo(conta, BigDecimal.valueOf(VALOR_MAIOR), "BRL",
                "Deposito R$150", NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
        carteira = mockCarteiraRepo.obterPorContaId(conta);
    }

    @And("tento comprar um jogo que custa R$50")
    public void tento_comprar_um_jogo_que_custa_50() {
        valorJogo = BigDecimal.valueOf(50);
    }

    @When("finalizo a compra imediatamente")
    public void finalizo_a_compra() {
        compraComSucesso = operacoesService.comprarJogo(carteira, carteira2, valorJogo, 
                new org.ludum.dominio.catalogo.jogo.entidades.JogoId("jogo-teste"));
    }

    @Then("a compra não deve ser concluída e o saldo bloqueado deve permanecer inalterado")
    public void a_compra_nao_deve_ser_concluida_e_o_saldo_bloqueado_deve_permanecer_inalterado() {
        assertFalse(compraComSucesso);
        Carteira carteiraAtual = mockCarteiraRepo.obterPorContaId(conta);
        assertEquals(BigDecimal.valueOf(VALOR_MAIOR), carteiraAtual.getSaldo().getBloqueado());
    }

    @Given("que tenho R$30 disponíveis na carteira")
    public void tenho_r_30_disponíveis_na_carteira() {
        operacoesService.adicionarSaldo(conta, BigDecimal.valueOf(VALOR_MENOR), "BRL", "Deposito R$30",
                NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
        carteira = mockCarteiraRepo.obterPorContaId(conta);
    }

    @And("o jogo custa R$25")
    public void o_jogo_custa_r_25() {
        valorJogo = BigDecimal.valueOf(25);
    }

    @When("finalizo a compra do jogo")
    public void compro_o_jogo() {
        compraComSucesso = operacoesService.comprarJogo(carteira, carteira2, valorJogo, 
                new org.ludum.dominio.catalogo.jogo.entidades.JogoId("jogo-teste"));
    }

    @Then("o saldo deve ser debitado em R$25 e a compra deve ser concluída com sucesso")
    public void o_saldo_deve_ser_debitado_em_r_25_e_a_compra_deve_ser_concluida_com_sucesso() {
        assertTrue(compraComSucesso);
        Carteira carteiraComprador = mockCarteiraRepo.obterPorContaId(conta);
        Carteira carteiraDev = mockCarteiraRepo.obterPorContaId(conta2);
        assertEquals(BigDecimal.valueOf(5), carteiraComprador.getSaldo().getDisponivel());
        assertEquals(BigDecimal.valueOf(25), carteiraDev.getSaldo().getBloqueado());
    }

    @Given("que tenho R$20 disponíveis na carteira")
    public void tenho_r_20_disponíveis_na_carteira() {
        operacoesService.adicionarSaldo(conta, BigDecimal.valueOf(VALOR_MENOR_AINDA), "BRL", "Deposito R$20",
                NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
        carteira = mockCarteiraRepo.obterPorContaId(conta);
    }

    @And("o jogo custa R$27")
    public void o_jogo_custa_r_27() {
        valorJogo = BigDecimal.valueOf(27);
    }

    @When("tento comprar o jogo")
    public void compro_o_jogo_2() {
        compraComSucesso = operacoesService.comprarJogo(carteira, carteira2, valorJogo, 
                new org.ludum.dominio.catalogo.jogo.entidades.JogoId("jogo-teste"));
    }

    @Then("a compra não deve ser concluída e nenhum débito deve ocorrer no saldo")
    public void a_compra_nao_deve_ser_concluida_e_nenhum_debito_deve_ocorrer_no_saldo() {
        assertFalse(compraComSucesso);
        Carteira carteiraAtual = mockCarteiraRepo.obterPorContaId(conta);
        assertEquals(BigDecimal.valueOf(VALOR_MENOR_AINDA), carteiraAtual.getSaldo().getDisponivel());
    }

    @Given("que solicitei reembolso válido \\(dentro do prazo e não baixei\\)")
    public void solicitei_reembolso_valido() {
        operacaoSucesso = operacoesService.solicitarReembolso(carteira, BigDecimal.valueOf(VALOR), new Date());
    }

    @And("o reembolso foi processado pelo sistema")
    public void o_reembolso_foi_processado_pelo_sistema() {
        assertTrue(operacaoSucesso);
    }

    @When("verifico minha carteira")
    public void verifico_minha_carteira() {
        System.out.println(carteira.getSaldo().getDisponivel());
    }

    @Then("o valor reembolsado deve constar como saldo disponível")
    public void o_valor_reembolsado_deve_constar_como_saldo_disponivel() {
        assertEquals(BigDecimal.valueOf(VALOR), carteira.getSaldo().getDisponivel());
    }

    @Given("que solicitei reembolso inválido \\(fora do prazo OU após download\\)")
    public void solicitei_reembolso_invalido() {
        Date dataForaDoPrazo = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(25));
        operacaoSucesso = operacoesService.solicitarReembolso(carteira, BigDecimal.valueOf(VALOR), dataForaDoPrazo);
    }

    @When("o sistema avalia a solicitação")
    public void o_sistema_avalia_a_solicitacao() {
        assertFalse(operacaoSucesso);
    }

    @Then("o reembolso deve ser negado e nenhum valor deve ser creditado na carteira")
    public void o_reembolso_deve_ser_negado() {
        assertEquals(BigDecimal.ZERO, carteira.getSaldo().getDisponivel());
    }

    @Given("que vendi um jogo para um usuário")
    public void vendi_um_jogo_para_um_usuario() {
        simularVendaDeJogo();
        this.dataVenda = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(25));
    }

    @And("o valor está disponível na conta do desenvolvedor há >24h")
    public void o_valor_esta_disponivel_ha_mais_de_24h() {
        carteira2.liberarSaldoBloqueado();
    }

    @And("minha conta externa está vinculada\\/validada")
    public void minha_conta_externa_esta_vinculada_validada() {
        carteira2.setContaExternaValida(true);
    }

    @When("solicito saque")
    public void solicito_saque() {
        operacaoSucesso = operacoesService.solicitarSaque(carteira2, "recipient-mock-id", BigDecimal.valueOf(VALOR),
                dataVenda, false,
                false);
    }

    @Then("o valor deve ser transferido para minha conta externa e o saldo na plataforma deve ser reduzido")
    public void o_valor_deve_ser_transferido() {
        assertTrue(operacaoSucesso);
        Carteira carteiraDev = mockCarteiraRepo.obterPorContaId(conta2);
        assertEquals(BigDecimal.ZERO, carteiraDev.getSaldo().getDisponivel());
    }

    @Given("que vendi um jogo há menos de 24h")
    public void vendi_um_jogo_ha_menos_de_24h() {
        simularVendaDeJogo();
        this.dataVenda = new Date();
        mockCarteiraRepo.salvar(carteira);
        mockCarteiraRepo.salvar(carteira2);
    }

    @When("solicito saque desse valor")
    public void solicito_saque_desse_valor() {
        Carteira carteiraAtual = mockCarteiraRepo.obterPorContaId(conta2);
        carteiraAtual.setContaExternaValida(true);
        operacaoSucesso = operacoesService.solicitarSaque(carteiraAtual, "recipient-mock-id", BigDecimal.valueOf(VALOR),
                dataVenda, false,
                false);
        if (operacaoSucesso) {
            mockCarteiraRepo.salvar(carteiraAtual);
        }
    }

    @Then("a solicitação de saque deve ser recusada e o valor permanece indisponível para saque")
    public void a_solicitacao_de_saque_deve_ser_recusada() {
        assertFalse(operacaoSucesso);
        Carteira carteiraDev = mockCarteiraRepo.obterPorContaId(conta2);
        assertEquals(BigDecimal.valueOf(VALOR), carteiraDev.getSaldo().getBloqueado());
        assertEquals(BigDecimal.ZERO, carteiraDev.getSaldo().getDisponivel());
    }

    @Given("que tenho saldo disponível para saque")
    public void tenho_saldo_disponivel_para_saque() {
        simularVendaDeJogo();
        carteira2.liberarSaldoBloqueado();
        this.dataVenda = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(25));
    }

    @And("minha conta bancária\\/external está vinculada e verificada")
    public void minha_conta_bancaria_esta_vinculada() {
        carteira2.setContaExternaValida(true);
    }

    @When("solicito o saque")
    public void solicito_saque_valido() {
        operacaoSucesso = operacoesService.solicitarSaque(carteira2, "recipient-mock-id", BigDecimal.valueOf(VALOR),
                dataVenda, false,
                false);
    }

    @Then("o saque deve ser processado e transferido para a conta externa")
    public void o_saque_deve_ser_processado() {
        assertTrue(operacaoSucesso);
        assertEquals(BigDecimal.ZERO, carteira2.getSaldo().getDisponivel());
    }

    @And("não tenho conta externa vinculada\\/validada")
    public void nao_tenho_conta_externa_vinculada() {
        carteira2.setContaExternaValida(false);
    }

    @Then("a solicitação de saque deve ser recusada e o saldo do desenvolvedor não deve ser alterado")
    public void a_solicitacao_de_saque_deve_ser_recusada_por_conta_nao_validada() {
        assertFalse(operacaoSucesso);
        assertEquals(BigDecimal.valueOf(VALOR), carteira2.getSaldo().getDisponivel());
    }

    @Given("que finalizei uma campanha")
    public void finalizei_uma_campanha() {
        simularVendaDeJogo();
        this.dataVenda = new Date(System.currentTimeMillis() - TimeUnit.HOURS.toMillis(25));
    }

    @And("a meta mínima foi atingida")
    public void a_meta_minima_foi_atingida() {
    }

    @And("já passou 1 dia do término da campanha")
    public void ja_passou_1_dia_do_termino_da_campanha() {
        carteira2.liberarSaldoBloqueado();
    }

    @And("minha conta externa está validada")
    public void minha_conta_externa_esta_validada() {
        carteira2.setContaExternaValida(true);
    }

    @When("solicito saque do valor arrecadado")
    public void solicito_saque_do_valor_arrecadado() {
        operacaoSucesso = operacoesService.solicitarSaque(carteira2, "recipient-mock-id", BigDecimal.valueOf(VALOR),
                dataVenda, true, true);
    }

    @Then("o valor arrecadado deve ser liberado e transferido para minha conta externa")
    public void o_valor_arrecadado_deve_ser_liberado() {
        assertTrue(operacaoSucesso);
        assertEquals(BigDecimal.ZERO, carteira2.getSaldo().getDisponivel());
    }

    @And("\\(a meta mínima NÃO foi atingida OU ainda não passou {int} dia do término\\)")
    public void meta_nao_atingida_ou_tempo_insuficiente(Integer int1) {
        this.dataVenda = new Date();
        carteira2.setContaExternaValida(true);
    }

    @Then("o saque deve ser bloqueado e nenhum valor deve ser liberado")
    public void o_saque_deve_ser_bloqueado() {
        assertFalse(operacaoSucesso);
        assertEquals(BigDecimal.valueOf(VALOR), carteira2.getSaldo().getBloqueado());
        assertEquals(BigDecimal.ZERO, carteira2.getSaldo().getDisponivel());
    }
}