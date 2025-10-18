package org.ludum.catalogo.jogo.services;

import org.ludum.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.catalogo.jogo.entidades.Jogo;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.identidade.conta.entities.Conta;
import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.identidade.conta.repositories.ContaRepository;
import org.ludum.identidade.conta.enums.StatusConta;
import org.ludum.identidade.conta.enums.TipoConta;

import java.util.Objects;

public class PublicacaoService {

    private final JogoRepository jogoRepository;
    private final ContaRepository contaRepository;

    public PublicacaoService(JogoRepository jogoRepository, ContaRepository contaRepository) {
        this.jogoRepository = Objects.requireNonNull(jogoRepository);
        this.contaRepository = Objects.requireNonNull(contaRepository);
    }

    public void publicarJogo(ContaId devId, JogoId jogoId) {
        Objects.requireNonNull(devId, "ContaId não pode ser nulo");
        Objects.requireNonNull(jogoId, "JogoId não pode ser nulo");

        Conta conta = contaRepository.obterPorId(devId);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada: " + devId);
        }

        if (conta.getTipo() != TipoConta.DESENVOLVEDORA) {
            throw new IllegalStateException(
                    "Apenas contas de desenvolvedores podem publicar jogos. Tipo atual: " + conta.getTipo());
        }

        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new IllegalStateException(
                    "Apenas contas ativas podem publicar jogos. Status atual: " + conta.getStatus());
        }

        Jogo jogo = jogoRepository.obterPorId(jogoId);
        if (jogo == null) {
            throw new IllegalArgumentException("Jogo não encontrado: " + jogoId);
        }

        if (!jogo.getDesenvolvedoraId().equals(devId)) {
            throw new IllegalStateException(
                    "Apenas o desenvolvedor dono pode publicar o jogo. " +
                            "Jogo pertence a: " + jogo.getDesenvolvedoraId() +
                            ", tentativa de: " + devId);
        }

        boolean slugDuplicada = jogoRepository.existeSlugParaDesenvolvedora(devId, jogo.getSlug());
        if (slugDuplicada) {
            throw new IllegalStateException(
                    "Você já possui um jogo com este título/slug: " + jogo.getSlug().getValor());
        }

        Jogo jogoComMesmaSlug = jogoRepository.obterPorSlug(jogo.getSlug());
        if (jogoComMesmaSlug != null && !jogoComMesmaSlug.getId().equals(jogoId)) {
            throw new IllegalStateException(
                    "Já existe outro jogo com esta slug. Por favor, escolha um título diferente. " +
                            "Slug: " + jogo.getSlug().getValor());
        }

        if (jogo.getStatus() != StatusPublicacao.AGUARDANDO_VALIDACAO) {
            throw new IllegalStateException(
                    "Jogo precisa estar aguardando validação para ser publicado. " +
                            "Status atual: " + jogo.getStatus());
        }

        jogo.publicar();

        jogoRepository.salvar(jogo);

        // TODO: Notificar seguidores da desenvolvedora sobre novo jogo
        // notificacaoService.notificarNovoJogo(jogo);
    }

    public void rejeitarJogo(JogoId jogoId, String motivo) {
        Objects.requireNonNull(jogoId, "JogoId não pode ser nulo");
        Objects.requireNonNull(motivo, "Motivo não pode ser nulo");

        if (motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo da rejeição não pode ser vazio");
        }

        Jogo jogo = jogoRepository.obterPorId(jogoId);
        if (jogo == null) {
            throw new IllegalArgumentException("Jogo não encontrado: " + jogoId);
        }

        jogo.rejeitar();
        jogoRepository.salvar(jogo);

        // TODO: Notificar desenvolvedor sobre rejeição com motivo
        // notificacaoService.notificarRejeicao(jogo, motivo);
    }

    public void arquivarJogo(ContaId devId, JogoId jogoId) {
        Objects.requireNonNull(devId, "ContaId não pode ser nulo");
        Objects.requireNonNull(jogoId, "JogoId não pode ser nulo");

        Jogo jogo = jogoRepository.obterPorId(jogoId);
        if (jogo == null) {
            throw new IllegalArgumentException("Jogo não encontrado: " + jogoId);
        }

        if (!jogo.getDesenvolvedoraId().equals(devId)) {
            throw new IllegalStateException(
                    "Apenas o desenvolvedor dono pode arquivar o jogo");
        }

        jogo.arquivar();
        jogoRepository.salvar(jogo);
    }
}
