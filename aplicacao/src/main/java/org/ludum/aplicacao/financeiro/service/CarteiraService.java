package org.ludum.aplicacao.financeiro.service;

import org.ludum.aplicacao.financeiro.dto.AdicionarSaldoRequest;
import org.ludum.aplicacao.financeiro.dto.CarteiraResponse;
import org.ludum.aplicacao.financeiro.dto.PagamentoResponse;
import org.ludum.dominio.financeiro.carteira.CarteiraRepository;
import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno;
import org.ludum.dominio.financeiro.carteira.ProcessadorPagamentoExterno.ResultadoPagamento;
import org.ludum.dominio.financeiro.carteira.entidades.Carteira;
import org.ludum.dominio.financeiro.carteira.entidades.Saldo;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.infraestrutura.financeiro.AsaasProcessadorPagamento;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class CarteiraService {

  private final CarteiraRepository carteiraRepository;
  private final ProcessadorPagamentoExterno processadorPagamento;

  public CarteiraService(CarteiraRepository carteiraRepository,
      ProcessadorPagamentoExterno processadorPagamento) {
    this.carteiraRepository = carteiraRepository;
    this.processadorPagamento = processadorPagamento;
  }

  @Transactional
  public PagamentoResponse adicionarSaldo(String contaIdStr, AdicionarSaldoRequest request) {
    ContaId contaId = new ContaId(contaIdStr);

    Carteira carteira = carteiraRepository.obterPorContaId(contaId);
    if (carteira == null) {
      carteira = new Carteira(contaId, new Saldo());
      carteiraRepository.salvar(carteira);
    }

    if (processadorPagamento instanceof AsaasProcessadorPagamento) {
      AsaasProcessadorPagamento asaas = (AsaasProcessadorPagamento) processadorPagamento;
      try {
        String nome = request.getNomeCartao();
        String cpfCnpj = request.getCpfCnpj();
        String email = contaIdStr + "@ludum.app";
        String telefone = request.getTelefone();

        String customerId = asaas.criarCliente(nome, cpfCnpj, email, telefone);
        System.out.println("Customer criado no Asaas: " + customerId);

        asaas.setCustomerId(customerId);
      } catch (Exception e) {
        System.err.println("Erro ao criar customer no Asaas: " + e.getMessage());
        return PagamentoResponse.falha(null, "Erro ao criar cliente: " + e.getMessage());
      }
    }

    try {
      ResultadoPagamento resultado = processadorPagamento.processar(
          contaId,
          request.getValor(),
          "BRL",
          "Adição de saldo via cartão");

      if (resultado.isSucesso()) {
        carteira.getSaldo().addDisponivel(request.getValor());
        carteiraRepository.salvar(carteira);

        return PagamentoResponse.sucesso(
            resultado.getTransacaoId(),
            resultado.getIdGateway(),
            request.getValor());
      } else {
        return PagamentoResponse.falha(
            resultado.getTransacaoId(),
            resultado.getMensagemErro());
      }
    } finally {
      if (processadorPagamento instanceof AsaasProcessadorPagamento) {
        ((AsaasProcessadorPagamento) processadorPagamento).clearCustomerId();
      }
    }
  }

  public CarteiraResponse obterCarteira(String contaIdStr) {
    ContaId contaId = new ContaId(contaIdStr);
    Carteira carteira = carteiraRepository.obterPorContaId(contaId);

    if (carteira == null) {
      return new CarteiraResponse(
          contaIdStr,
          BigDecimal.ZERO,
          BigDecimal.ZERO,
          false);
    }

    return new CarteiraResponse(
        carteira.getId().getValue(),
        carteira.getSaldo().getDisponivel(),
        carteira.getSaldo().getBloqueado(),
        carteira.isContaExternaValida());
  }

  @Transactional
  public void bloquearSaldo(String contaIdStr, BigDecimal valor) {
    ContaId contaId = new ContaId(contaIdStr);
    Carteira carteira = carteiraRepository.obterPorContaId(contaId);

    if (carteira == null) {
      throw new IllegalStateException("Carteira não encontrada");
    }

    if (carteira.getSaldo().getDisponivel().compareTo(valor) < 0) {
      throw new IllegalStateException("Saldo insuficiente");
    }

    carteira.getSaldo().subtrairDisponivel(valor);
    carteira.getSaldo().addBloqueado(valor);
    carteiraRepository.salvar(carteira);
  }

  @Transactional
  public void liberarSaldoBloqueado(String contaIdStr) {
    ContaId contaId = new ContaId(contaIdStr);
    Carteira carteira = carteiraRepository.obterPorContaId(contaId);

    if (carteira == null) {
      throw new IllegalStateException("Carteira não encontrada");
    }

    carteira.liberarSaldoBloqueado();
    carteiraRepository.salvar(carteira);
  }
}
