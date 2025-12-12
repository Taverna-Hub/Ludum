package org.ludum.dominio.financeiro.carteira;

import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.financeiro.transacao.enums.StatusTransacao;
import org.ludum.dominio.financeiro.transacao.enums.TipoTransacao;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class ProcessadorPagamentoExterno {

  private final TransacaoRepository transacaoRepository;

  protected ProcessadorPagamentoExterno(TransacaoRepository transacaoRepository) {
    this.transacaoRepository = transacaoRepository;
  }

  public final ResultadoPagamento processar(ContaId contaId, BigDecimal valor, String moeda, String descricao,
      String nomeCliente, String cpfCnpjCliente, String emailCliente, String telefoneCliente) {
    String transacaoId = gerarIdTransacao();

    beforeProcessar(contaId, valor, moeda);

    try {
      validarSolicitacao(contaId, valor, moeda);

      configurarCliente(nomeCliente, cpfCnpjCliente, emailCliente, telefoneCliente);

      Object dadosGateway = prepararDadosGateway(contaId, valor, moeda, descricao);

      String idGateway = executarPagamentoNoGateway(dadosGateway, valor);

      registrarResultado(transacaoId, idGateway, true, contaId, valor);

      afterProcessar(transacaoId, true);

      return ResultadoPagamento.sucesso(transacaoId, idGateway);

    } catch (Exception e) {
      registrarResultado(transacaoId, null, false, contaId, valor);

      afterProcessar(transacaoId, false);

      return ResultadoPagamento.falha(transacaoId, e.getMessage());
    }
  }

  /**
   * Configura o cliente no gateway de pagamento.
   * Implementações podem sobrescrever este método para criar/buscar o cliente no
   * gateway.
   */
  protected void configurarCliente(String nome, String cpfCnpj, String email, String telefone) {
    // Implementação padrão não faz nada - subclasses podem sobrescrever
  }

  protected abstract void validarSolicitacao(ContaId contaId, BigDecimal valor, String moeda);

  protected abstract Object prepararDadosGateway(ContaId contaId, BigDecimal valor, String moeda, String descricao);

  protected abstract String executarPagamentoNoGateway(Object dadosGateway, BigDecimal valor) throws Exception;

  public abstract String executarPayout(ContaId contaId, BigDecimal valor, String descricao) throws Exception;

  protected void registrarResultado(String transacaoId, String idGateway, boolean sucesso,
      ContaId contaId, BigDecimal valor) {
    if (transacaoRepository != null) {
      Transacao transacao = new Transacao(
          new TransacaoId(transacaoId),
          contaId,
          null,
          TipoTransacao.CREDITO,
          sucesso ? StatusTransacao.CONFIRMADA : StatusTransacao.CANCELADA,
          LocalDateTime.now(),
          valor);
      transacaoRepository.salvar(transacao);
    }
  }

  protected void beforeProcessar(ContaId contaId, BigDecimal valor, String moeda) {
  }

  protected void afterProcessar(String transacaoId, boolean sucesso) {
  }

  private String gerarIdTransacao() {
    return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
  }

  protected TransacaoRepository getTransacaoRepository() {
    return transacaoRepository;
  }

  public static class ResultadoPagamento {
    private final boolean sucesso;
    private final String transacaoId;
    private final String idGateway;
    private final String mensagemErro;
    private final LocalDateTime dataProcessamento;

    private ResultadoPagamento(boolean sucesso, String transacaoId, String idGateway, String mensagemErro) {
      this.sucesso = sucesso;
      this.transacaoId = transacaoId;
      this.idGateway = idGateway;
      this.mensagemErro = mensagemErro;
      this.dataProcessamento = LocalDateTime.now();
    }

    public static ResultadoPagamento sucesso(String transacaoId, String idGateway) {
      return new ResultadoPagamento(true, transacaoId, idGateway, null);
    }

    public static ResultadoPagamento falha(String transacaoId, String mensagemErro) {
      return new ResultadoPagamento(false, transacaoId, null, mensagemErro);
    }

    public boolean isSucesso() {
      return sucesso;
    }

    public String getTransacaoId() {
      return transacaoId;
    }

    public String getIdGateway() {
      return idGateway;
    }

    public String getMensagemErro() {
      return mensagemErro;
    }

    public LocalDateTime getDataProcessamento() {
      return dataProcessamento;
    }
  }
}
