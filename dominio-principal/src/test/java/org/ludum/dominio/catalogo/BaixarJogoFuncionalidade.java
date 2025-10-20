package org.ludum.dominio.catalogo;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.ludum.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.catalogo.biblioteca.entidades.ItemBiblioteca;
import org.ludum.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.catalogo.biblioteca.repositorios.BibliotecaRepository;
import org.ludum.catalogo.biblioteca.services.BibliotecaService;

import io.cucumber.java.Before;
import org.ludum.catalogo.jogo.entidades.*;
import org.ludum.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.catalogo.tag.entidades.Tag;
import org.ludum.catalogo.tag.entidades.TagId;
import org.ludum.financeiro.transacao.TransacaoRepository;
import org.ludum.financeiro.transacao.entidades.Transacao;
import org.ludum.financeiro.transacao.entidades.TransacaoId;
import org.ludum.financeiro.transacao.enums.StatusTransacao;
import org.ludum.financeiro.transacao.enums.TipoTransacao;
import org.ludum.identidade.conta.entities.Conta;
import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.identidade.conta.enums.StatusConta;
import org.ludum.identidade.conta.enums.TipoConta;
import org.ludum.identidade.conta.repositories.ContaRepository;


import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.*;

public class BaixarJogoFuncionalidade {

    private BibliotecaService bibliotecaService;

    private BibliotecaRepository bibliotecaRepository;
    private TransacaoRepository transacaoRepository;
    private JogoRepository jogoRepository;
    private ContaRepository contaRepository;

    private Biblioteca biblioteca;
    private ContaId contaId;
    private Conta contaJogador;
    private Transacao transacao;
    private Jogo jogo;
    private Jogo currentJogo;
    private boolean downloadFinalizado;

    private Exception e;

    @Before
    public void setup() {

        this.bibliotecaRepository = mock(BibliotecaRepository.class);
        this.transacaoRepository = mock(TransacaoRepository.class);
        this.jogoRepository = mock(JogoRepository.class);
        this.contaRepository = mock(ContaRepository.class);

        this.bibliotecaService = new BibliotecaService(this.bibliotecaRepository, this.transacaoRepository, this.jogoRepository, this.contaRepository);

        this.downloadFinalizado = false;

        this.contaId = new ContaId(UUID.randomUUID().toString());

        this.contaJogador = new Conta(this.contaId, "abc", "123", TipoConta.JOGADOR, StatusConta.ATIVA);

        this.biblioteca = new Biblioteca(this.contaId);
        this.transacao = new Transacao(new TransacaoId(UUID.randomUUID().toString()), this.contaId, new ContaId(UUID.randomUUID().toString()), TipoTransacao.PIX, StatusTransacao.CONFIRMADA, LocalDateTime.now(), BigDecimal.valueOf(10000));
    }

    @Given("que eu sou um usuário com uma conta no status {string}")
    public void queEuSouUmUsuarioComUmaContaNoStatus(String str) {
        this.contaJogador = new Conta(this.contaId, "abc", "123", TipoConta.JOGADOR, StatusConta.valueOf(str));

    }

    @And("existe um jogo chamado {string} que é gratuito, já foi lançado e se encontra na minha biblioteca")
    public void existeUmJogoGratuitoJaLancado(String str){
        try{
            this.jogo = new Jogo(new JogoId(UUID.randomUUID().toString()), new ContaId(UUID.randomUUID().toString()), new Slug(str).toString(), "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")), false, LocalDate.of(2021, 3, 15));
            this.jogo.adicionarVersao(new PacoteZip(new byte[10]), new VersaoId("1"), "jogo_1.0.0.zip", "aaaa");
            when(jogoRepository.obterPorId(this.jogo.getId())).thenReturn(this.jogo);
            when(transacaoRepository.obterPorId(this.transacao.getTransacaoId())).thenReturn(this.transacao);
            when(bibliotecaRepository.obterPorJogador(this.contaJogador.getId())).thenReturn(this.biblioteca);
            when(contaRepository.obterPorId(this.contaJogador.getId())).thenReturn(this.contaJogador);
            this.bibliotecaService.adicionarJogo(ModeloDeAcesso.GRATUITO, this.jogo.getId(), this.contaJogador.getId(), this.transacao.getTransacaoId());
        }catch(IllegalStateException | MalformedURLException e){
            e.printStackTrace();
        }
    }

    @When("eu seleciono a opção para baixar {string}")
    public void  euSelecionoParaBaixar(String str){
        Slug currentSlug = new Slug(str);
        when(jogoRepository.obterPorSlug(currentSlug)).thenReturn(this.jogo);
        this.currentJogo = this.jogoRepository.obterPorSlug(currentSlug);
    }

    @Then("o download do jogo deve iniciar")
    public void oDownloadDoJogoDeveIniciar(){
        try{
            bibliotecaService.processarDownload(contaJogador.getId(), this.currentJogo.getId());
            this.downloadFinalizado = true;
        }catch(IllegalStateException e){
            this.downloadFinalizado = false;
            e.printStackTrace();
        }
    }

    @And("após a conclusão, {string} deve aparecer na minha lista de jogos baixados")
    public void aposAConclusao(String str){
        if(downloadFinalizado){
            Slug currentSlug = new Slug(str);
            when(jogoRepository.obterPorSlug(currentSlug)).thenReturn(this.jogo);
            ItemBiblioteca currentItemBiblioteca = new ItemBiblioteca(ModeloDeAcesso.GRATUITO, this.jogoRepository.obterPorSlug(currentSlug).getId());
            Assertions.assertTrue(this.biblioteca.getItensBaixados().contains(currentItemBiblioteca));
        }
    }

    @Then("o sistema deve bloquear o download")
    public void  oSistemaDeveBloquear(){
        try{
            bibliotecaService.processarDownload(contaJogador.getId(), this.currentJogo.getId());
            this.downloadFinalizado = true;
        }catch(IllegalStateException e){
            this.downloadFinalizado = false;
            this.e = e;
        }
    }

    @And("deve exibir a mensagem de erro adequada")
    public void deveExibirUmaMensagem(){
        if(!this.downloadFinalizado){
            e.printStackTrace();
        }
    }

    @And("existe um jogo chamado {string} que é gratuito, mas sua data de lançamento é futura")
    public void existeUmaJogoChamado(String str){
        try{
            this.jogo = new Jogo(new JogoId(UUID.randomUUID().toString()), new ContaId(UUID.randomUUID().toString()), new Slug(str).toString(), "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")), false, LocalDate.of(2029, 3, 15));
            this.jogo.adicionarVersao(new PacoteZip(new byte[10]), new VersaoId("1"), "jogo_1.0.0.zip", "aaaa");
            when(jogoRepository.obterPorId(this.jogo.getId())).thenReturn(this.jogo);
            when(transacaoRepository.obterPorId(this.transacao.getTransacaoId())).thenReturn(this.transacao);
            when(bibliotecaRepository.obterPorJogador(this.contaJogador.getId())).thenReturn(this.biblioteca);
            when(contaRepository.obterPorId(this.contaJogador.getId())).thenReturn(this.contaJogador);
            this.bibliotecaService.adicionarJogo(ModeloDeAcesso.GRATUITO, this.jogo.getId(), this.contaJogador.getId(), this.transacao.getTransacaoId());
        }catch(IllegalStateException | MalformedURLException e){
            this.e = e;
        }
    }

    @And("existe um jogo chamado {string} que é pago, já foi lançado e se encontra na minha biblioteca")
    public void existeUmJogoChamado(String str){
        try{
            this.jogo = new Jogo(new JogoId(UUID.randomUUID().toString()), new ContaId(UUID.randomUUID().toString()), new Slug(str).toString(), "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")), false, LocalDate.of(2021, 3, 15));
            this.jogo.adicionarVersao(new PacoteZip(new byte[10]), new VersaoId("1"), "jogo_1.0.0.zip", "aaaa");
            when(jogoRepository.obterPorId(this.jogo.getId())).thenReturn(this.jogo);
            when(transacaoRepository.obterPorId(this.transacao.getTransacaoId())).thenReturn(this.transacao);
            when(bibliotecaRepository.obterPorJogador(this.contaJogador.getId())).thenReturn(this.biblioteca);
            when(contaRepository.obterPorId(this.contaJogador.getId())).thenReturn(this.contaJogador);
            this.bibliotecaService.adicionarJogo(ModeloDeAcesso.GRATUITO, this.jogo.getId(), this.contaJogador.getId(), this.transacao.getTransacaoId());
        }catch(IllegalStateException | MalformedURLException e){
            e.printStackTrace();
        }
    }

    @Given("que eu possuo o jogo {string} na minha biblioteca")
    public void queEuPossuoOJogoNaMinhaBiblioteca(String str){
        try{
            this.jogo = new Jogo(new JogoId(UUID.randomUUID().toString()), new ContaId(UUID.randomUUID().toString()), str, "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")), false, LocalDate.of(2021, 3, 15));
            this.jogo.adicionarVersao(new PacoteZip(new byte[10]), new VersaoId("1"), "jogo_1.0.0.zip", "aaaa");
            when(jogoRepository.obterPorId(this.jogo.getId())).thenReturn(this.jogo);
            when(transacaoRepository.obterPorId(this.transacao.getTransacaoId())).thenReturn(this.transacao);
            when(bibliotecaRepository.obterPorJogador(this.contaJogador.getId())).thenReturn(this.biblioteca);
            when(contaRepository.obterPorId(this.contaJogador.getId())).thenReturn(this.contaJogador);

        }catch(IllegalStateException | MalformedURLException e){
            e.printStackTrace();
        }

        ItemBiblioteca newItem = new ItemBiblioteca(ModeloDeAcesso.GRATUITO, this.jogo.getId());
        this.biblioteca.adicionarJogo(newItem.getModeloDeAcesso(), newItem.getJogoId());

        if(!biblioteca.getItens().contains(newItem)){
            throw new IllegalStateException("Biblioteca não contem o item");
        }
    }

    @And("{string} já consta na minha lista de jogos baixados")
    public void jaConstaNaMinhaListaDeJogosBaixados(String str){
        Slug currentSlug = new Slug(str);

        when(jogoRepository.obterPorSlug(currentSlug)).thenReturn(this.jogo);
        Optional<ItemBiblioteca> currentItemBiblioteca = biblioteca.buscarJogoEmBiblioteca(this.jogoRepository.obterPorSlug(currentSlug).getId());

        if(currentItemBiblioteca.isPresent()){
            biblioteca.baixouJogo(currentItemBiblioteca.get());
            Assertions.assertTrue(this.biblioteca.getItensBaixados().contains(currentItemBiblioteca.get()));
        }

    }

    @Then("um novo download do jogo {string} deve ser iniciado com sucesso")
    public void umNovoDownloadDoJogo(String str){
        try{
            bibliotecaService.processarDownload(contaJogador.getId(), this.currentJogo.getId());
            this.downloadFinalizado = true;
        }catch(IllegalStateException e){
            this.downloadFinalizado = false;
            e.printStackTrace();
        }
    }

    @And("o sistema não deve me impedir de realizar um novo download e deve registrar o novo download na minha lista de jogos baixados")
    public void oSistemaNaoDeveMeImpedir(){
        if(!this.downloadFinalizado){
            throw new IllegalStateException("Sistema impediu o novo download");
        }
    }



}