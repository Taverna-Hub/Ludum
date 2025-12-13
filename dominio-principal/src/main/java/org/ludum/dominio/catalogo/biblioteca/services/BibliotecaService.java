package org.ludum.dominio.catalogo.biblioteca.services;

import org.ludum.dominio.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.dominio.catalogo.biblioteca.entidades.ItemBiblioteca;
import org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.dominio.catalogo.biblioteca.estruturas.IteratorBiblioteca;
import org.ludum.dominio.catalogo.biblioteca.repositorios.BibliotecaRepository;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.PacoteZip;
import org.ludum.dominio.catalogo.jogo.entidades.Versao;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.financeiro.transacao.enums.StatusTransacao;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.enums.StatusConta;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import java.time.LocalDate;

public class BibliotecaService {

    private final BibliotecaRepository bibliotecaRepository;
    private final TransacaoRepository transacaoRepository;
    private final JogoRepository jogoRepository;
    private final ContaRepository contaRepository;

    public BibliotecaService(BibliotecaRepository bibliotecaRepository, TransacaoRepository transacaoRepository,
            JogoRepository jogoRepository, ContaRepository contaRepository) {
        this.bibliotecaRepository = bibliotecaRepository;
        this.transacaoRepository = transacaoRepository;
        this.jogoRepository = jogoRepository;
        this.contaRepository = contaRepository;
    }

    public void adicionarJogo(ModeloDeAcesso modeloDeAcesso, JogoId jogoid, ContaId contaid, TransacaoId transacaoId) {
        Biblioteca currentBiblioteca = this.bibliotecaRepository.obterPorJogador(contaid);

        if (currentBiblioteca == null) {
            currentBiblioteca = new Biblioteca(contaid);
        }

        Transacao currentTransacao = null;
        if (transacaoId != null) {
            currentTransacao = transacaoRepository.obterPorId(transacaoId);
        }

        if (modeloDeAcesso.equals(ModeloDeAcesso.PAGO)) {
            if (currentTransacao == null || currentTransacao.getStatus() != StatusTransacao.CONFIRMADA) {
                throw new IllegalStateException("Transação cancelada, pendente ou inexistente");
            }
        }

        currentBiblioteca.adicionarJogo(modeloDeAcesso, jogoid);
        bibliotecaRepository.salvar(currentBiblioteca);

    }

    public boolean verificarPosse(ContaId contaId, JogoId jogoId) {
        Biblioteca biblioteca = bibliotecaRepository.obterPorJogador(contaId);
        if (biblioteca == null) {
            return false;
        }
        return biblioteca.buscarJogoEmBiblioteca(jogoId).isPresent();
    }

    public List<Jogo> obterJogosEmBiblioteca(ContaId contaId) {
        Biblioteca biblioteca = bibliotecaRepository.obterPorJogador(contaId);
        if (biblioteca == null) {
            return Collections.emptyList();
        }

        List<Jogo> jogos = new ArrayList<>();

        IteratorBiblioteca<ItemBiblioteca> iterator = biblioteca.criarIterator();
        while (iterator.existeProximo()) {
            ItemBiblioteca item = iterator.proximo();
            Jogo jogo = jogoRepository.obterPorId(item.getJogoId());
            if (jogo != null) {
                jogos.add(jogo);
            }
        }

        return jogos;
    }

    public PacoteZip processarDownload(ContaId contaId, JogoId jogoId) {
        Biblioteca currentBiblioteca = this.bibliotecaRepository.obterPorJogador(contaId);
        Jogo currentJogo = jogoRepository.obterPorId(jogoId);

        if (currentJogo == null) {
            throw new IllegalArgumentException("Jogo não encontrado com ID: " + jogoId);
        }

        if (currentJogo.getVersaoHistory() == null || currentJogo.getVersaoHistory().isEmpty()) {
            throw new IllegalStateException("O jogo não possui versões publicadas para download.");
        }

        Versao currentVersao = currentJogo.getVersaoHistory().getLast();

        ItemBiblioteca currentItemBiblioteca = currentBiblioteca.buscarJogoEmBiblioteca(jogoId).orElse(null);

        Conta currentUser = contaRepository.obterPorId(contaId);

        if (currentUser.getStatus().equals(StatusConta.INATIVA)) {
            throw new IllegalStateException("Usuário com conta inativa");
        }

        if (currentJogo.getDataDeLancamento().isAfter(LocalDate.now())) {
            throw new IllegalStateException("Jogo ainda não lançado");
        }

        if (currentItemBiblioteca == null) {
            throw new IllegalStateException("Jogo não está presente na biblioteca");
        }

        currentBiblioteca.baixouJogo(currentItemBiblioteca);
        return currentVersao.getPacoteZip();

    }

}
