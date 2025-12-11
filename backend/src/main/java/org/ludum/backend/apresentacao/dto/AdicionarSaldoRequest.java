package org.ludum.backend.apresentacao.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class AdicionarSaldoRequest {

  @NotNull(message = "Valor é obrigatório")
  private BigDecimal valor;

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

  @NotBlank(message = "CPF/CNPJ é obrigatório")
  @Pattern(regexp = "\\d{11}|\\d{14}", message = "CPF deve ter 11 dígitos ou CNPJ 14 dígitos")
  private String cpfCnpj;

  @NotBlank(message = "CEP é obrigatório")
  @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP inválido")
  private String cep;

  @NotBlank(message = "Número do endereço é obrigatório")
  private String numeroEndereco;

  @NotBlank(message = "Telefone é obrigatório")
  @Pattern(regexp = "\\(\\d{2}\\) ?\\d{4,5}-?\\d{4}", message = "Telefone inválido")
  private String telefone;

  public AdicionarSaldoRequest() {
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

  public String getCpfCnpj() {
    return cpfCnpj;
  }

  public void setCpfCnpj(String cpfCnpj) {
    this.cpfCnpj = cpfCnpj;
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

  public String getTelefone() {
    return telefone;
  }

  public void setTelefone(String telefone) {
    this.telefone = telefone;
  }
}
