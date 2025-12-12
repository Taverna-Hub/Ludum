package org.ludum.backend.apresentacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class AdicionarSaldoRequest {

  @NotNull(message = "Valor é obrigatório")
  private BigDecimal valor;

  @NotBlank(message = "Nome do cliente é obrigatório")
  @Size(min = 3, max = 100, message = "Nome do cliente deve ter entre 3 e 100 caracteres")
  private String nomeCliente;

  @NotBlank(message = "Email do cliente é obrigatório")
  private String emailCliente;

  @NotBlank(message = "CPF/CNPJ do cliente é obrigatório")
  @Pattern(regexp = "\\d{11}|\\d{14}", message = "CPF deve ter 11 dígitos ou CNPJ 14 dígitos")
  private String cpfCnpjCliente;

  @Pattern(regexp = "\\d{10,11}", message = "Telefone do cliente inválido")
  private String telefoneCliente;

  @NotBlank(message = "Número do cartão é obrigatório")
  @Pattern(regexp = "\\d{16}", message = "Número do cartão deve conter 16 dígitos")
  private String numeroCartao;

  @NotBlank(message = "Nome no cartão é obrigatório")
  @Size(min = 3, max = 100, message = "Nome no cartão deve ter entre 3 e 100 caracteres")
  private String nomeCartao;

  @NotBlank(message = "Mês de validade é obrigatório")
  @Pattern(regexp = "\\d{2}", message = "Mês deve conter 2 dígitos")
  private String mesValidade;

  @NotBlank(message = "Ano de validade é obrigatório")
  @Pattern(regexp = "\\d{2}", message = "Ano deve conter 2 dígitos")
  private String anoValidade;

  @NotBlank(message = "CVC é obrigatório")
  @Pattern(regexp = "\\d{3}", message = "CVC deve conter 3 dígitos")
  private String cvc;

  @NotBlank(message = "CPF/CNPJ do titular do cartão é obrigatório")
  @Pattern(regexp = "\\d{11}|\\d{14}", message = "CPF deve ter 11 dígitos ou CNPJ 14 dígitos")
  private String cpfCnpjTitular;

  @NotBlank(message = "CEP é obrigatório")
  @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
  private String cep;

  @NotBlank(message = "Número do endereço é obrigatório")
  private String numeroEndereco;

  @NotBlank(message = "Telefone do titular é obrigatório")
  @Pattern(regexp = "\\(\\d{2}\\) ?\\d{4,5}-?\\d{4}", message = "Telefone inválido")
  private String telefoneTitular;

  public AdicionarSaldoRequest() {
  }

  // Getters e Setters - Dados do Cliente
  public String getNomeCliente() {
    return nomeCliente;
  }

  public void setNomeCliente(String nomeCliente) {
    this.nomeCliente = nomeCliente;
  }

  public String getEmailCliente() {
    return emailCliente;
  }

  public void setEmailCliente(String emailCliente) {
    this.emailCliente = emailCliente;
  }

  public String getCpfCnpjCliente() {
    return cpfCnpjCliente;
  }

  public void setCpfCnpjCliente(String cpfCnpjCliente) {
    this.cpfCnpjCliente = cpfCnpjCliente;
  }

  public String getTelefoneCliente() {
    return telefoneCliente;
  }

  public void setTelefoneCliente(String telefoneCliente) {
    this.telefoneCliente = telefoneCliente;
  }

  // Getters e Setters
  public BigDecimal getValor() {
    return valor;
  }

  public void setValor(BigDecimal valor) {
    this.valor = valor;
  }

  public String getNumeroCartao() {
    return numeroCartao;
  }

  public void setNumeroCartao(String numeroCartao) {
    this.numeroCartao = numeroCartao;
  }

  public String getNomeCartao() {
    return nomeCartao;
  }

  public void setNomeCartao(String nomeCartao) {
    this.nomeCartao = nomeCartao;
  }

  public String getMesValidade() {
    return mesValidade;
  }

  public void setMesValidade(String mesValidade) {
    this.mesValidade = mesValidade;
  }

  public String getAnoValidade() {
    return anoValidade;
  }

  public void setAnoValidade(String anoValidade) {
    this.anoValidade = anoValidade;
  }

  public String getCvc() {
    return cvc;
  }

  public void setCvc(String cvc) {
    this.cvc = cvc;
  }

  public String getCpfCnpjTitular() {
    return cpfCnpjTitular;
  }

  public void setCpfCnpjTitular(String cpfCnpjTitular) {
    this.cpfCnpjTitular = cpfCnpjTitular;
  }

  public String getCep() {
    return cep;
  }

  public void setCep(String cep) {
    this.cep = cep;
  }

  public String getNumeroEndereco() {
    return numeroEndereco;
  }

  public void setNumeroEndereco(String numeroEndereco) {
    this.numeroEndereco = numeroEndereco;
  }

  public String getTelefoneTitular() {
    return telefoneTitular;
  }

  public void setTelefoneTitular(String telefoneTitular) {
    this.telefoneTitular = telefoneTitular;
  }
}
