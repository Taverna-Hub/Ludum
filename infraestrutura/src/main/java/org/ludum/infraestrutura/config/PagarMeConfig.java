package org.ludum.infraestrutura.config;

import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno;
import org.ludum.infraestrutura.financeiro.PagarMeProcessadorPagamento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

/**
 * Configuração Spring para Pagar.me
 * 
 * Usa Template Method Pattern via ProcessadorPagamentoExterno
 */
@Configuration
public class PagarMeConfig {

  @Value("${pagarme.api.key:}")
  private String apiKey;

  @PostConstruct
  public void init() {
    if (apiKey == null || apiKey.isBlank() || apiKey.equals("sk_test_YOUR_API_KEY_HERE")) {
      System.out.println("========================================");
      System.out.println("ATENÇÃO: Pagar.me não configurado!");
      System.out.println("Configure a chave API no application.properties");
      System.out.println("pagarme.api.key=sk_test_... ou sk_live_...");
      System.out.println("========================================");
    } else {
      boolean isTest = apiKey.startsWith("sk_test_");
      System.out.println("========================================");
      System.out.println("✓ Pagar.me configurado!");
      System.out.println("   Modo: " + (isTest ? "TESTE" : "PRODUÇÃO"));
      if (!isTest) {
        System.out.println("\n⚠️  AVISO: Usando chaves de PRODUÇÃO!");
        System.out.println("   Transações reais serão processadas!");
      }
      System.out.println("========================================");
    }
  }

  @Bean
  public ProcessadorPagamentoExterno processadorPagamento() {
    PagarMeProcessadorPagamento processador = new PagarMeProcessadorPagamento(apiKey);

    if (apiKey != null && !apiKey.isBlank() && !apiKey.equals("sk_test_YOUR_API_KEY_HERE")) {
      processador.validarCredenciais();
    }

    return processador;
  }
}
