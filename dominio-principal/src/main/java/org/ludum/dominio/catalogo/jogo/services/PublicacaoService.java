package org.ludum.dominio.catalogo.jogo.services;

import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.dominio.catalogo.jogo.estrategias.*;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;
import org.ludum.dominio.identidade.conta.enums.StatusConta;
import org.ludum.dominio.identidade.conta.enums.TipoConta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PublicacaoService {

    private final JogoRepository jogoRepository;
    private final ContaRepository contaRepository;
    private final Map<StatusPublicacao, EstrategiaPublicacao> estrategias;

    public PublicacaoService(JogoRepository jogoRepository, ContaRepository contaRepository) {
        this.jogoRepository = Objects.requireNonNull(jogoRepository);
        this.contaRepository = Objects.requireNonNull(contaRepository);
        this.estrategias = new HashMap<>();
        inicializarEstrategias();
    }
    
    private void inicializarEstrategias() {
        registrarEstrategia(new PublicacaoEmUpload());
        registrarEstrategia(new PublicacaoAguardandoValidacao());
        registrarEstrategia(new PublicacaoRejeitado());
        registrarEstrategia(new PublicacaoPublicado());
        registrarEstrategia(new PublicacaoArquivado());
    }
    
    private void registrarEstrategia(EstrategiaPublicacao estrategia) {
        for (StatusPublicacao status : StatusPublicacao.values()) {
            if (estrategia.podeAplicar(status)) {
                estrategias.put(status, estrategia);
            }
        }
    }

    public Jogo publicarJogo(ContaId devId, String titulo, String descricao, 
                            java.net.URL capaOficial, List<org.ludum.dominio.catalogo.tag.entidades.Tag> tags,
                            List<java.net.URL> screenshots, List<java.net.URL> videos,
                            boolean isNSFW, java.time.LocalDate dataDeLancamento) {
        
        Objects.requireNonNull(devId, "ContaId não pode ser nulo");
        Objects.requireNonNull(titulo, "Título não pode ser nulo");
        Objects.requireNonNull(descricao, "Descrição não pode ser nula");

        Conta conta = contaRepository.obterPorId(devId);
        if (conta == null) {
            throw new IllegalArgumentException("Conta não encontrada: " + devId);
        }

        if (conta.getTipo() != TipoConta.DESENVOLVEDORA) {
            throw new IllegalStateException(
                "Apenas contas de desenvolvedores podem publicar jogos. Tipo atual: " + conta.getTipo()
            );
        }

        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new IllegalStateException(
                "Apenas contas ativas podem publicar jogos. Status atual: " + conta.getStatus()
            );
        }

        // Gerar ID do jogo
        JogoId jogoId = new JogoId(java.util.UUID.randomUUID().toString());
        
        // Criar jogo com status inicial EM_UPLOAD
        Jogo jogo = new Jogo(
            jogoId,
            devId,
            titulo,
            descricao,
            capaOficial,
            tags,
            isNSFW,
            dataDeLancamento
        );
        
        // Adicionar screenshots
        if (screenshots != null) {
            for (java.net.URL screenshot : screenshots) {
                jogo.adicionarScreenshot(screenshot);
            }
        }
        
        // Adicionar vídeos
        if (videos != null) {
            for (java.net.URL video : videos) {
                jogo.adicionarVideo(video);
            }
        }

        // Pegar estratégia do status atual (EM_UPLOAD)
        StatusPublicacao statusAtual = jogo.getStatus();
        EstrategiaPublicacao estrategia = estrategias.get(statusAtual);
        
        if (estrategia == null) {
            throw new IllegalStateException(
                "Não há estratégia de publicação definida para o status: " + statusAtual
            );
        }
        
        // Validar regras de negócio
        List<String> erros = estrategia.validar(jogo);
        if (!erros.isEmpty()) {
            throw new IllegalStateException(
                "Não foi possível publicar o jogo: " + String.join(", ", erros)
            );
        }
        
        // Executar transição de estado (EM_UPLOAD → AGUARDANDO_VALIDACAO)
        estrategia.executar(jogo);
        
        // Salvar no banco
        jogoRepository.salvar(jogo);
        
        return jogo;
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

        // Usa a strategy de AGUARDANDO_VALIDACAO para rejeitar
        EstrategiaPublicacao estrategia = estrategias.get(StatusPublicacao.AGUARDANDO_VALIDACAO);
        
        if (estrategia == null) {
            throw new IllegalStateException(
                "Estratégia de rejeição não encontrada"
            );
        }
        
        List<String> erros = estrategia.validar(jogo);
        if (!erros.isEmpty()) {
            throw new IllegalStateException(
                "Não foi possível rejeitar o jogo: " + String.join(", ", erros)
            );
        }
        
        estrategia.executar(jogo);
        jogoRepository.salvar(jogo);
        
        // TODO: Notificar desenvolvedor sobre rejeição com motivo
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
                "Apenas o desenvolvedor dono pode arquivar o jogo"
            );
        }

        // Usa a strategy de PUBLICADO para arquivar
        EstrategiaPublicacao estrategia = estrategias.get(StatusPublicacao.PUBLICADO);
        
        if (estrategia == null) {
            throw new IllegalStateException(
                "Estratégia de arquivamento não encontrada"
            );
        }
        
        List<String> erros = estrategia.validar(jogo);
        if (!erros.isEmpty()) {
            throw new IllegalStateException(
                "Não foi possível arquivar o jogo: " + String.join(", ", erros)
            );
        }
        
        estrategia.executar(jogo);
        jogoRepository.salvar(jogo);
    }
    
}
