package org.ludum.backend.conta.services;

import org.ludum.backend.conta.dto.AuthResponse;
import org.ludum.backend.conta.dto.LoginRequest;
import org.ludum.backend.conta.dto.RegistroRequest;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.enums.StatusConta;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final ContaRepository contaRepository;
    private final TokenService tokenService;

    public AuthService(ContaRepository contaRepository, TokenService tokenService) {
        this.contaRepository = contaRepository;
        this.tokenService = tokenService;
    }

    public AuthResponse registrar(RegistroRequest request) {
        if (contaRepository.obterPorNome(request.getNome()) != null) {
            throw new IllegalArgumentException("Usuário já existe");
        }

        ContaId novoId = new ContaId(UUID.randomUUID().toString());
        Conta novaConta = new Conta(
                novoId,
                request.getNome(),
                request.getSenha(), // Senha salva como hash diretamente conforme solicitado
                request.getTipo(),
                StatusConta.ATIVA
        );

        contaRepository.salvar(novaConta);

        String token = tokenService.gerarToken(novaConta);
        return new AuthResponse(token, novoId.getValue(), novaConta.getNome());
    }

    public AuthResponse login(LoginRequest request) {
        Conta conta = contaRepository.obterPorNome(request.getNome());

        String token = tokenService.gerarToken(conta);
        return new AuthResponse(token, conta.getId().getValue(), conta.getNome());
    }
}
