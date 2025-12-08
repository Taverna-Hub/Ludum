package org.ludum.infraestrutura.financeiro;

import com.stripe.Stripe;
import com.stripe.exception.*;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import java.math.BigDecimal;

public class StripeProcessadorPagamento extends ProcessadorPagamentoExterno {

  private final String stripeSecretKey;
  private final String stripePublishableKey;
  private final boolean usarModoTeste;

  public StripeProcessadorPagamento(String stripeSecretKey, String stripePublishableKey) {
    this.stripeSecretKey = stripeSecretKey;
    this.stripePublishableKey = stripePublishableKey;
    this.usarModoTeste = stripeSecretKey != null && stripeSecretKey.startsWith("sk_test_");

    Stripe.apiKey = stripeSecretKey;
  }

  @Override
  protected void validarSolicitacao(ContaId contaId, BigDecimal valor, String moeda) {
    if (contaId == null || contaId.getValue() == null) {
      throw new IllegalArgumentException("ContaId não pode ser nula");
    }

    if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
      throw new IllegalArgumentException("Valor deve ser maior que zero");
    }

    BigDecimal valorMinimo = new BigDecimal("0.50");
    if (valor.compareTo(valorMinimo) < 0) {
      throw new IllegalArgumentException(
          "Stripe requer valor mínimo de " + valorMinimo + " " + moeda);
    }

    if (moeda == null || moeda.isBlank()) {
      throw new IllegalArgumentException("Moeda não pode ser nula");
    }

    if (!isMoedaSuportada(moeda)) {
      throw new IllegalArgumentException("Moeda não suportada pelo Stripe: " + moeda);
    }

    if (stripeSecretKey == null || !stripeSecretKey.startsWith("sk_")) {
      throw new IllegalStateException("Stripe Secret Key inválida ou não configurada");
    }
  }

  @Override
  protected Object prepararDadosGateway(ContaId contaId, BigDecimal valor, String moeda, String descricao) {
    long valorEmCentavos = valor.multiply(new BigDecimal("100")).longValue();

    PaymentIntentCreateParams.Builder paramsBuilder = PaymentIntentCreateParams.builder()
        .setAmount(valorEmCentavos)
        .setCurrency(moeda.toLowerCase())
        .setDescription(descricao != null ? descricao : "Adicionar saldo Ludum")
        .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.AUTOMATIC)
        .addPaymentMethodType("card");

    paramsBuilder.putMetadata("conta_id", contaId.getValue());
    paramsBuilder.putMetadata("plataforma", "Ludum");
    paramsBuilder.putMetadata("tipo", "adicionar_saldo");
    paramsBuilder.putMetadata("ambiente", usarModoTeste ? "teste" : "producao");

    return paramsBuilder.build();
  }

  @Override
  protected String executarPagamentoNoGateway(Object dadosGateway, BigDecimal valor) throws Exception {
    try {
      PaymentIntentCreateParams params = (PaymentIntentCreateParams) dadosGateway;

      System.out.println("Criando PaymentIntent no Stripe...");
      System.out.println("   Valor: " + params.getAmount() + " centavos");
      System.out.println("   Moeda: " + params.getCurrency());
      System.out.println("   Modo: " + (usarModoTeste ? "TESTE" : "PRODUÇÃO"));

      // Criar PaymentIntent na API do Stripe
      PaymentIntent paymentIntent = PaymentIntent.create(params);

      String paymentIntentId = paymentIntent.getId();
      String status = paymentIntent.getStatus();

      System.out.println("PaymentIntent criado: " + paymentIntentId);
      System.out.println("   Status: " + status);

      if ("succeeded".equals(status)) {
        return paymentIntentId;
      } else if ("requires_payment_method".equals(status) ||
          "requires_confirmation".equals(status) ||
          "requires_action".equals(status)) {

        System.out.println("PaymentIntent criado mas aguardando ação: " + status);
        return paymentIntentId;
      } else if ("processing".equals(status)) {
        System.out.println("Pagamento em processamento...");
        return paymentIntentId;
      } else {
        throw new Exception("Status inesperado do PaymentIntent: " + status);
      }

    } catch (CardException e) {
      System.err.println("Cartão recusado: " + e.getMessage());
      System.err.println("   Código: " + e.getCode());
      System.err.println("   Decline Code: " + e.getDeclineCode());
      throw new Exception("Cartão recusado: " + e.getMessage(), e);

    } catch (RateLimitException e) {
      System.err.println("Rate limit excedido");
      throw new Exception("Muitas requisições. Tente novamente em alguns segundos.", e);

    } catch (InvalidRequestException e) {
      System.err.println("Requisição inválida: " + e.getMessage());
      throw new Exception("Parâmetros inválidos na requisição: " + e.getMessage(), e);

    } catch (AuthenticationException e) {
      System.err.println("Erro de autenticação com Stripe");
      System.err.println("   Verifique sua API key em application.properties");
      throw new Exception("Erro de autenticação com Stripe. Verifique as credenciais.", e);

    } catch (ApiConnectionException e) {
      System.err.println("Erro de conexão com Stripe");
      throw new Exception("Erro ao conectar com Stripe. Verifique sua conexão.", e);

    } catch (ApiException e) {
      System.err.println("Erro na API do Stripe: " + e.getMessage());
      throw new Exception("Erro ao processar pagamento no Stripe: " + e.getMessage(), e);

    } catch (StripeException e) {
      System.err.println("Erro no Stripe: " + e.getMessage());
      throw new Exception("Erro ao processar pagamento: " + e.getMessage(), e);
    }
  }

  @Override
  protected void beforeProcessar(ContaId contaId, BigDecimal valor, String moeda) {
    System.out.println("Processamento Stripe Iniciado");
  }

  @Override
  protected void afterProcessar(String transacaoId, boolean sucesso) {
    System.out.println("Processamento Stripe Finalizado");
    System.out.println("Transação: " + transacaoId);
    System.out.println("Status: " + (sucesso ? "SUCESSO" : "FALHA"));

    if (sucesso) {
      System.out.println("Pagamento processado com sucesso via Stripe!");
      System.out.println("Você pode visualizar detalhes em:");
      System.out.println("  " + (usarModoTeste
          ? "https://dashboard.stripe.com/test/payments"
          : "https://dashboard.stripe.com/payments"));
    } else {
      System.out.println("O pagamento falhou. Verifique os logs acima.");
    }
    System.out.println();
  }

  private boolean isMoedaSuportada(String moeda) {
    String[] moedasSuportadas = {
        "BRL", "USD", "EUR", "GBP", "JPY", "CAD", "AUD", "CHF", "CNY", "SEK",
        "NOK", "DKK", "PLN", "MXN", "SGD", "HKD", "NZD", "KRW", "TRY", "RUB",
        "INR", "ARS", "CLP", "COP", "PEN", "UYU"
    };

    for (String moedaSuportada : moedasSuportadas) {
      if (moedaSuportada.equalsIgnoreCase(moeda)) {
        return true;
      }
    }

    return false;
  }

  public String getStripePublishableKey() {
    return stripePublishableKey;
  }

  public boolean isUsarModoTeste() {
    return usarModoTeste;
  }

  public boolean validarCredenciais() {
    try {
      if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
        System.err.println("Stripe Secret Key não configurada");
        return false;
      }

      if (!stripeSecretKey.startsWith("sk_test_") && !stripeSecretKey.startsWith("sk_live_")) {
        System.err.println("Stripe Secret Key inválida (deve começar com sk_test_ ou sk_live_)");
        return false;
      }

      if (stripePublishableKey == null || stripePublishableKey.isBlank()) {
        System.err.println("Stripe Publishable Key não configurada");
        return false;
      }

      if (!stripePublishableKey.startsWith("pk_test_") && !stripePublishableKey.startsWith("pk_live_")) {
        System.err.println("Stripe Publishable Key inválida (deve começar com pk_test_ ou pk_live_)");
        return false;
      }

      boolean secretIsTest = stripeSecretKey.startsWith("sk_test_");
      boolean publishableIsTest = stripePublishableKey.startsWith("pk_test_");

      if (secretIsTest != publishableIsTest) {
        System.err.println("Secret Key e Publishable Key são de ambientes diferentes!");
        System.err.println("   Secret: " + (secretIsTest ? "teste" : "produção"));
        System.err.println("   Publishable: " + (publishableIsTest ? "teste" : "produção"));
        return false;
      }

      System.out.println("Credenciais Stripe validadas com sucesso");
      System.out.println("   Modo: " + (usarModoTeste ? "TESTE" : "PRODUÇÃO"));
      return true;

    } catch (Exception e) {
      System.err.println("Erro ao validar credenciais: " + e.getMessage());
      return false;
    }
  }
}
