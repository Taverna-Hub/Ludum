package org.ludum.dominio.financeiro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ludum.financeiro.carteira.OperacoesFinanceirasService;
import org.ludum.financeiro.carteira.entidades.Carteira;
import org.ludum.financeiro.carteira.entidades.Saldo;
import org.ludum.financeiro.transacao.TransacaoRepository;
import org.ludum.financeiro.transacao.entidades.Transacao;
import org.ludum.financeiro.transacao.entidades.TransacaoId;
import org.ludum.financeiro.transacao.enums.StatusTransacao;
import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.financeiro.transacao.entidades.Recibo;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;


public class SaldoFuncionalidade {

    private static final int VALOR_MENOR_AINDA = 20;
    private static final int VALOR_MENOR = 30;
    private static final int VALOR = 50;
    private static final int VALOR_MAIOR = 150;

    private ContaId conta = new ContaId("comprador");
    private Saldo saldo = new Saldo();
    private Carteira carteira = new Carteira(conta, saldo); 

    private ContaId conta2 = new ContaId("desenvolvedor");
    private Saldo saldo2 = new Saldo();
    private Carteira carteira2 = new Carteira(conta2, saldo2); 

    private OperacoesFinanceirasService operacoesService;
    private boolean operacaoSucesso;
    private boolean compraComSucesso;
    private List<Transacao> transacoes;
    private List<Recibo> recibos;
    private BigDecimal valorJogo;

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

    public SaldoFuncionalidade() {
        this.mockRepo = new MockTransacaoRepository();
        this.operacoesService = new OperacoesFinanceirasService(mockRepo);
        this.transacoes = mockRepo.getTransacoes();
        this.recibos = mockRepo.getRecibos();
    }

    // Adicionar saldo com pagamento confirmado (positivo)

    @Given("adicionei R$ 50 na carteira")
    public void adicionei_d1_na_carteira() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(VALOR), true);
    }

    @When("verifico meu saldo") 
    public void verifico_meu_saldo() {
        System.out.println(carteira.getSaldo().getDisponivel());
    }

    @Then("devo ver R$50 disponível")
    public void devo_ver_50_disponivel() {
        assertTrue(operacaoSucesso);
        assertEquals(BigDecimal.valueOf(VALOR), carteira.getSaldo().getDisponivel());
        assertEquals(1, transacoes.size());
    }

    // Adicionar saldo com pagamento pendente/recusado (negativo)

    @Given("adicionei R$ 50 na carteira")
    public void adicionei_d2_na_carteira() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(VALOR), false);
    }

    @Then("o saldo disponível não deve ser alterado e a transação de depósito não deve ser confirmada")
    public void o_saldo_disponível_nao_deve_ser_alterado_e_a_transacao_de_deposito_nao_deve_ser_confirmada() {
        assertTrue(operacaoSucesso);
        assertEquals(BigDecimal.valueOf(VALOR), carteira.getSaldo().getDisponivel());
        assertEquals(StatusTransacao.PENDENTE, transacoes.get(1).getStatus());
    }
    
    // Depósito acima de R$100 fica bloqueado imediatamente (positivo)

    @Given("adicionei R$ 150 na carteira")
    public void adicionei_150_na_carteira() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(VALOR_MAIOR), true);
    }

    @Then("R$150 deve constar como bloqueado/pendente por 24h e o saldo disponível para uso não aumenta")
    public void r_150_deve_constar_como_bloqueado_pendente_por_24h_e_o_saldo_disponivel_para_uso_nao_aumenta() {
        assertTrue(operacaoSucesso);
        assertEquals(BigDecimal.valueOf(VALOR), carteira.getSaldo().getDisponivel());
        assertEquals(BigDecimal.valueOf(VALOR_MAIOR), carteira.getSaldo().getBloqueado());
        assertEquals(3, transacoes.size());
    }

    // Tentar usar saldo bloqueado antes de 24h (negativo)
    @Given("adicionei R$ 150 na carteira")
    public void adicionei_150_na_carteira_compra_jogo() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(VALOR_MAIOR), true);
    }

    @And("tento comprar um jogo que custa R$50")
    public void tento_comprar_um_jogo_que_custa_50() {
        valorJogo = BigDecimal.valueOf(50);
    }

    @When("finalizo a compra")
    public void finalizo_a_compra() {
        compraComSucesso = operacoesService.comprarJogo(carteira, carteira2, valorJogo);
    }

    @Then("a compra não deve ser concluída e o saldo bloqueado deve permanecer inalterado")
    public void a_compra_nao_deve_ser_concluida_e_o_saldo_bloqueado_deve_permanecer_inalterado() {
        assertFalse(compraComSucesso);
        assertEquals(BigDecimal.valueOf(VALOR_MAIOR), carteira.getSaldo().getBloqueado());
    }

    // Usar saldo em compra com saldo suficiente (positivo)
    @Given("tenho R$30 disponíveis na carteira")
    public void tenho_r_30_disponíveis_na_carteira() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(VALOR_MENOR), true);
    }

    @And("o jogo custa R$25")
    public void o_jogo_custa_r_25() {
        valorJogo = BigDecimal.valueOf(25);
    }

    @When("compro o jogo") 
    public void compro_o_jogo() {
        compraComSucesso = operacoesService.comprarJogo(carteira, carteira2, valorJogo);
    }

    @Then("o saldo deve ser debitado em R$25 e a compra deve ser concluída com sucesso")
    public void o_saldo_deve_ser_debitado_em_r_25_e_a_compra_deve_ser_concluida_com_sucesso() {
        assertTrue(compraComSucesso);
        assertEquals(BigDecimal.valueOf(5), carteira.getSaldo().getDisponivel());
        assertEquals(BigDecimal.valueOf(25), carteira2.getSaldo().getDisponivel());
    }

    // Tentar usar saldo insuficiente (negativo)
    @Given("tenho R$20 disponíveis na carteira")
    public void tenho_r_20_disponíveis_na_carteira() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(15), true);
    }

    @And("o jogo custa R$27") 
    public void o_jogo_custa_r_27() {
        valorJogo = BigDecimal.valueOf(27);
    }

    @When("compro o jogo") 
    public void compro_o_jogo_2() {
        compraComSucesso = operacoesService.comprarJogo(carteira, carteira2, valorJogo);
    }

    @Then("a compra não deve ser concluída e nenhum débito deve ocorrer no saldo")
    public void a_compra_nao_deve_ser_concluida_e_nenhum_debito_deve_ocorrer_no_saldo() {
        assertFalse(compraComSucesso);
        assertEquals(BigDecimal.valueOf(20), carteira.getSaldo().getDisponivel());
    }

    // Reembolso válido é creditado na carteira (positivo)
    @Given("solicitei reembolso válido dentro do prazo e não baixei")
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

    // Reembolso inválido não é creditado (negativo)
    @Given("solicitei reembolso inválido fora do prazo ou após download")
    public void solicitei_reembolso_invalido() {
        operacaoSucesso = operacoesService.solicitarReembolso(carteira, BigDecimal.valueOf(VALOR), new Date(new Date().getTime() + 24L * 60 * 60 * 1000));
    }

    @When("o sistema avalia a solicitação")
    public void o_sistema_avalia_a_solicitacao() {
        assertFalse(operacaoSucesso);
    }

    @Then("o reembolso deve ser negado e nenhum valor deve ser creditado na carteira")
    public void o_reembolso_deve_ser_negado() {
        assertEquals(BigDecimal.ZERO, carteira.getSaldo().getDisponivel());
    }

    // Desenvolvedor saca saldo após 24h (positivo)
    @Given("vendi um jogo para um usuário")
    public void vendi_um_jogo_para_um_usuario() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(VALOR), true);
    }

    @And("o valor está disponível na conta do desenvolvedor há mais de 24h")
    public void o_valor_esta_disponivel_ha_mais_de_24h() {
        carteira.liberarSaldoBloqueado();
    }

    @And("minha conta externa está vinculada e validada")
    public void minha_conta_externa_esta_vinculada() {
        carteira.setContaExternaValida(true);
    }

    @When("solicito saque")
    public void solicito_saque() {
        Date dataVenda = new Date(System.currentTimeMillis() - 25 * 60 * 60 * 1000); // 25 horas atrás
        operacaoSucesso = operacoesService.solicitarSaque(carteira, BigDecimal.valueOf(VALOR), true, dataVenda, false, false);
    }

    @Then("o valor deve ser transferido para minha conta externa e o saldo na plataforma deve ser reduzido")
    public void o_valor_deve_ser_transferido() {
        assertTrue(operacaoSucesso);
        assertEquals(BigDecimal.ZERO, carteira.getSaldo().getDisponivel());
    }

    // Desenvolvedor tenta sacar antes de 24h (negativo)
    @Given("vendi um jogo há menos de 24h")
    public void vendi_um_jogo_ha_menos_de_24h() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(VALOR), true);
    }

    @When("solicito saque desse valor")
    public void solicito_saque_desse_valor() {
        Date dataVenda = new Date(); // Agora, menos de 24h
        operacaoSucesso = operacoesService.solicitarSaque(carteira, BigDecimal.valueOf(VALOR), true, dataVenda, false, false);
    }

    @Then("a solicitação de saque deve ser recusada e o valor permanece indisponível para saque")
    public void a_solicitacao_de_saque_deve_ser_recusada() {
        assertFalse(operacaoSucesso);
        assertEquals(BigDecimal.valueOf(VALOR), carteira.getSaldo().getBloqueado());
    }

    // Saque com conta externa validada (positivo)
    @Given("tenho saldo disponível para saque")
    public void tenho_saldo_disponivel_para_saque() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(VALOR), true);
        carteira.liberarSaldoBloqueado(); 
    }

    @And("minha conta bancária está vinculada e verificada")
    public void minha_conta_bancaria_esta_vinculada() {
        carteira.setContaExternaValida(true);
    }

    @When("solicito saque")
    public void solicito_saque_valido() {
        Date dataVenda = new Date(System.currentTimeMillis() - 25 * 60 * 60 * 1000); // 25 horas atrás
        operacaoSucesso = operacoesService.solicitarSaque(carteira, BigDecimal.valueOf(VALOR), true, dataVenda, false, false);
    }

    @Then("o saque deve ser processado e transferido para a conta externa")
    public void o_saque_deve_ser_processado() {
        assertTrue(operacaoSucesso);
        assertEquals(BigDecimal.ZERO, carteira.getSaldo().getDisponivel());
    }

    // Saque sem conta externa vinculada (negativo)
    @Given("não tenho conta externa vinculada ou validada")
    public void nao_tenho_conta_externa_vinculada() {
        carteira.setContaExternaValida(false);
    }

    @Then("a solicitação de saque deve ser recusada e o saldo do desenvolvedor não deve ser alterado")
    public void a_solicitacao_de_saque_deve_ser_recusada_por_conta_nao_validada() {
        assertFalse(operacaoSucesso);
        assertEquals(BigDecimal.valueOf(VALOR), carteira.getSaldo().getDisponivel());
    }

    // Desenvolvedor saca saldo de crowdfunding após meta e 1 dia (positivo)
    @Given("finalizei uma campanha")
    public void finalizei_uma_campanha() {
        operacaoSucesso = operacoesService.adicionarSaldo(carteira, BigDecimal.valueOf(VALOR), true);
    }

    @And("a meta mínima foi atingida")
    public void a_meta_minima_foi_atingida() {
        assertTrue(operacaoSucesso);
    }

    @And("já passou 1 dia do término da campanha")
    public void ja_passou_1_dia_do_termino_da_campanha() {
        carteira.liberarSaldoBloqueado();
    }

    @When("solicito saque do valor arrecadado")
    public void solicito_saque_do_valor_arrecadado() {
        Date dataVenda = new Date(System.currentTimeMillis() - 25 * 60 * 60 * 1000); // 25 horas atrás
        operacaoSucesso = operacoesService.solicitarSaque(carteira, BigDecimal.valueOf(VALOR), true, dataVenda, true, true);
    }

    @Then("o valor arrecadado deve ser liberado e transferido para minha conta externa")
    public void o_valor_arrecadado_deve_ser_liberado() {
        assertTrue(operacaoSucesso);
        assertEquals(BigDecimal.ZERO, carteira.getSaldo().getDisponivel());
    }

    // Tentativa de saque de crowdfunding antes de 1 dia ou meta não atingida (negativo)
    @Given("a meta mínima não foi atingida ou ainda não passou 1 dia do término")
    public void meta_nao_atingida_ou_tempo_insuficiente() {
        operacaoSucesso = false;
    }

    @Then("o saque deve ser bloqueado e nenhum valor deve ser liberado")
    public void o_saque_deve_ser_bloqueado() {
        assertFalse(operacaoSucesso);
        assertEquals(BigDecimal.ZERO, carteira.getSaldo().getDisponivel());
    }
}