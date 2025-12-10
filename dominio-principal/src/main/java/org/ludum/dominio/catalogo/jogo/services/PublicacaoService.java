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

    public ResultadoPublicacao publicarJogoComStrategy(ContaId devId, JogoId jogoId) {
        Objects.requireNonNull(devId, "ContaId não pode ser nulo");
        Objects.requireNonNull(jogoId, "JogoId não pode ser nulo");

        Conta conta = contaRepository.obterPorId(devId);
        if (conta == null) {
            return ResultadoPublicacao.erro("Conta não encontrada: " + devId);
        }

        if (conta.getTipo() != TipoConta.DESENVOLVEDORA) {
            return ResultadoPublicacao.erro(
                "Apenas contas de desenvolvedores podem publicar jogos. Tipo atual: " + conta.getTipo()
            );
        }

        if (conta.getStatus() != StatusConta.ATIVA) {
            return ResultadoPublicacao.erro(
                "Apenas contas ativas podem publicar jogos. Status atual: " + conta.getStatus()
            );
        }

        Jogo jogo = jogoRepository.obterPorId(jogoId);
        if (jogo == null) {
            return ResultadoPublicacao.erro("Jogo não encontrado: " + jogoId);
        }

        if (!jogo.getDesenvolvedoraId().equals(devId)) {
            return ResultadoPublicacao.erro(
                "Apenas o desenvolvedor dono pode publicar o jogo. " +
                "Jogo pertence a: " + jogo.getDesenvolvedoraId() +
                ", tentativa de: " + devId
            );
        }

        StatusPublicacao statusAnterior = jogo.getStatus();
        EstrategiaPublicacao estrategia = estrategias.get(statusAnterior);
        
        if (estrategia == null) {
            return ResultadoPublicacao.erro(
                "Não há estratégia de publicação definida para o status: " + statusAnterior
            );
        }
        
        List<String> erros = estrategia.validar(jogo);
        if (!erros.isEmpty()) {
            return ResultadoPublicacao.comErros(erros);
        }
        
        estrategia.executar(jogo);
        
        jogoRepository.salvar(jogo);
        
        String mensagem = obterMensagemSucesso(statusAnterior, jogo.getStatus());
        return ResultadoPublicacao.sucesso(mensagem);
    }
    
    public ResultadoPublicacao validarPublicacao(JogoId jogoId) {
        Objects.requireNonNull(jogoId, "JogoId não pode ser nulo");
        
        Jogo jogo = jogoRepository.obterPorId(jogoId);
        if (jogo == null) {
            return ResultadoPublicacao.erro("Jogo não encontrado: " + jogoId);
        }
        
        StatusPublicacao status = jogo.getStatus();
        EstrategiaPublicacao estrategia = estrategias.get(status);
        
        if (estrategia == null) {
            return ResultadoPublicacao.erro("Status inválido para validação: " + status);
        }
        
        List<String> erros = estrategia.validar(jogo);
        if (!erros.isEmpty()) {
            return ResultadoPublicacao.comErros(erros);
        }
        
        return ResultadoPublicacao.sucesso("Jogo válido para publicação");
    }
    
    private String obterMensagemSucesso(StatusPublicacao statusAnterior, StatusPublicacao statusNovo) {
        if (statusAnterior == StatusPublicacao.EM_UPLOAD && 
            statusNovo == StatusPublicacao.AGUARDANDO_VALIDACAO) {
            return "Jogo enviado para validação com sucesso";
        }
        
        if (statusAnterior == StatusPublicacao.AGUARDANDO_VALIDACAO && 
            statusNovo == StatusPublicacao.PUBLICADO) {
            return "Jogo publicado com sucesso na plataforma";
        }
        
        return "Operação realizada com sucesso";
    }

    public void publicarJogo(ContaId devId, JogoId jogoId) {
        ResultadoPublicacao resultado = publicarJogoComStrategy(devId, jogoId);
        
        if (resultado.isFalha()) {
            // Lança exceção para manter compatibilidade com código legado
            if (!resultado.getErros().isEmpty()) {
                throw new IllegalStateException(
                    "Não foi possível publicar o jogo: " + String.join(", ", resultado.getErros())
                );
            } else {
                throw new IllegalStateException(resultado.getMensagem());
            }
        }
        
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

        if (jogo.getStatus() != StatusPublicacao.AGUARDANDO_VALIDACAO) {
            throw new IllegalStateException(
                "Apenas jogos aguardando validação podem ser rejeitados. " +
                "Status atual: " + jogo.getStatus()
            );
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
                "Apenas o desenvolvedor dono pode arquivar o jogo"
            );
        }

        if (jogo.getStatus() == StatusPublicacao.ARQUIVADO) {
            throw new IllegalStateException(
                "Jogo já está arquivado"
            );
        }

        jogo.arquivar();
        jogoRepository.salvar(jogo);
    }
}
