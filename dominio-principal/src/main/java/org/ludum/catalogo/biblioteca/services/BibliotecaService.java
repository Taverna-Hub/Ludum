package org.ludum.catalogo.biblioteca.services;

import org.ludum.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.catalogo.biblioteca.entidades.ItemBiblioteca;
import org.ludum.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.catalogo.jogo.entidades.Jogo;
import org.ludum.catalogo.jogo.entidades.PacoteZip;
import org.ludum.catalogo.biblioteca.repositorios.BibliotecaRepository;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.entidades.Versao;
import org.ludum.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.financeiro.transacao.TransacaoRepository;
import org.ludum.financeiro.transacao.entidades.Transacao;
import org.ludum.financeiro.transacao.entidades.TransacaoId;
import org.ludum.financeiro.transacao.enums.StatusTransacao;
import org.ludum.identidade.conta.repositories.ContaRepository;
import org.ludum.identidade.conta.entities.Conta;
import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.identidade.conta.enums.StatusConta;

import java.time.LocalDate;

public class BibliotecaService {

    private final BibliotecaRepository bibliotecaRepository;
    private final TransacaoRepository transacaoRepository;
    private final JogoRepository jogoRepository;
    private final ContaRepository contaRepository;

    public BibliotecaService(BibliotecaRepository bibliotecaRepository,  TransacaoRepository transacaoRepository,  JogoRepository jogoRepository,   ContaRepository contaRepository) {
        this.bibliotecaRepository = bibliotecaRepository;
        this.transacaoRepository = transacaoRepository;
        this.jogoRepository = jogoRepository;
        this.contaRepository = contaRepository;
    }

    public void adicionarJogo(ModeloDeAcesso modeloDeAcesso, JogoId jogoid, ContaId contaid, TransacaoId transacaoId){
        Biblioteca currentBiblioteca = this.bibliotecaRepository.obterPorJogador(contaid);
        Jogo currentJogo = jogoRepository.obterPorId(jogoid);
        Transacao currentTransacao = transacaoRepository.obterPorId(transacaoId);

        if(currentJogo.getDataDeLancamento().isAfter(LocalDate.now())){
            throw new IllegalStateException("Jogo ainda não lançado");
        }

        if(currentTransacao.getStatus() != StatusTransacao.CONFIRMADA){
            throw new IllegalStateException("Transação cancelada ou pendente");
        }

        currentBiblioteca.adicionarJogo(modeloDeAcesso, jogoid);
        bibliotecaRepository.salvar(currentBiblioteca);

    }

    public PacoteZip processarDownload(ContaId contaId, JogoId jogoId){
        Biblioteca currentBiblioteca = this.bibliotecaRepository.obterPorJogador(contaId);
        Versao currentVersao = this.jogoRepository.obterPorId(jogoId).getVersaoHistory().getLast();
        ItemBiblioteca currentJogo = currentBiblioteca.buscarJogoEmBiblioteca(jogoId).orElse(null);
        Conta currentUser = contaRepository.obterPorId(contaId);

        if(currentUser.getStatus().equals(StatusConta.INATIVA)){
            throw new IllegalStateException("Conta inativa");
        }

        if(currentJogo == null){
            throw new IllegalStateException("Jogo não está presente na biblioteca");
        }

        return currentVersao.getPacoteZip();

    }

}
