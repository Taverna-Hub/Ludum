package org.ludum.dominio.catalogo;

import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.ludum.dominio.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.dominio.catalogo.biblioteca.entidades.ItemBiblioteca;
import org.ludum.dominio.catalogo.biblioteca.estruturas.IteratorBiblioteca;
import org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.dominio.catalogo.biblioteca.repositorios.BibliotecaRepository;
import org.ludum.dominio.catalogo.biblioteca.services.BibliotecaService;

import io.cucumber.java.Before;
import org.ludum.dominio.catalogo.jogo.entidades.*;
import org.ludum.dominio.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.financeiro.transacao.TransacaoRepository;
import org.ludum.dominio.financeiro.transacao.entidades.Recibo;
import org.ludum.dominio.financeiro.transacao.entidades.Transacao;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.financeiro.transacao.enums.StatusTransacao;
import org.ludum.dominio.financeiro.transacao.enums.TipoTransacao;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.enums.StatusConta;
import org.ludum.dominio.identidade.conta.enums.TipoConta;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class BaixarJogoFuncionalidade {

    private BibliotecaService bibliotecaService;

    private MockBibliotecaRepository bibliotecaRepository;
    private MockTransacaoRepository transacaoRepository;
    private MockJogoRepository jogoRepository;
    private MockContaRepository contaRepository;

    private Biblioteca biblioteca;
    private ContaId contaId;
    private TransacaoId transacaoId;
    private JogoId jogoId;
    private Jogo currentJogo;
    private Conta contaJogador;
    private boolean downloadFinalizado;
    private ModeloDeAcesso modeloDeAcesso;

    private Exception e;

    public static class MockBibliotecaRepository implements BibliotecaRepository {
        List<Biblioteca> bibliotecas;

        public MockBibliotecaRepository() {
            this.bibliotecas = new ArrayList<>();
        }

        @Override
        public Biblioteca obterPorJogador(ContaId contaId) {
            for (int i = 0; i < bibliotecas.size(); i++) {
                Biblioteca currentBiblioteca = this.bibliotecas.get(i);
                if (currentBiblioteca.getContaId().equals(contaId)) {
                    return currentBiblioteca;
                }
            }
            return null;
        }

        @Override
        public void salvar(Biblioteca biblioteca) {
            this.bibliotecas.add(biblioteca);
        }
    }

    private static class MockTransacaoRepository implements TransacaoRepository {
        private List<Transacao> transacoes = new ArrayList<>();
        private List<Recibo> recibos = new ArrayList<>();

        @Override
        public Transacao obterPorId(TransacaoId id) {
            return transacoes.stream().filter(t -> t.getTransacaoId().equals(id)).findFirst().orElse(null);
        }

        @Override
        public void salvarRecibo(Recibo recibo) {
            recibos.add(recibo);
        }

        @Override
        public void salvar(Transacao transacao) {
            transacoes.add(transacao);
        }

        public List<Transacao> getTransacoes() {
            return new ArrayList<>(transacoes);
        }

        public List<Recibo> getRecibos() {
            return new ArrayList<>(recibos);
        }
    }

    private static class MockJogoRepository implements JogoRepository {
        private final List<Jogo> jogos;

        public MockJogoRepository() {
            this.jogos = new ArrayList<>();
        }

        @Override
        public void salvar(Jogo jogo) {
            jogos.removeIf(j -> j.getId().equals(jogo.getId()));
            jogos.add(jogo);
        }

        @Override
        public Jogo obterPorId(JogoId id) {
            return jogos.stream()
                    .filter(j -> j.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public Jogo obterPorSlug(Slug slug) {
            return jogos.stream()
                    .filter(j -> j.getSlug().equals(slug))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public boolean existeSlugParaDesenvolvedora(ContaId devId, Slug slug) {
            // Verifica se existe jogo PUBLICADO com a mesma slug para este desenvolvedor
            // Ignora jogos com status AGUARDANDO_VALIDACAO (o jogo atual antes de publicar)
            return jogos.stream()
                    .filter(j -> j.getDesenvolvedoraId().equals(devId))
                    .filter(j -> j.getSlug().equals(slug))
                    .filter(j -> j.getStatus() == StatusPublicacao.PUBLICADO)
                    .findAny()
                    .isPresent();
        }

        @Override
        public List<Jogo> obterJogosPorTag(TagId tagId) {
            return jogos.stream()
                    .filter(j -> j.getTags().stream()
                            .anyMatch(t -> t.getId().equals(tagId)))
                    .toList();
        }
    }

    private static class MockContaRepository implements ContaRepository {
        public List<Conta> contas = new ArrayList<>();

        @Override
        public void salvar(Conta conta) {
            contas.add(conta);
        }

        @Override
        public Conta obterPorId(ContaId id) {
            return contas.stream()
                    .filter(c -> c.getId().getValue().equals(id.getValue()))
                    .findFirst()
                    .orElse(null);
        }

        public void adicionarConta(Conta conta) {
            contas.add(conta);
        }
    }

    @Before
    public void setup() {

        this.bibliotecaRepository = new MockBibliotecaRepository();
        this.transacaoRepository = new MockTransacaoRepository();
        this.jogoRepository = new MockJogoRepository();
        this.contaRepository = new MockContaRepository();

        this.bibliotecaService = new BibliotecaService(this.bibliotecaRepository, this.transacaoRepository,
                this.jogoRepository, this.contaRepository);

        this.downloadFinalizado = false;

        this.contaId = new ContaId(UUID.randomUUID().toString());
        this.transacaoId = new TransacaoId(UUID.randomUUID().toString());
        this.jogoId = new JogoId(UUID.randomUUID().toString());

        this.transacaoRepository
                .salvar(new Transacao(this.transacaoId, this.contaId, new ContaId(UUID.randomUUID().toString()),
                        TipoTransacao.PIX, StatusTransacao.CONFIRMADA, LocalDateTime.now(), BigDecimal.valueOf(10000)));
    }

    @Given("que eu sou um usuário com uma conta no status {string}")
    public void queEuSouUmUsuarioComUmaContaNoStatus(String str) {
        this.contaJogador = new Conta(this.contaId, "abc", "123", TipoConta.JOGADOR, StatusConta.valueOf(str));
        this.contaRepository.salvar(contaJogador);

        this.biblioteca = new Biblioteca(this.contaId);
        this.bibliotecaRepository.salvar(this.biblioteca);

    }

    @And("existe um jogo chamado {string} que é gratuito, já foi lançado e se encontra na minha biblioteca")
    public void existeUmJogoGratuitoJaLancado(String str) {
        try {
            Jogo newJogo = new Jogo(jogoId, new ContaId(UUID.randomUUID().toString()), new Slug(str).toString(),
                    "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")),
                    false, LocalDate.of(2021, 3, 15));
            newJogo.adicionarVersao(new PacoteZip(new byte[10]), new VersaoId("1"), "jogo_1.0.0.zip", "aaaa");
            jogoRepository.salvar(newJogo);
            this.modeloDeAcesso = ModeloDeAcesso.GRATUITO;
            this.bibliotecaService.adicionarJogo(this.modeloDeAcesso, this.jogoId, this.contaId, this.transacaoId);
        } catch (IllegalStateException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @When("eu seleciono a opção para baixar {string}")
    public void euSelecionoParaBaixar(String str) {
        Slug currentSlug = new Slug(str);
        this.currentJogo = this.jogoRepository.obterPorSlug(currentSlug);
    }

    @Then("o download do jogo deve iniciar")
    public void oDownloadDoJogoDeveIniciar() {
        try {
            bibliotecaService.processarDownload(this.contaId, this.currentJogo.getId());
            this.downloadFinalizado = true;
        } catch (IllegalStateException e) {
            this.downloadFinalizado = false;
            e.printStackTrace();
        }
    }

    @And("após a conclusão, {string} deve aparecer na minha lista de jogos baixados")
    public void aposAConclusao(String str) {
        if (downloadFinalizado) {
            Slug currentSlug = new Slug(str);
            ItemBiblioteca currentItemBiblioteca = new ItemBiblioteca(this.modeloDeAcesso,
                    this.jogoRepository.obterPorSlug(currentSlug).getId());
            Assertions.assertTrue(this.biblioteca.getItensBaixados().contains(currentItemBiblioteca));
        }
    }

    @Then("o sistema deve bloquear o download")
    public void oSistemaDeveBloquear() {
        try {
            this.bibliotecaService.processarDownload(contaId, this.currentJogo.getId());
            this.downloadFinalizado = true;
        } catch (IllegalStateException e) {
            this.downloadFinalizado = false;
            this.e = e;
        }
    }

    @And("deve exibir a mensagem de erro adequada")
    public void deveExibirUmaMensagem() {
        Assertions.assertNotNull(this.e, "Deveria ter sido lançada uma exceção contendo a mensagem de erro.");
    }

    @And("existe um jogo chamado {string} que é gratuito, mas sua data de lançamento é futura")
    public void existeUmaJogoChamado(String str) {
        try {
            Jogo newJogo = new Jogo(this.jogoId, new ContaId(UUID.randomUUID().toString()), new Slug(str).toString(),
                    "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")),
                    false, LocalDate.of(2029, 3, 15));
            newJogo.adicionarVersao(new PacoteZip(new byte[10]), new VersaoId("1"), "jogo_1.0.0.zip", "aaaa");
            this.jogoRepository.salvar(newJogo);

            this.modeloDeAcesso = ModeloDeAcesso.GRATUITO;
            this.bibliotecaService.adicionarJogo(this.modeloDeAcesso, this.jogoId, this.contaJogador.getId(),
                    this.transacaoId);
        } catch (IllegalStateException | MalformedURLException e) {
            this.e = e;
        }
    }

    @And("existe um jogo chamado {string} que é pago, já foi lançado e se encontra na minha biblioteca")
    public void existeUmJogoChamado(String str) {
        try {
            Jogo newJogo = new Jogo(this.jogoId, new ContaId(UUID.randomUUID().toString()), new Slug(str).toString(),
                    "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")),
                    false, LocalDate.of(2021, 3, 15));
            newJogo.adicionarVersao(new PacoteZip(new byte[10]), new VersaoId("1"), "jogo_1.0.0.zip", "aaaa");
            this.jogoRepository.salvar(newJogo);

            this.modeloDeAcesso = ModeloDeAcesso.PAGO;
            this.bibliotecaService.adicionarJogo(this.modeloDeAcesso, this.jogoId, this.contaJogador.getId(),
                    transacaoId);
        } catch (IllegalStateException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Given("que eu possuo o jogo {string} na minha biblioteca")
    public void queEuPossuoOJogoNaMinhaBiblioteca(String str) {
        try {
            this.contaJogador = new Conta(this.contaId, "abc", "123", TipoConta.JOGADOR, StatusConta.ATIVA);
            this.contaRepository.salvar(contaJogador);

            this.biblioteca = new Biblioteca(this.contaId);
            this.bibliotecaRepository.salvar(this.biblioteca);

            Jogo newJogo = new Jogo(this.jogoId, new ContaId(UUID.randomUUID().toString()), str, "JogoJogoJogo",
                    new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")), false,
                    LocalDate.of(2021, 3, 15));
            newJogo.adicionarVersao(new PacoteZip(new byte[10]), new VersaoId("1"), "jogo_1.0.0.zip", "aaaa");
            this.jogoRepository.salvar(newJogo);

            this.modeloDeAcesso = ModeloDeAcesso.GRATUITO;
            this.biblioteca.adicionarJogo(this.modeloDeAcesso, this.jogoId);

            boolean encontrado = false;
            IteratorBiblioteca<ItemBiblioteca> iterator = biblioteca.criarIterator();
            ItemBiblioteca itemProcurado = new ItemBiblioteca(this.modeloDeAcesso, this.jogoId);

            while (iterator.existeProximo()) {
                if (iterator.proximo().equals(itemProcurado)) {
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                throw new IllegalStateException("Biblioteca não contem o item");
            }
        } catch (IllegalStateException | MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @And("{string} já consta na minha lista de jogos baixados")
    public void jaConstaNaMinhaListaDeJogosBaixados(String str) {
        Slug currentSlug = new Slug(str);

        Optional<ItemBiblioteca> currentItemBiblioteca = biblioteca
                .buscarJogoEmBiblioteca(this.jogoRepository.obterPorSlug(currentSlug).getId());

        if (currentItemBiblioteca.isPresent()) {
            biblioteca.baixouJogo(currentItemBiblioteca.get());
            Assertions.assertTrue(this.biblioteca.getItensBaixados().contains(currentItemBiblioteca.get()));
        }

    }

    @Then("um novo download do jogo {string} deve ser iniciado com sucesso")
    public void umNovoDownloadDoJogo(String str) {
        try {
            bibliotecaService.processarDownload(this.contaId, this.currentJogo.getId());
            this.downloadFinalizado = true;
        } catch (IllegalStateException e) {
            this.downloadFinalizado = false;
            e.printStackTrace();
        }
    }

    @And("o sistema não deve me impedir de realizar um novo download e deve registrar o novo download na minha lista de jogos baixados")
    public void oSistemaNaoDeveMeImpedir() {
        if (!this.downloadFinalizado) {
            throw new IllegalStateException("Sistema impediu o novo download");
        }
    }

}