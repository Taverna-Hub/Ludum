package org.ludum.dominio.financeiro.carteira;

import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.financeiro.transacao.enums.StatusTransacao;
import org.ludum.dominio.financeiro.transacao.enums.TipoTransacao;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class ProcessadorPagamentoExterno {

  private final TransacaoRepository transacaoRepository;

  protected ProcessadorPagamentoExterno(TransacaoRepository transacaoRepository) {
    this.transacaoRepository = transacaoRepository;
  }

  public final ResultadoPagamento processar(ContaId contaId, BigDecimal valor, String moeda, String descricao,
      String nomeCliente, String cpfCnpjCliente, String emailCliente, String telefoneCliente) {
    beforeProcessar(contaId, valor, moeda);

    try {
      validarSolicitacao(contaId, valor, moeda);

      configurarCliente(nomeCliente, cpfCnpjCliente, emailCliente, telefoneCliente);

      Object dadosGateway = prepararDadosGateway(contaId, valor, moeda, descricao);

      String idGateway = executarPagamentoNoGateway(dadosGateway, valor);

      Transacao transacao = registrarResultado(idGateway, true, contaId, valor);
      String transacaoId = transacao.getTransacaoId().getValue();

      afterProcessar(transacaoId, true);

      return ResultadoPagamento.sucesso(transacaoId, idGateway);

    } catch (Exception e) {
      Transacao transacao = registrarResultado(null, false, contaId, valor);
      String transacaoId = transacao.getTransacaoId().getValue();

      afterProcessar(transacaoId, false);

      return ResultadoPagamento.falha(transacaoId, e.getMessage());
    }
  }

  protected void configurarCliente(String nome, String cpfCnpj, String email, String telefone) {
  }

  protected abstract void validarSolicitacao(ContaId contaId, BigDecimal valor, String moeda);

  protected abstract Object prepararDadosGateway(ContaId contaId, BigDecimal valor, String moeda, String descricao);

  protected abstract String executarPagamentoNoGateway(Object dadosGateway, BigDecimal valor) throws Exception;

  protected Transacao registrarResultado(String idGateway, boolean sucesso,
      ContaId contaId, BigDecimal valor) {
    // Para CREDITO: contaOrigem = null (origem externa), contaDestino = contaId (quem recebe)
    Transacao transacao = new Transacao(
        null,
        null,           // contaOrigem - origem externa (pagamento via cartão)
        contaId,        // contaDestino - conta que recebe o crédito
        TipoTransacao.CREDITO,
        sucesso ? StatusTransacao.CONFIRMADA : StatusTransacao.CANCELADA,
        LocalDateTime.now(),
        valor);
    
    if (transacaoRepository != null) {
      transacaoRepository.salvar(transacao);
    }
    
    return transacao;
  }

  protected void beforeProcessar(ContaId contaId, BigDecimal valor, String moeda) {
  }

  protected void afterProcessar(String transacaoId, boolean sucesso) {
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
