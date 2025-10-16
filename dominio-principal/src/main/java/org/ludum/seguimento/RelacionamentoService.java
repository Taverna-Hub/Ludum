package org.ludum.seguimento;

import org.ludum.bloqueio.BloqueioRepository;
import org.ludum.bloqueio.entidades.Bloqueio;
import org.ludum.bloqueio.entidades.BloqueioId;
import org.ludum.conta.ContaRepository;
import org.ludum.conta.entidades.Conta;
import org.ludum.conta.entidades.ContaId;
import org.ludum.conta.enums.StatusConta;
import org.ludum.seguimento.entidades.AlvoId;
import org.ludum.seguimento.entidades.Seguimento;
import org.ludum.seguimento.entidades.SeguimentoId;

public class RelacionamentoService {

    private final SeguimentoRepository seguimentoRepository;
    private final ContaRepository contaRepository;
    private final BloqueioRepository bloqueioRepository;

    public RelacionamentoService(SeguimentoRepository seguimentoRepository, 
                                  ContaRepository contaRepository,
                                  BloqueioRepository bloqueioRepository) {
        this.seguimentoRepository = seguimentoRepository;
        this.contaRepository = contaRepository;
        this.bloqueioRepository = bloqueioRepository;
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

       

        switch (tipoAlvo) {
            case CONTA:
                // Verificar se o usuário alvo existe
                Conta alvoConta = contaRepository.obterPorId(alvoContaId);
                if (alvoConta == null) {
                    throw new IllegalArgumentException("Usuário alvo não encontrado.");
                }

                // Usuários bloqueados não podem seguir quem bloqueou
                ContaId alvoContaId = new ContaId(alvoId.getValue());

                if (bloqueioRepository.verificarBloqueio(alvoContaId, seguidorId)) {
                    throw new IllegalArgumentException("Você está bloqueado por este usuário.");
                }

                if (bloqueioRepository.verificarBloqueio(seguidorId, alvoContaId)) {
                    throw new IllegalArgumentException("Você bloqueou este usuário.");
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

    public void bloquearConta(ContaId bloqueadorId, ContaId alvoId) {
        // Verificar se não está tentando bloquear a si mesmo
        if (bloqueadorId.getValue().equals(alvoId.getValue())) {
            throw new IllegalArgumentException("Não é possível bloquear a si mesmo.");
        }

        // Verificar se o bloqueador existe
        Conta bloqueador = contaRepository.obterPorId(bloqueadorId);
        if (bloqueador == null) {
            throw new IllegalArgumentException("Bloqueador não encontrado.");
        }
        if (bloqueador.getStatus() != StatusConta.ATIVA) {
            throw new IllegalArgumentException("Conta do bloqueador não está ativa.");
        }

        // Verificar se o alvo existe
        Conta alvo = contaRepository.obterPorId(alvoId);
        if (alvo == null) {
            throw new IllegalArgumentException("Usuário alvo não encontrado.");
        }

        // Verificar se já existe bloqueio
        if (bloqueioRepository.verificarBloqueio(bloqueadorId, alvoId)) {
            throw new IllegalArgumentException("Você já bloqueou este usuário.");
        }

        // Criar o bloqueio
        BloqueioId bloqueioId = new BloqueioId(java.util.UUID.randomUUID().toString());
        Bloqueio novoBloqueio = new Bloqueio(bloqueioId, bloqueadorId, alvoId);
        bloqueioRepository.salvar(novoBloqueio);

        // Remover seguimentos existentes entre os usuários
        AlvoId alvoSeguimentoId = new AlvoId(alvoId.getValue());
        seguimentoRepository.obter(bloqueadorId, alvoSeguimentoId).ifPresent(seguimentoRepository::remover);
        
        AlvoId bloqueadorSeguimentoId = new AlvoId(bloqueadorId.getValue());
        seguimentoRepository.obter(alvoId, bloqueadorSeguimentoId).ifPresent(seguimentoRepository::remover);
    }
}
