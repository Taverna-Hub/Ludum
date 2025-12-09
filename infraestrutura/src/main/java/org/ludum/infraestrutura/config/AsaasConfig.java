package org.ludum.infraestrutura.config;

import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno;
import org.ludum.infraestrutura.financeiro.AsaasProcessadorPagamento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class AsaasConfig {

  @Value("${asaas.api.key:}")
  private String apiKey;

  @Autowired
  private CarteiraRepository carteiraRepository;

  @PostConstruct
  public void init() {
    if (apiKey == null || apiKey.isBlank()) {
      System.out.println("ATENÇÃO: Asaas não configurado!");
    } else {
      System.out.println("Asaas configurado!");
    }
  }

  @Bean
  public ProcessadorPagamentoExterno processadorPagamento() {
    AsaasProcessadorPagamento processador = new AsaasProcessadorPagamento(apiKey, carteiraRepository);

    if (apiKey != null && !apiKey.isBlank()) {
      processador.validarCredenciais();
    }

    return processador;
  }
}
