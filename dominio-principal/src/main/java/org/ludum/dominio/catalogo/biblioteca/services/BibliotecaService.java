package org.ludum.dominio.catalogo.biblioteca.services;

import org.ludum.dominio.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.dominio.catalogo.biblioteca.entidades.ItemBiblioteca;
import org.ludum.dominio.catalogo.biblioteca.estruturas.IteratorBiblioteca;
import org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.PacoteZip;
import org.ludum.dominio.catalogo.biblioteca.repositorios.BibliotecaRepository;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.Versao;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.financeiro.transacao.enums.StatusTransacao;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.enums.StatusConta;

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
        Transacao currentTransacao = transacaoRepository.obterPorId(transacaoId);

        if (modeloDeAcesso.equals(ModeloDeAcesso.PAGO)) {
            if (currentTransacao.getStatus() != StatusTransacao.CONFIRMADA) {
                throw new IllegalStateException("Transação cancelada ou pendente");
            }
        }

        currentBiblioteca.adicionarJogo(modeloDeAcesso, jogoid);
        bibliotecaRepository.salvar(currentBiblioteca);

    }

    public PacoteZip processarDownload(ContaId contaId, JogoId jogoId) {
        Biblioteca currentBiblioteca = this.bibliotecaRepository.obterPorJogador(contaId);
        Jogo currentJogo = jogoRepository.obterPorId(jogoId);
        Versao currentVersao = this.jogoRepository.obterPorId(jogoId).getVersaoHistory().getLast();

        ItemBiblioteca currentItemBiblioteca = null;
        IteratorBiblioteca<ItemBiblioteca> iterator = currentBiblioteca.criarIterator();
        while (iterator.existeProximo()) {
            ItemBiblioteca item = iterator.proximo();
            if (item.getJogoId().equals(jogoId)) {
                currentItemBiblioteca = item;
                break;
            }
        }

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
