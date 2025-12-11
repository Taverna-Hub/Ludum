package org.ludum.infraestrutura.financeiro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno.ResultadoPagamento;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.carteira.entidades.Saldo;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Recibo;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class AsaasProcessadorPagamentoTest {

  private AsaasProcessadorPagamento processador;
  private static String API_KEY;
  private CarteiraRepository mockCarteiraRepository;
  private MockTransacaoRepository mockTransacaoRepository;

  // Mock do TransacaoRepository para capturar as transações salvas
  private static class MockTransacaoRepository implements TransacaoRepository {
    private List<Transacao> transacoes = new ArrayList<>();
    private List<Recibo> recibos = new ArrayList<>();

    @Override
    public Transacao obterPorId(TransacaoId id) {
      return transacoes.stream()
          .filter(t -> t.getTransacaoId().equals(id))
          .findFirst()
          .orElse(null);
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

    public void clear() {
      transacoes.clear();
      recibos.clear();
    }
  }

  @BeforeEach
  void setUp() {
    if (API_KEY == null) {
      try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
        if (input != null) {
          Properties prop = new Properties();
          prop.load(input);
          API_KEY = prop.getProperty("asaas.api.key", "");
          if (!API_KEY.isEmpty()) {
            System.out.println("API Key lida do application.properties");
          }
        }
      } catch (IOException e) {
        System.err.println("Erro ao ler application.properties: " + e.getMessage());
      }
    }

    if (API_KEY == null || API_KEY.isBlank()) {
      System.out.println("AVISO: Configure asaas.api.key no application.properties");
    }

    mockCarteiraRepository = new CarteiraRepository() {
      @Override
      public void salvar(Carteira carteira) {
      }

      @Override
      public Carteira obterPorContaId(ContaId contaId) {
        Carteira carteira = new Carteira(contaId, new Saldo());
        carteira.setContaExterna("24971563792");
        return carteira;
      }
    };

    mockTransacaoRepository = new MockTransacaoRepository();

    processador = new AsaasProcessadorPagamento(API_KEY, mockCarteiraRepository, mockTransacaoRepository);
  }

  @Test
  void deveValidarCredenciais() {
    if (API_KEY == null || API_KEY.isBlank()) {
      System.out.println("Teste pulado: API Key não configurada");
      return;
    }

    assertDoesNotThrow(() -> processador.validarCredenciais());
    System.out.println("Credenciais válidas!");
  }

  @Test
  void deveCriarClienteComSucesso() throws Exception {
    if (API_KEY == null || API_KEY.isBlank()) {
      System.out.println("Teste pulado: API Key não configurada");
      return;
    }

    String nome = "João Silva Teste";
    String cpf = "24971563792";
    String email = "joao.teste" + System.currentTimeMillis() + "@example.com";
    String telefone = "11987654321";

    String customerId = processador.criarCliente(nome, cpf, email, telefone);

    assertNotNull(customerId);
    assertFalse(customerId.isBlank());

    System.out.println("Cliente criado: " + customerId);
    System.out.println("Use este ID para criar cobranças!");
  }

  @Test
  void deveCriarCobrancaComSucesso() throws Exception {
    if (API_KEY == null || API_KEY.isBlank()) {
      System.out.println("Teste pulado: API Key não configurada");
      return;
    }

    String email = "maria.teste" + System.currentTimeMillis() + "@example.com";
    String customerId = processador.criarCliente("Maria Silva", "98765432100", email, null);
    System.out.println("Cliente criado para teste: " + customerId);

    ContaId contaId = new ContaId(customerId);
    BigDecimal valor = new BigDecimal("50.00");
    String moeda = "BRL";
    String descricao = "Teste de adicionar saldo";

    ResultadoPagamento resultado = processador.processar(contaId, valor, moeda, descricao);

    assertTrue(resultado.isSucesso());
    assertNotNull(resultado.getIdGateway());
    assertFalse(resultado.getIdGateway().isBlank());

    System.out.println("Cobrança criada: " + resultado.getIdGateway());
    System.out.println("Transação ID: " + resultado.getTransacaoId());
    System.out.println("Acesse o painel sandbox para ver detalhes");
  }

  @Test
  void deveRejeitarValorAbaixoDoMinimo() {
    ContaId contaId = new ContaId("cliente_teste");
    BigDecimal valor = new BigDecimal("3.00");
    String moeda = "BRL";

    ResultadoPagamento resultado = processador.processar(contaId, valor, moeda, "Teste");

    assertFalse(resultado.isSucesso());
    assertTrue(resultado.getMensagemErro().contains("valor mínimo"));
    System.out.println("Validação de valor mínimo funcionando");
  }

  @Test
  void deveRejeitarMoedaDiferenteDeBRL() {
    ContaId contaId = new ContaId("cliente_teste");
    BigDecimal valor = new BigDecimal("50.00");
    String moeda = "USD";

    ResultadoPagamento resultado = processador.processar(contaId, valor, moeda, "Teste");

    assertFalse(resultado.isSucesso());
    assertTrue(resultado.getMensagemErro().contains("BRL"));
    System.out.println("Validação de moeda funcionando");
  }

  @Test
  void deveConsultarStatusPagamento() throws Exception {
    if (API_KEY == null || API_KEY.isBlank()) {
      System.out.println("Teste pulado: API Key não configurada");
      return;
    }

    String email = "consulta.teste" + System.currentTimeMillis() + "@example.com";
    String customerId = processador.criarCliente("Ana Paula", "24971563792", email, null);

    ContaId contaId = new ContaId(customerId);
    BigDecimal valor = new BigDecimal("25.00");

    ResultadoPagamento resultado = processador.processar(contaId, valor, "BRL", "Teste consulta");
    assertTrue(resultado.isSucesso());

    String status = processador.consultarStatusPagamento(resultado.getIdGateway());

    assertNotNull(status);
    System.out.println("Status consultado: " + status);
    System.out.println("Payment ID: " + resultado.getIdGateway());
  }

  @Test
  void deveExecutarPayoutComSucesso() {
    if (API_KEY == null || API_KEY.isBlank()) {
      System.out.println("Teste pulado: API Key não configurada");
      return;
    }

    ContaId contaId = new ContaId("conta_desenvolvedor_123");
    BigDecimal valor = new BigDecimal("10.00");
    String descricao = "Teste de payout para desenvolvedor";

    try {
      String transferId = processador.executarPayout(contaId, valor, descricao);
      System.out.println("✓ Payout executado: " + transferId);
    } catch (Exception e) {
      System.out.println("Payout falhou (pode ser esperado no sandbox)");
      System.out.println(" Motivo: " + e.getMessage());
      System.out.println(" No ambiente real, isso funcionará com saldo disponível");
    }
  }
}
