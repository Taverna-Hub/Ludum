package org.ludum.catalogo.jogo.services;

import org.ludum.catalogo.jogo.entidades.Jogo;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.entidades.PacoteZip;
import org.ludum.catalogo.jogo.entidades.VersaoId;
import org.ludum.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.identidade.conta.repositories.ContaRepository;
import org.ludum.identidade.conta.entities.Conta;
import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.identidade.conta.enums.TipoConta;

public class GestaoDeJogosService {

    private final JogoRepository jogoRepository;
    private final ContaRepository contaRepository;

    public GestaoDeJogosService(JogoRepository jogoRepository, ContaRepository contaRepository) {
        this.jogoRepository = jogoRepository;
        this.contaRepository = contaRepository;
    }

    public void verificarMalware(PacoteZip pacoteZip, String str){
        // TODO: Implementação de API externa
        if(!str.equals("SEGURO")){
            throw new IllegalStateException("Malware detectado");
        }
    }

    public void processarUpload(ContaId contaId, JogoId jogoId, PacoteZip pacote, VersaoId versaoId, String nomeVersao, String descVersao){
        Conta currentUser = contaRepository.obterPorId(contaId);
        Jogo currentJogo = jogoRepository.obterPorId(jogoId);

        verificarMalware(pacote, "SEGURO");

        if(!currentUser.getTipo().equals(TipoConta.DESENVOLVEDORA)){
            throw new IllegalStateException("Funcionalidade disponível apenas para desenvolvedores");
        }

        if(!contaId.equals(currentJogo.getDesenvolvedoraId())){
            throw new IllegalStateException("Jogo não pertence a desenvolvedora");
        }

        currentJogo.adicionarVersao(pacote, versaoId, nomeVersao, descVersao);
        jogoRepository.salvar(currentJogo);

    }
}