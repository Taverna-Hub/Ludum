package org.ludum.dominio.financeiro.carteira;

import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public abstract class ProcessadorPagamentoExterno {

  public final ResultadoPagamento processar(ContaId contaId, BigDecimal valor, String moeda, String descricao) {
    String transacaoId = gerarIdTransacao();

    beforeProcessar(contaId, valor, moeda);

    try {
      validarSolicitacao(contaId, valor, moeda);

      Object dadosGateway = prepararDadosGateway(contaId, valor, moeda, descricao);

      String idGateway = executarPagamentoNoGateway(dadosGateway, valor);

      registrarResultado(transacaoId, idGateway, true);

      afterProcessar(transacaoId, true);

      return ResultadoPagamento.sucesso(transacaoId, idGateway);

    } catch (Exception e) {
      registrarResultado(transacaoId, null, false);
      afterProcessar(transacaoId, false);

      return ResultadoPagamento.falha(transacaoId, e.getMessage());
    }
  }

  protected abstract void validarSolicitacao(ContaId contaId, BigDecimal valor, String moeda);

  protected abstract Object prepararDadosGateway(ContaId contaId, BigDecimal valor, String moeda, String descricao);

  protected abstract String executarPagamentoNoGateway(Object dadosGateway, BigDecimal valor) throws Exception;

  public abstract String executarPayout(String recipientId, BigDecimal valor, String descricao) throws Exception;

  protected void registrarResultado(String transacaoId, String idGateway, boolean sucesso) {
    System.out.println(String.format(
        "Pagamento %s - Transação: %s, Gateway ID: %s",
        sucesso ? "SUCESSO" : "FALHA",
        transacaoId,
        idGateway));
  }

  protected void beforeProcessar(ContaId contaId, BigDecimal valor, String moeda) {
  }

  protected void afterProcessar(String transacaoId, boolean sucesso) {
  }

  private String gerarIdTransacao() {
    return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
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
