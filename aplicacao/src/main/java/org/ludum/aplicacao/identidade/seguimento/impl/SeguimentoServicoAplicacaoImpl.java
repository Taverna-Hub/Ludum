package org.ludum.aplicacao.identidade.seguimento.impl;

import org.ludum.aplicacao.identidade.seguimento.SeguimentoRepositorioConsulta;
import org.ludum.aplicacao.identidade.seguimento.SeguimentoResumo;
import org.ludum.aplicacao.identidade.seguimento.SeguimentoServicoAplicacao;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.tag.TagRepository;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;
import org.ludum.dominio.identidade.seguimento.entities.AlvoId;
import org.ludum.dominio.identidade.seguimento.entities.Seguimento;
import org.ludum.dominio.identidade.seguimento.enums.TipoAlvo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação do serviço de aplicação para seguimentos.
 */
@Service
public class SeguimentoServicoAplicacaoImpl implements SeguimentoServicoAplicacao {

    private final SeguimentoRepositorioConsulta seguimentoRepositorioConsulta;
    private final ContaRepository contaRepository;
    private final JogoRepository jogoRepository;
    private final TagRepository tagRepository;

    public SeguimentoServicoAplicacaoImpl(
            SeguimentoRepositorioConsulta seguimentoRepositorioConsulta,
            ContaRepository contaRepository,
            JogoRepository jogoRepository,
            TagRepository tagRepository) {
        this.seguimentoRepositorioConsulta = seguimentoRepositorioConsulta;
        this.contaRepository = contaRepository;
        this.jogoRepository = jogoRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public List<SeguimentoResumo> listarSeguindo(String seguidorId) {
        ContaId contaId = new ContaId(seguidorId);
        List<Seguimento> seguimentos = seguimentoRepositorioConsulta.listarSeguindo(contaId);
        
        return seguimentos.stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeguimentoResumo> listarSeguidores(String alvoId) {
        AlvoId aId = new AlvoId(alvoId);
        List<Seguimento> seguimentos = seguimentoRepositorioConsulta.listarSeguidores(aId);
        
        return seguimentos.stream()
                .map(this::converterParaResumo)
                .collect(Collectors.toList());
    }

    @Override
    public boolean estaSeguindo(String seguidorId, String alvoId) {
        return seguimentoRepositorioConsulta.estaSeguindo(
                new ContaId(seguidorId), 
                new AlvoId(alvoId)
        );
    }

    @Override
    public long contarSeguidores(String alvoId) {
        return seguimentoRepositorioConsulta.contarSeguidores(new AlvoId(alvoId));
    }

    @Override
    public long contarSeguindo(String seguidorId) {
        return seguimentoRepositorioConsulta.contarSeguindo(new ContaId(seguidorId));
    }

    private SeguimentoResumo converterParaResumo(Seguimento seguimento) {
        String alvoNome = obterNomeAlvo(seguimento.getSeguidoId(), seguimento.getTipoAlvo());
        
        return SeguimentoResumo.fromSeguimento(seguimento, alvoNome);
    }

    private String obterNomeAlvo(AlvoId alvoId, TipoAlvo tipoAlvo) {
        try {
            switch (tipoAlvo) {
                case CONTA:
                case DESENVOLVEDORA:
                    Conta conta = contaRepository.obterPorId(new ContaId(alvoId.getValue()));
                    return conta != null ? conta.getNome() : "Usuário Desconhecido";
                case JOGO:
                    Jogo jogo = jogoRepository.obterPorId(new JogoId(alvoId.getValue()));
                    return jogo != null ? jogo.getTitulo() : "Jogo Desconhecido";
                case TAG:
                    Tag tag = tagRepository.obterPorId(new TagId(alvoId.getValue()));
                    return tag != null ? tag.getNome() : "Tag Desconhecida";
                default:
                    return "Desconhecido";
            }
        } catch (Exception e) {
            return "Desconhecido";
        }
    }
}
