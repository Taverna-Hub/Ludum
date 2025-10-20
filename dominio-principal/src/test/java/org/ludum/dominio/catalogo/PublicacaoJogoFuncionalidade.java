package org.ludum.dominio.catalogo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.ludum.catalogo.jogo.entidades.Jogo;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.entidades.Slug;
import org.ludum.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.catalogo.jogo.services.PublicacaoService;
import org.ludum.catalogo.tag.entidades.Tag;
import org.ludum.catalogo.tag.entidades.TagId;
import org.ludum.identidade.conta.entities.Conta;
import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.identidade.conta.enums.StatusConta;
import org.ludum.identidade.conta.enums.TipoConta;
import org.ludum.identidade.conta.repositories.ContaRepository;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PublicacaoJogoFuncionalidade {

    private ContaId desenvolvedorId;
    private ContaId outroDesenvolvedorId;
    private ContaId jogadorId;
    private Jogo jogoAtual;
    private PublicacaoService publicacaoService;
    private boolean operacaoSucesso;
    private String mensagemErro;
    private String tituloAtual;
    private String descricaoAtual;
    private URL capaOficial;
    private List<URL> screenshots;
    private List<URL> videos;
    private List<Tag> tags;
    private boolean isNSFW;
    private TipoConta tipoContaAtual;
    private StatusConta statusContaAtual;

    // Mock do repositório de jogos
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
                    .filter(j -> j.getStatus() == StatusPublicacao.PUBLICADO)
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

    // Mock do repositório de contas
    private static class MockContaRepository implements ContaRepository {
        private final List<Conta> contas;

        public MockContaRepository() {
            this.contas = new ArrayList<>();
        }

        @Override
        public void salvar(Conta conta) {
            contas.removeIf(c -> c.getId().equals(conta.getId()));
            contas.add(conta);
        }

        @Override
        public Conta obterPorId(ContaId id) {
            return contas.stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }
    }

    private MockJogoRepository mockJogoRepo;
    private MockContaRepository mockContaRepo;

    @Before
    public void setup() {
        // Criar novos repositórios mockados para cada cenário
        this.mockJogoRepo = new MockJogoRepository();
        this.mockContaRepo = new MockContaRepository();

        this.publicacaoService = new PublicacaoService(mockJogoRepo, mockContaRepo);

        // Reset de todos os campos
        this.desenvolvedorId = new ContaId("dev-123");
        this.outroDesenvolvedorId = new ContaId("dev-456");
        this.jogadorId = new ContaId("jogador-789");
        this.jogoAtual = null;
        this.operacaoSucesso = false;
        this.mensagemErro = null;
        this.tituloAtual = null;
        this.descricaoAtual = null;
        this.capaOficial = null;
        this.screenshots = new ArrayList<>();
        this.videos = new ArrayList<>();
        this.tags = new ArrayList<>();
        this.isNSFW = false;
        this.tipoContaAtual = TipoConta.DESENVOLVEDORA;
        this.statusContaAtual = StatusConta.ATIVA;

        // Criar contas padrão
        mockContaRepo.salvar(new Conta(desenvolvedorId, "Dev Principal", "hash",
                TipoConta.DESENVOLVEDORA, StatusConta.ATIVA));
        mockContaRepo.salvar(new Conta(outroDesenvolvedorId, "Outro Dev", "hash",
                TipoConta.DESENVOLVEDORA, StatusConta.ATIVA));
        mockContaRepo.salvar(new Conta(jogadorId, "Jogador", "hash",
                TipoConta.JOGADOR, StatusConta.ATIVA));
    }

    public PublicacaoJogoFuncionalidade() {
    }

    // Método auxiliar para criar URL
    @SuppressWarnings("deprecation")
    private URL criarURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL inválida: " + url, e);
        }
    }

    // ============================================================================
    // Step Definitions - Contexto e Setup
    // ============================================================================

    @Given("que sou um desenvolvedor com conta ativa")
    public void que_sou_um_desenvolvedor_com_conta_ativa() {
        tipoContaAtual = TipoConta.DESENVOLVEDORA;
        statusContaAtual = StatusConta.ATIVA;
        mockContaRepo.salvar(new Conta(desenvolvedorId, "Dev", "hash",
                tipoContaAtual, statusContaAtual));
    }

    @Given("que sou um jogador com conta ativa")
    public void que_sou_um_jogador_com_conta_ativa() {
        tipoContaAtual = TipoConta.JOGADOR;
        statusContaAtual = StatusConta.ATIVA;
        mockContaRepo.salvar(new Conta(desenvolvedorId, "Jogador", "hash",
                tipoContaAtual, statusContaAtual));
    }

    @Given("que sou um desenvolvedor com conta inativa")
    public void que_sou_um_desenvolvedor_com_conta_inativa() {
        tipoContaAtual = TipoConta.DESENVOLVEDORA;
        statusContaAtual = StatusConta.INATIVA;
        mockContaRepo.salvar(new Conta(desenvolvedorId, "Dev Inativo", "hash",
                tipoContaAtual, statusContaAtual));
    }

    // ============================================================================
    // Step Definitions - Criação de Jogo
    // ============================================================================

    @Given("que criei um jogo com título {string}")
    public void que_criei_um_jogo_com_titulo(String titulo) {
        this.tituloAtual = titulo;
        if (this.descricaoAtual == null) {
            // Não fazer nada - deixar null para testes que esperam erro
        }
    }

    @Given("que criei um jogo sem título")
    public void que_criei_um_jogo_sem_titulo() {
        this.tituloAtual = null;
    }

    @And("adicionei a descrição {string}")
    public void adicionei_a_descricao(String descricao) {
        this.descricaoAtual = descricao;
    }

    @And("não adicionei descrição")
    public void nao_adicionei_descricao() {
        this.descricaoAtual = null;
    }

    @And("adicionei a capa oficial")
    public void adicionei_a_capa_oficial() {
        this.capaOficial = criarURL("https://example.com/capa.jpg");
    }

    @And("não adicionei capa oficial")
    public void nao_adicionei_capa_oficial() {
        this.capaOficial = null;
    }

    @And("adicionei {int} screenshot(s)")
    public void adicionei_screenshots(Integer quantidade) {
        this.screenshots.clear();
        for (int i = 0; i < quantidade; i++) {
            this.screenshots.add(criarURL("https://example.com/screenshot" + i + ".jpg"));
        }
    }

    @And("adicionei {int} vídeo(s)")
    public void adicionei_videos(Integer quantidade) {
        this.videos.clear();
        for (int i = 0; i < quantidade; i++) {
            this.videos.add(criarURL("https://example.com/video" + i + ".mp4"));
        }
    }

    @And("não adicionei screenshots nem vídeos")
    public void nao_adicionei_screenshots_nem_videos() {
        this.screenshots.clear();
        this.videos.clear();
    }

    @And("adicionei a tag {string}")
    public void adicionei_a_tag(String nomeTag) {
        Tag tag = new Tag(new TagId("tag-" + nomeTag.toLowerCase()), nomeTag);
        this.tags.add(tag);
    }

    @And("adicionei as tags {string}, {string}, {string}, {string}")
    public void adicionei_quatro_tags(String tag1, String tag2, String tag3, String tag4) {
        adicionei_a_tag(tag1);
        adicionei_a_tag(tag2);
        adicionei_a_tag(tag3);
        adicionei_a_tag(tag4);
    }

    @And("não adicionei nenhuma tag")
    public void nao_adicionei_nenhuma_tag() {
        this.tags.clear();
    }

    @And("marquei o jogo como NSFW")
    public void marquei_o_jogo_como_nsfw() {
        this.isNSFW = true;
    }

    @Given("que criei um jogo válido")
    public void que_criei_um_jogo_valido() {
        tituloAtual = "Jogo Válido";
        descricaoAtual = "Uma descrição válida para o jogo";
        capaOficial = criarURL("https://example.com/capa.jpg");
        screenshots.clear();
        screenshots.add(criarURL("https://example.com/screenshot.jpg"));
        tags.clear();
        tags.add(new Tag(new TagId("tag-aventura"), "Aventura"));

        criarJogoAtual();
        if (jogoAtual != null) {
            operacaoSucesso = false;
            mensagemErro = null;
        }
    }

    private void criarJogoAtual() {
        try {
            JogoId jogoId = new JogoId("jogo-" + System.nanoTime());
            jogoAtual = new Jogo(jogoId, desenvolvedorId, tituloAtual, descricaoAtual,
                    capaOficial, tags, isNSFW, LocalDate.now());

            for (URL screenshot : screenshots) {
                jogoAtual.adicionarScreenshot(screenshot);
            }

            for (URL video : videos) {
                jogoAtual.adicionarVideo(video);
            }

        } catch (Exception e) {
            jogoAtual = null;
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    // ============================================================================
    // Step Definitions - Cenários de Duplicação
    // ============================================================================

    @Given("que já publiquei um jogo com título {string}")
    public void que_ja_publiquei_um_jogo_com_titulo(String titulo) {
        tituloAtual = titulo;
        descricaoAtual = "Descrição do jogo publicado";
        capaOficial = criarURL("https://example.com/capa.jpg");
        screenshots.add(criarURL("https://example.com/screenshot.jpg"));
        tags.add(new Tag(new TagId("tag-aventura"), "Aventura"));

        criarJogoAtual();

        try {
            if (jogoAtual != null) {
                mockJogoRepo.salvar(jogoAtual);

                publicacaoService.publicarJogo(desenvolvedorId, jogoAtual.getId());
                
                jogoAtual = mockJogoRepo.obterPorId(jogoAtual.getId());
                operacaoSucesso = true;
            } else {
                operacaoSucesso = false;
                mensagemErro = "Falha ao criar jogo para publicação";
            }
        } catch (Exception e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Given("que criei outro jogo com título {string}")
    public void que_criei_outro_jogo_com_titulo(String titulo) {
        jogoAtual = null;
        operacaoSucesso = false;
        mensagemErro = null;
        
        screenshots.clear();
        videos.clear();
        tags.clear();
        isNSFW = false;

        tituloAtual = titulo;
    }

    @Given("que outro desenvolvedor já publicou um jogo com título {string}")
    public void que_outro_desenvolvedor_ja_publicou_um_jogo_com_titulo(String titulo) {
        ContaId outroDevId = outroDesenvolvedorId;
        JogoId jogoId = new JogoId("jogo-outro-dev-" + System.nanoTime());

        List<Tag> tagsOutroJogo = new ArrayList<>();
        tagsOutroJogo.add(new Tag(new TagId("tag-aventura"), "Aventura"));

        Jogo outroJogo = new Jogo(jogoId, outroDevId, titulo, "Descrição do outro jogo",
                criarURL("https://example.com/capa.jpg"),
                tagsOutroJogo, false, LocalDate.now());

        outroJogo.adicionarScreenshot(criarURL("https://example.com/screenshot.jpg"));
        mockJogoRepo.salvar(outroJogo);

        try {
            publicacaoService.publicarJogo(outroDevId, outroJogo.getId());
            outroJogo = mockJogoRepo.obterPorId(jogoId);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao publicar jogo do outro desenvolvedor: " + e.getMessage(), e);
        }
    }

    // ============================================================================
    // Step Definitions - Cenários de Status
    // ============================================================================

    @Given("que criei um jogo aguardando validação")
    public void que_criei_um_jogo_aguardando_validacao() {
        que_criei_um_jogo_valido();
        assertEquals(StatusPublicacao.AGUARDANDO_VALIDACAO, jogoAtual.getStatus());
        mockJogoRepo.salvar(jogoAtual);
    }

    @Given("o jogo foi rejeitado por um moderador")
    public void o_jogo_foi_rejeitado_por_um_moderador() {
        if (jogoAtual == null) {
            descricaoAtual = "Descrição para jogo rejeitado";
            capaOficial = criarURL("https://example.com/capa.jpg");
            if (screenshots.isEmpty()) {
                screenshots.add(criarURL("https://example.com/screenshot.jpg"));
            }
            if (tags.isEmpty()) {
                tags.add(new Tag(new TagId("tag-aventura"), "Aventura"));
            }
            criarJogoAtual();
        }
        
        if (jogoAtual != null) {
            jogoAtual.rejeitar();
            mockJogoRepo.salvar(jogoAtual);
        }
    }

    // ============================================================================
    // Step Definitions - Cenários de Autorização
    // ============================================================================

    @Given("que outro desenvolvedor criou um jogo")
    public void que_outro_desenvolvedor_criou_um_jogo() {
        JogoId jogoId = new JogoId("jogo-outro-dev-" + System.nanoTime());
        List<Tag> tagsJogo = new ArrayList<>();
        tagsJogo.add(new Tag(new TagId("tag-aventura"), "Aventura"));

        jogoAtual = new Jogo(jogoId, outroDesenvolvedorId, "Jogo do Outro Dev",
                "Descrição do jogo", criarURL("https://example.com/capa.jpg"),
                tagsJogo, false, LocalDate.now());

        jogoAtual.adicionarScreenshot(criarURL("https://example.com/screenshot.jpg"));
        mockJogoRepo.salvar(jogoAtual);
    }

    // ============================================================================
    // Step Definitions - Ações (When)
    // ============================================================================

    @When("publico o jogo")
    public void publico_o_jogo() {
        try {
            criarJogoAtual();
            if (jogoAtual != null) {
                mockJogoRepo.salvar(jogoAtual);

                publicacaoService.publicarJogo(desenvolvedorId, jogoAtual.getId());
                jogoAtual = mockJogoRepo.obterPorId(jogoAtual.getId());
                operacaoSucesso = true;
                mensagemErro = null;
            } else {
                operacaoSucesso = false;
            }
        } catch (Exception e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @When("tento publicar o jogo")
    public void tento_publicar_o_jogo() {
        try {
            if (jogoAtual == null) {
                criarJogoAtual();
            }
            if (jogoAtual != null) {
                mockJogoRepo.salvar(jogoAtual);

                publicacaoService.publicarJogo(desenvolvedorId, jogoAtual.getId());
                jogoAtual = mockJogoRepo.obterPorId(jogoAtual.getId());
                operacaoSucesso = true;
                mensagemErro = null;
            } else {
                operacaoSucesso = false;
            }
        } catch (Exception e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @When("tento publicar o jogo novamente")
    public void tento_publicar_o_jogo_novamente() {
        try {
            publicacaoService.publicarJogo(desenvolvedorId, jogoAtual.getId());
            operacaoSucesso = true;
        } catch (Exception e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @When("tento publicar o jogo deste desenvolvedor")
    public void tento_publicar_o_jogo_deste_desenvolvedor() {
        try {
            publicacaoService.publicarJogo(desenvolvedorId, jogoAtual.getId());
            operacaoSucesso = true;
        } catch (Exception e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @When("adiciono {int} tags ao jogo")
    public void adiciono_n_tags_ao_jogo(Integer quantidade) {
        try {
            criarJogoAtual();

            for (int i = 0; i < quantidade; i++) {
                Tag tag = new Tag(new TagId("tag-" + i), "Tag" + i);
                jogoAtual.adicionarTag(tag);
            }
            operacaoSucesso = true;
        } catch (Exception e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @When("um moderador rejeita o jogo com motivo {string}")
    public void um_moderador_rejeita_o_jogo_com_motivo(String motivo) {
        try {
            publicacaoService.rejeitarJogo(jogoAtual.getId(), motivo);
            jogoAtual = mockJogoRepo.obterPorId(jogoAtual.getId());
            operacaoSucesso = true;
        } catch (Exception e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @When("arquivo o jogo")
    public void arquivo_o_jogo() {
        try {
            publicacaoService.arquivarJogo(desenvolvedorId, jogoAtual.getId());
            jogoAtual = mockJogoRepo.obterPorId(jogoAtual.getId());
            operacaoSucesso = true;
            mensagemErro = null;
        } catch (Exception e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @When("tento arquivar o jogo")
    public void tento_arquivar_o_jogo() {
        arquivo_o_jogo();
    }

    @When("tento criar um jogo com título {string}")
    public void tento_criar_um_jogo_com_titulo(String titulo) {
        try {
            tituloAtual = titulo;
            capaOficial = criarURL("https://example.com/capa.jpg");

            JogoId jogoId = new JogoId("jogo-" + System.nanoTime());
            jogoAtual = new Jogo(jogoId, desenvolvedorId, tituloAtual, descricaoAtual,
                    capaOficial, tags, isNSFW, LocalDate.now());

            operacaoSucesso = true;
        } catch (Exception e) {
            jogoAtual = null;
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Given("que tento criar um jogo com título {string}")
    public void que_tento_criar_um_jogo_com_titulo(String titulo) {
        tento_criar_um_jogo_com_titulo(titulo);
    }

    @When("adiciono a descrição {string}")
    public void adiciono_a_descricao(String descricao) {
        try {
            descricaoAtual = descricao;

            if (jogoAtual == null && tituloAtual != null) {
                JogoId jogoId = new JogoId("jogo-" + System.nanoTime());
                List<Tag> tagsTemp = tags.isEmpty() ? new ArrayList<>() : tags;
                jogoAtual = new Jogo(jogoId, desenvolvedorId, tituloAtual, descricaoAtual,
                        capaOficial, tagsTemp, isNSFW, LocalDate.now());
            }

            operacaoSucesso = true;
            mensagemErro = null;
        } catch (Exception e) {
            jogoAtual = null;
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    // ============================================================================
    // Step Definitions - Assertions (Then)
    // ============================================================================

    @Then("o jogo deve ser publicado com sucesso")
    public void o_jogo_deve_ser_publicado_com_sucesso() {
        assertTrue(operacaoSucesso,
                "Operação deveria ter sucesso. Erro: " + (mensagemErro != null ? mensagemErro : "sem erro registrado"));
        assertNotNull(jogoAtual, "Jogo não deveria ser nulo");
    }

    @Then("o jogo deve ter status {string}")
    public void o_jogo_deve_ter_status(String status) {
        assertNotNull(jogoAtual, "Jogo não deveria ser nulo");
        assertEquals(StatusPublicacao.valueOf(status), jogoAtual.getStatus());
    }

    @Then("a operação deve falhar")
    public void a_operacao_deve_falhar() {
        assertFalse(operacaoSucesso, "Operação deveria ter falhou");
    }

    @Then("devo receber um erro informando {string}")
    public void devo_receber_um_erro_informando(String mensagemEsperada) {
        assertFalse(operacaoSucesso, "Operação deveria ter falhado");
        assertNotNull(mensagemErro, "Mensagem de erro não deveria ser null");
        assertTrue(mensagemErro.contains(mensagemEsperada) ||
                mensagemErro.toLowerCase().contains(mensagemEsperada.toLowerCase()),
                "Mensagem de erro deveria conter: '" + mensagemEsperada +
                        "', mas foi: '" + mensagemErro + "'");
    }

    @Then("o jogo deve ser rejeitado")
    public void o_jogo_deve_ser_rejeitado() {
        assertTrue(operacaoSucesso, "Operação deveria ter sucesso");
        assertNotNull(jogoAtual, "Jogo não deveria ser nulo");
    }

    @Then("o jogo deve ser arquivado com sucesso")
    public void o_jogo_deve_ser_arquivado_com_sucesso() {
        assertTrue(operacaoSucesso, "Operação deveria ter sucesso");
        assertNotNull(jogoAtual, "Jogo não deveria ser nulo");
    }
}
