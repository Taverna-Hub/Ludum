package org.ludum.identidade.seguimento.services;

import org.ludum.catalogo.jogo.JogoRepository;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.tag.TagRepository;
import org.ludum.catalogo.tag.entidades.TagId;
import org.ludum.identidade.bloqueio.repositories.BloqueioRepository;
import org.ludum.identidade.bloqueio.entities.Bloqueio;
import org.ludum.identidade.bloqueio.entities.BloqueioId;
import org.ludum.identidade.conta.repositories.ContaRepository;
import org.ludum.identidade.conta.entities.Conta;
import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.identidade.conta.enums.StatusConta;
import org.ludum.identidade.seguimento.repositories.SeguimentoRepository;
import org.ludum.identidade.seguimento.enums.TipoAlvo;
import org.ludum.identidade.seguimento.entities.AlvoId;
import org.ludum.identidade.seguimento.entities.Seguimento;
import org.ludum.identidade.seguimento.entities.SeguimentoId;

public class RelacionamentoService {

    private final SeguimentoRepository seguimentoRepository;
    private final ContaRepository contaRepository;
    private final BloqueioRepository bloqueioRepository;
    private final JogoRepository jogoRepository;
    private final TagRepository tagRepository;

    public RelacionamentoService(
            SeguimentoRepository seguimentoRepository,
            ContaRepository contaRepository,
            BloqueioRepository bloqueioRepository,
            JogoRepository jogoRepository, TagRepository tagRepository) {

        this.seguimentoRepository = seguimentoRepository;
        this.contaRepository = contaRepository;
        this.bloqueioRepository = bloqueioRepository;
        this.jogoRepository = jogoRepository;
        this.tagRepository = tagRepository;
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
            throw new IllegalArgumentException("Já existe um seguimento entre estas entidades.");
        }



        switch (tipoAlvo) {
            case CONTA:
                ContaId alvoContaId = new ContaId(alvoId.getValue());

                // Verificar se o usuário alvo existe
                Conta alvoConta = contaRepository.obterPorId(alvoContaId);
                if (alvoConta == null) {
                    throw new IllegalArgumentException("Usuário alvo não encontrado.");
                }
                if (bloqueioRepository.buscar(alvoContaId, seguidorId).isPresent()) {
                    throw new IllegalArgumentException("Você está bloqueado por este usuário.");
                }

                if (bloqueioRepository.buscar(seguidorId, alvoContaId).isPresent()) {
                    throw new IllegalArgumentException("Você bloqueou este usuário.");
                }

                break;
            case JOGO:
                JogoId jogoId = new JogoId(alvoId.getValue());

                if (jogoRepository.obterPorId(jogoId) == null) {
                    throw new IllegalArgumentException("Jogo não encontrado.");
                }
                break;

            case DESENVOLVEDORA:

                Conta desenvolvedora = contaRepository.obterPorId(new ContaId(alvoId.getValue()));

                if (desenvolvedora == null) {
                    throw new IllegalArgumentException("Usuário alvo não encontrado.");
                }

                if (bloqueioRepository.buscar(desenvolvedora.getId(), seguidorId).isPresent()) {
                    throw new IllegalArgumentException("Você está bloqueado por este usuário.");
                }

                if (bloqueioRepository.buscar(seguidorId, desenvolvedora.getId()).isPresent()) {
                    throw new IllegalArgumentException("Você bloqueou este usuário.");
                }
                break;
            case TAG:

                TagId tagId = new TagId(alvoId.getValue());
                if (tagRepository.obterPorId(tagId) == null) {
                    throw new IllegalArgumentException("Tag não encontrado.");
                }
                if (jogoRepository.obterJogosPorTag(tagId).isEmpty()) {
                    throw new IllegalArgumentException("Tag não disponivel, nenhum jogo atrelado á ela");
                }
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
        if (bloqueioRepository.buscar(bloqueadorId, alvoId).isPresent()) {
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
    public void desbloquearConta(ContaId bloqueadorId, ContaId alvoId) {
        bloqueioRepository.buscar(bloqueadorId, alvoId).ifPresent(bloqueioRepository::remover);
    }
}
