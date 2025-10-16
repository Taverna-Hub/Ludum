package org.ludum.seguimento;

import org.ludum.conta.ContaRepository;
import org.ludum.conta.entidades.Conta;
import org.ludum.conta.entidades.ContaId;
import org.ludum.conta.enums.StatusConta;
import org.ludum.seguimento.entidades.AlvoId;
import org.ludum.seguimento.entidades.Seguimento;
import org.ludum.seguimento.entidades.SeguimentoId;

public class SeguimentoService {

    private final SeguimentoRepository seguimentoRepository;
    private final ContaRepository contaRepository;

    public SeguimentoService(SeguimentoRepository seguimentoRepository, ContaRepository contaRepository) {
        this.seguimentoRepository = seguimentoRepository;
        this.contaRepository = contaRepository;
    }

    public void seguirAlvo(ContaId seguidorId, AlvoId alvoId, TipoAlvo tipoAlvo) {
        // 1. Verificar se o id do alvo não é o id do seguidor (não pode se seguir)
        if (seguidorId.getValue().equals(alvoId.getValue())) {
            throw new IllegalArgumentException("Não é possível seguir a si mesmo.");
        }

        // Verificar se o seguidor existe e não está bloqueado
        Conta seguidor = contaRepository.obterPorId(seguidorId);
        if (seguidor == null) {
            throw new IllegalArgumentException("Seguidor não encontrado.");
        }
        if (seguidor.getStatus() != StatusConta.ATIVA) {
            throw new IllegalArgumentException("Conta do seguidor não está ativa.");
        }

        // Verificar se ainda não existe relação de seguimento entre eles
        if (seguimentoRepository.obter(seguidorId, alvoId).isPresent()) {
            throw new IllegalArgumentException("Já existe um seguimento entre estes usuários.");
        }

        // TODO: Usuários bloqueados não podem seguir quem bloqueou

        switch (tipoAlvo) {
            case CONTA:
                // Verificar se o usuário alvo existe
                ContaId alvoContaId = new ContaId(alvoId.getValue());
                Conta alvoConta = contaRepository.obterPorId(alvoContaId);
                if (alvoConta == null) {
                    throw new IllegalArgumentException("Usuário alvo não encontrado.");
                }
                break;
            case JOGO:
                // TODO: Verificar se o jogo existe
                break;
            case DESENVOLVEDORA:
                // TODO: Verificar se a desenvolvedora existe
                break;
            case TAG:
                // TODO: Verificar se a tag seguida tem pelo menos 1 jogo atrelada a ela
                break;
        }

        // Criar o seguimento
        SeguimentoId seguimentoId = new SeguimentoId(java.util.UUID.randomUUID().toString());
        Seguimento novoSeguimento = new Seguimento(seguimentoId, seguidorId, alvoId, tipoAlvo);
        seguimentoRepository.salvar(novoSeguimento);
    }

    public void deixarDeSeguirAlvo(ContaId seguidorId, AlvoId alvoId) {
        seguimentoRepository.obter(seguidorId, alvoId).ifPresent(seguimentoRepository::remover);
    }
}

