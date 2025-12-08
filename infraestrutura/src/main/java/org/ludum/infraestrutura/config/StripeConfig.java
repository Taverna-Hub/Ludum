package org.ludum.infraestrutura.config;

import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno;
import org.ludum.infraestrutura.financeiro.StripeProcessadorPagamento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

  @Value("${stripe.secret.key:}")
  private String secretKey;

  @Value("${stripe.publishable.key:}")
  private String publishableKey;

  @PostConstruct
  public void init() {

    if (secretKey == null || secretKey.isBlank() || secretKey.equals("sk_test_YOUR_SECRET_KEY_HERE")) {
      System.out.println("ATENÇÃO: Stripe não configurado!");
    } else {
      boolean isTest = secretKey.startsWith("sk_test_");
      System.out.println("Stripe configurado!");
      if (!isTest) {
        System.out.println("\nAVISO: Usando chaves de PRODUÇÃO!");
      }
    }
  }

  @Bean
  public ProcessadorPagamentoExterno stripeProcessadorPagamento() {
    StripeProcessadorPagamento processador = new StripeProcessadorPagamento(secretKey, publishableKey);

    if (secretKey != null && !secretKey.isBlank() && !secretKey.equals("sk_test_YOUR_SECRET_KEY_HERE")) {
      processador.validarCredenciais();
    }

    return processador;
  }
}
