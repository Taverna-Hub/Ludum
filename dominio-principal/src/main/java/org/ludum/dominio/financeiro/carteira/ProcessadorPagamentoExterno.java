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

/**
 * Template Method para processamento de pagamentos externos.
 * 
 * Define o esqueleto do algoritmo de pagamento, permitindo que subclasses
 * implementem passos específicos para diferentes gateways (ex: Asaas, Stripe).
 * 
 * Estrutura do Template Method:
 * - Abstract steps (obrigatórios): validarSolicitacao, prepararDadosGateway,
 * executarPagamentoNoGateway
 * - Optional step (com implementação padrão): registrarResultado
 * - Hooks (pontos de extensão): beforeProcessar, afterProcessar
 */
public abstract class ProcessadorPagamentoExterno {

  private final TransacaoRepository transacaoRepository;

  /**
   * Construtor que recebe o repositório de transações para persistência.
   * 
   * @param transacaoRepository repositório para persistir transações
   */
  protected ProcessadorPagamentoExterno(TransacaoRepository transacaoRepository) {
    this.transacaoRepository = transacaoRepository;
  }

  /**
   * Template Method - define o algoritmo de processamento de pagamento.
   * Este método é final para garantir que subclasses não alterem a estrutura do
   * algoritmo.
   */
  public final ResultadoPagamento processar(ContaId contaId, BigDecimal valor, String moeda, String descricao) {
    String transacaoId = gerarIdTransacao();

    // Hook: executado antes do processamento
    beforeProcessar(contaId, valor, moeda);

    try {
      // Abstract step 1: validação específica do gateway
      validarSolicitacao(contaId, valor, moeda);

      // Abstract step 2: preparação dos dados para o gateway
      Object dadosGateway = prepararDadosGateway(contaId, valor, moeda, descricao);

      // Abstract step 3: execução do pagamento no gateway externo
      String idGateway = executarPagamentoNoGateway(dadosGateway, valor);

      // Optional step: registrar resultado com persistência
      registrarResultado(transacaoId, idGateway, true, contaId, valor);

      // Hook: executado após processamento bem-sucedido
      afterProcessar(transacaoId, true);

      return ResultadoPagamento.sucesso(transacaoId, idGateway);

    } catch (Exception e) {
      // Optional step: registrar falha com persistência
      registrarResultado(transacaoId, null, false, contaId, valor);

      // Hook: executado após processamento com falha
      afterProcessar(transacaoId, false);

      return ResultadoPagamento.falha(transacaoId, e.getMessage());
    }
  }

  // ========== ABSTRACT STEPS (obrigatórios para subclasses) ==========

  /**
   * Abstract step: Valida a solicitação de pagamento conforme regras do gateway.
   */
  protected abstract void validarSolicitacao(ContaId contaId, BigDecimal valor, String moeda);

  /**
   * Abstract step: Prepara os dados no formato esperado pelo gateway.
   */
  protected abstract Object prepararDadosGateway(ContaId contaId, BigDecimal valor, String moeda, String descricao);

  /**
   * Abstract step: Executa o pagamento no gateway externo.
   */
  protected abstract String executarPagamentoNoGateway(Object dadosGateway, BigDecimal valor) throws Exception;

  /**
   * Abstract step: Executa um payout (saque) para conta externa.
   */
  public abstract String executarPayout(ContaId contaId, BigDecimal valor, String descricao) throws Exception;

  // ========== OPTIONAL STEP (com implementação padrão) ==========

  /**
   * Optional step: Registra o resultado do processamento persistindo a transação.
   * 
   * Esta é a implementação padrão que persiste no TransacaoRepository.
   * Subclasses podem sobrescrever para adicionar comportamento adicional
   * (ex: enviar notificação, logging específico), mas devem chamar super.
   * 
   * @param transacaoId ID da transação gerado internamente
   * @param idGateway   ID retornado pelo gateway externo (null se falhou)
   * @param sucesso     indica se o pagamento foi bem-sucedido
   * @param contaId     ID da conta do usuário
   * @param valor       valor da transação
   */
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

  // ========== HOOKS (pontos de extensão opcionais) ==========

  /**
   * Hook: Executado antes do processamento iniciar.
   * Subclasses podem sobrescrever para adicionar lógica de pré-processamento.
   */
  protected void beforeProcessar(ContaId contaId, BigDecimal valor, String moeda) {
    // Hook vazio - subclasses podem sobrescrever
  }

  /**
   * Hook: Executado após o processamento (sucesso ou falha).
   * Subclasses podem sobrescrever para adicionar lógica de pós-processamento.
   */
  protected void afterProcessar(String transacaoId, boolean sucesso) {
    // Hook vazio - subclasses podem sobrescrever
  }

  // ========== MÉTODOS AUXILIARES ==========

  private String gerarIdTransacao() {
    return "TXN_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
  }

  /**
   * Retorna o repositório de transações para uso em subclasses se necessário.
   */
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
