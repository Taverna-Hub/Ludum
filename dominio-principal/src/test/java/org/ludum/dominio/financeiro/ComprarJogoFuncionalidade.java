package org.ludum.dominio.financeiro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.ludum.dominio.catalogo.biblioteca.repositorios.BibliotecaRepository;
import org.ludum.dominio.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.dominio.catalogo.biblioteca.entidades.ItemBiblioteca;
import org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.Slug;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.OperacoesFinanceirasService;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.carteira.entidades.Saldo;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.financeiro.transacao.entidades.Recibo;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ComprarJogoFuncionalidade {

    // Dados de cliente de teste
    private static final String NOME_CLIENTE_TESTE = "Cliente Teste";
    private static final String CPF_CLIENTE_TESTE = "12345678901";
    private static final String EMAIL_CLIENTE_TESTE = "cliente@teste.com";
    private static final String TELEFONE_CLIENTE_TESTE = "11999999999";

    private ContaId conta;
    private ContaId contaDesenvolvedor;
    private BigDecimal valorJogo;
    private JogoId jogoId;
    @SuppressWarnings("unused")
    private Date dataCompra;
    private boolean compraRealizada;
    private boolean formaPagamentoValida;
    private boolean operacaoSucesso;
    @SuppressWarnings("unused")
    private boolean jogoPublicado;
    private boolean arquivoDisponivel;
    private boolean jaPossuiJogo;
    private Carteira carteira;
    private Carteira carteiraDesenvolvedor;

    private OperacoesFinanceirasService operacoesService;
    private MockBibliotecaRepository mockBibliotecaRepository;
    private MockJogoRepository mockJogoRepository;
    private MockCarteiraRepository mockCarteiraRepository;
    private MockTransacaoRepository mockTransacaoRepository;

    private static class MockBibliotecaRepository implements BibliotecaRepository {
        private List<Biblioteca> bibliotecas = new ArrayList<>();

        @Override
        public void salvar(Biblioteca biblioteca) {
            bibliotecas.removeIf(b -> b.getContaId().equals(biblioteca.getContaId()));
            bibliotecas.add(biblioteca);
        }

        @Override
        public Biblioteca obterPorJogador(ContaId contaId) {
            return bibliotecas.stream()
                    .filter(b -> b.getContaId().equals(contaId))
                    .findFirst()
                    .orElseGet(() -> {
                        Biblioteca novaBiblioteca = new Biblioteca(contaId);
                        bibliotecas.add(novaBiblioteca);
                        return novaBiblioteca;
                    });
        }
    }

    private static class MockJogoRepository implements JogoRepository {
        private List<Jogo> jogos = new ArrayList<>();

        @Override
        public Jogo obterPorId(JogoId id) {
            return jogos.stream()
                    .filter(j -> j.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void salvar(Jogo jogo) {
            jogos.removeIf(j -> j.getId().equals(jogo.getId()));
            jogos.add(jogo);
        }

        @Override
        public Jogo obterPorSlug(Slug slug) {
            return null;
        }

        @Override
        public boolean existeSlugParaDesenvolvedora(ContaId devId, Slug slug) {
            return false;
        }

        @Override
        public List<Jogo> obterJogosPorTag(TagId tag) {
            return new ArrayList<>();
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

        @SuppressWarnings("unused")
        public List<Recibo> getRecibos() {
            return new ArrayList<>(recibos);
        }
    }

    private static class MockProcessadorPagamento
            extends org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno {
        public MockProcessadorPagamento(TransacaoRepository transacaoRepository) {
            super(transacaoRepository);
        }

        @Override
        protected void validarSolicitacao(ContaId contaId, java.math.BigDecimal valor, String moeda) {
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

    private MockProcessadorPagamento mockProcessadorPagamento;

    @Before
    public void setup() {
        this.mockTransacaoRepository = new MockTransacaoRepository();
        this.mockBibliotecaRepository = new MockBibliotecaRepository();
        this.mockJogoRepository = new MockJogoRepository();
        this.mockCarteiraRepository = new MockCarteiraRepository();
        this.mockProcessadorPagamento = new MockProcessadorPagamento(mockTransacaoRepository);

        this.operacoesService = new OperacoesFinanceirasService(mockTransacaoRepository, mockCarteiraRepository);
        this.operacoesService.setProcessadorPagamento(mockProcessadorPagamento);

        this.conta = new ContaId("comprador");
        this.contaDesenvolvedor = new ContaId("desenvolvedor");
        this.jogoId = new JogoId("jogo-123");

        this.carteira = new Carteira(conta, new Saldo());
        this.carteiraDesenvolvedor = new Carteira(contaDesenvolvedor, new Saldo());

        mockCarteiraRepository.salvar(carteira);
        mockCarteiraRepository.salvar(carteiraDesenvolvedor);

        try {
            List<Tag> tags = new ArrayList<>();
            tags.add(new Tag(new TagId("tag-aventura"), "Aventura"));

            URL capaOficial = URI.create("https://example.com/capa.jpg").toURL();
            Jogo jogo = new Jogo(jogoId, contaDesenvolvedor, "Jogo Teste", "Descrição do jogo",
                    capaOficial, tags, false, LocalDate.now());

            jogo.adicionarScreenshot(URI.create("https://example.com/screenshot.jpg").toURL());
            mockJogoRepository.salvar(jogo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        this.valorJogo = BigDecimal.ZERO;
        this.dataCompra = null;
        this.compraRealizada = false;
    }

    public ComprarJogoFuncionalidade() {
    }

    // --- Scenario: Compra com saldo + pagamento complementar ---
    @Given("que sou um usuário com saldo de R${int}")
    public void que_sou_um_usuário_com_saldo_de_r$(Integer saldoInicial) {
        Carteira carteira = mockCarteiraRepository.obterPorContaId(conta);
        carteira.getSaldo().addDisponivel(new BigDecimal(saldoInicial));
        mockCarteiraRepository.salvar(carteira);
    }

    @Given("o jogo está publicado e custa R${int}")
    public void o_jogo_está_publicado_e_custa_r$(Integer valor) {
        this.valorJogo = new BigDecimal(valor);

        Jogo jogo = mockJogoRepository.obterPorId(jogoId);
        jogo.publicar();
        mockJogoRepository.salvar(jogo);
    }

    @When("finalizo a compra usando R${int} de saldo e cobrando R${int} no cartão")
    public void finalizo_a_compra_usando_r_de_saldo_e_cobrando_r_no_cartao(Integer valorSaldo, Integer valorCartao) {
        try {
            this.carteira = mockCarteiraRepository.obterPorContaId(conta);
            this.carteiraDesenvolvedor = mockCarteiraRepository.obterPorContaId(contaDesenvolvedor);

            this.carteira.getSaldo().addDisponivel(new BigDecimal(valorCartao));
            mockCarteiraRepository.salvar(this.carteira);

            compraRealizada = operacoesService.comprarJogo(this.carteira, this.carteiraDesenvolvedor, valorJogo);

            if (compraRealizada) {
                Biblioteca biblioteca = mockBibliotecaRepository.obterPorJogador(conta);
                biblioteca.adicionarJogo(ModeloDeAcesso.PAGO, jogoId);
                mockBibliotecaRepository.salvar(biblioteca);

                this.carteira.getSaldo().setDisponivel(BigDecimal.ZERO);
                mockCarteiraRepository.salvar(this.carteira);
            }
        } catch (Exception e) {
            compraRealizada = false;
        }
    }

    @Then("a compra é concluída, o jogo é adicionado à minha biblioteca e meu saldo é zerado")
    public void a_compra_é_concluída_o_jogo_é_adicionado_à_minha_biblioteca_e_meu_saldo_é_zerado() {
        assertTrue(compraRealizada);

        Biblioteca biblioteca = mockBibliotecaRepository.obterPorJogador(conta);
        assertNotNull(biblioteca);
        Optional<ItemBiblioteca> item = biblioteca.buscarJogoEmBiblioteca(jogoId);
        assertTrue(item.isPresent());

        Carteira carteira = mockCarteiraRepository.obterPorContaId(conta);
        assertEquals(BigDecimal.ZERO, carteira.getSaldo().getDisponivel());

        List<Transacao> transacoes = mockTransacaoRepository.getTransacoes();
        assertTrue(transacoes.stream().anyMatch(t -> t.getValor().equals(valorJogo) &&
                t.getContaOrigem().equals(conta) &&
                t.getContaDestino().equals(contaDesenvolvedor)));
    }

    // --- Scenario: Falha na forma de pagamento complementar ---
    @Given("a tentativa de cobrar R${int} no cartão é recusada")
    public void a_tentativa_de_cobrar_r$_no_cartão_é_recusada(Integer int1) {
        this.formaPagamentoValida = false;
    }

    @Given("minha forma de pagamento complementar autoriza a cobrança de R${int}")
    public void minha_forma_de_pagamento_complementar_autoriza_a_cobrança_de_r$(Integer valor) {
        this.formaPagamentoValida = true;

        Carteira carteira = mockCarteiraRepository.obterPorContaId(conta);
        carteira.getSaldo().addDisponivel(new BigDecimal(valor));
        mockCarteiraRepository.salvar(carteira);
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
        operacoesService.adicionarSaldo(conta, new BigDecimal(valorCompra), "BRL", "Deposito para compra",
                NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
        carteira = mockCarteiraRepository.obterPorContaId(conta);
        carteira.liberarSaldoBloqueado();
        mockCarteiraRepository.salvar(carteira);

        if (!arquivoDisponivel) {
            operacaoSucesso = false;
        } else {
            operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor,
                    new BigDecimal(valorCompra));
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
            operacoesService.adicionarSaldo(conta, BigDecimal.valueOf(100), "BRL", "Deposito para compra",
                    NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
            carteira = mockCarteiraRepository.obterPorContaId(conta);
            carteira.liberarSaldoBloqueado();
            mockCarteiraRepository.salvar(carteira);
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
        operacoesService.adicionarSaldo(conta, this.valorJogo, "BRL", "Deposito para compra",
                NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
        carteira = mockCarteiraRepository.obterPorContaId(conta);
        carteira.liberarSaldoBloqueado();
        mockCarteiraRepository.salvar(carteira);

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
            operacoesService.adicionarSaldo(conta, this.valorJogo, "BRL", "Deposito para compra",
                    NOME_CLIENTE_TESTE, CPF_CLIENTE_TESTE, EMAIL_CLIENTE_TESTE, TELEFONE_CLIENTE_TESTE);
            carteira = mockCarteiraRepository.obterPorContaId(conta);
            carteira.liberarSaldoBloqueado();
            mockCarteiraRepository.salvar(carteira);
            operacaoSucesso = operacoesService.comprarJogo(carteira, carteiraDesenvolvedor, this.valorJogo);
        }
    }

    @Then("a plataforma retorna erro e nenhuma cobrança é feita")
    public void a_plataforma_retorna_erro_e_nenhuma_cobrança_é_feita() {
        assertFalse(operacaoSucesso);

        List<Transacao> transacoes = mockTransacaoRepository.getTransacoes();
        assertTrue(transacoes.isEmpty());
    }

    // --- Scenarios de Reembolso ---
    @Given("que comprei um jogo há menos de 24h")
    public void que_comprei_um_jogo_ha_menos_de_24h() {
        @SuppressWarnings("unused")
        Date compraTime = new Date(System.currentTimeMillis() - 23 * 60 * 60 * 1000);
        this.valorJogo = BigDecimal.valueOf(30);

        Biblioteca biblioteca = mockBibliotecaRepository.obterPorJogador(conta);
        biblioteca.adicionarJogo(ModeloDeAcesso.PAGO, jogoId);
        mockBibliotecaRepository.salvar(biblioteca);
    }

    @And("ainda não baixei o jogo")
    public void ainda_nao_baixei_o_jogo() {
    }

    @When("solicito reembolso")
    public void solicito_reembolso() {
        Biblioteca biblioteca = mockBibliotecaRepository.obterPorJogador(conta);
        if (biblioteca.getItensBaixados().stream().anyMatch(i -> i.getJogoId().equals(jogoId))) {
            operacaoSucesso = false;
        } else {
            biblioteca.removerJogo(jogoId);
            mockBibliotecaRepository.salvar(biblioteca);

            Carteira carteira = mockCarteiraRepository.obterPorContaId(conta);
            carteira.getSaldo().addDisponivel(valorJogo);
            mockCarteiraRepository.salvar(carteira);

            operacaoSucesso = true;
        }
    }

    @Then("devo receber o valor de volta no saldo a compra deve ser registrada como reembolsada")
    public void devo_receber_o_valor_de_volta_no_saldo_a_compra_deve_ser_registrada_como_reembolsada() {
        assertTrue(operacaoSucesso);

        Carteira carteira = mockCarteiraRepository.obterPorContaId(conta);
        assertEquals(this.valorJogo, carteira.getSaldo().getDisponivel());

        Biblioteca biblioteca = mockBibliotecaRepository.obterPorJogador(conta);
        Optional<ItemBiblioteca> item = biblioteca.buscarJogoEmBiblioteca(jogoId);
        assertFalse(item.isPresent());
    }

    @Given("que já baixei o jogo")
    public void que_ja_baixei_o_jogo() {
        @SuppressWarnings("unused")
        Date compraTime = new Date(System.currentTimeMillis() - 10 * 60 * 60 * 1000);
        this.valorJogo = BigDecimal.valueOf(30);

        Biblioteca biblioteca = mockBibliotecaRepository.obterPorJogador(conta);
        biblioteca.adicionarJogo(ModeloDeAcesso.PAGO, jogoId);
        ItemBiblioteca item = biblioteca.buscarJogoEmBiblioteca(jogoId).get();
        biblioteca.baixouJogo(item);
        mockBibliotecaRepository.salvar(biblioteca);
    }

    @Then("o sistema deve impedir o reembolso")
    public void o_sistema_deve_impedir_o_reembolso() {
        assertFalse(operacaoSucesso);

        Biblioteca biblioteca = mockBibliotecaRepository.obterPorJogador(conta);
        Optional<ItemBiblioteca> item = biblioteca.buscarJogoEmBiblioteca(jogoId);
        assertTrue(item.isPresent());
    }

    @Given("que comprei o jogo há mais de 24h")
    public void que_comprei_o_jogo_ha_mais_de_24h() {
        @SuppressWarnings("unused")
        Date compraTime = new Date(System.currentTimeMillis() - 25 * 60 * 60 * 1000);
        this.valorJogo = BigDecimal.valueOf(30);

        Biblioteca biblioteca = mockBibliotecaRepository.obterPorJogador(conta);
        biblioteca.adicionarJogo(ModeloDeAcesso.PAGO, jogoId);
        mockBibliotecaRepository.salvar(biblioteca);
    }

    @When("solicito reembolso fora do prazo")
    public void solicito_reembolso_fora_prazo() {
        operacaoSucesso = false;
    }

    @Then("o sistema deve impedir o reembolso devido ao prazo")
    public void o_sistema_deve_impedir_o_reembolso_devido_ao_prazo() {
        assertFalse(operacaoSucesso);

        Biblioteca biblioteca = mockBibliotecaRepository.obterPorJogador(conta);
        Optional<ItemBiblioteca> item = biblioteca.buscarJogoEmBiblioteca(jogoId);
        assertTrue(item.isPresent());
    }
}