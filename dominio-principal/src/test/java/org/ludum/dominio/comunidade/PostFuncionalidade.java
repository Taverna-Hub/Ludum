package org.ludum.dominio.comunidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.tag.entidades.Tag;
import org.ludum.catalogo.tag.entidades.TagId;
import org.ludum.comunidade.post.repositorios.PostRepository;
import org.ludum.comunidade.post.services.ConteudoAdultoValidator;
import org.ludum.comunidade.post.services.ImagemCompressor;
import org.ludum.comunidade.post.services.JogoInfo;
import org.ludum.comunidade.post.services.MalwareScanner;
import org.ludum.comunidade.post.services.NotificacaoService;
import org.ludum.comunidade.post.services.PostService;
import org.ludum.comunidade.post.entidades.ComentarioId;
import org.ludum.comunidade.post.entidades.Post;
import org.ludum.comunidade.post.entidades.PostId;
import org.ludum.comunidade.post.enums.PostStatus;
import org.ludum.identidade.conta.entities.ContaId;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PostFuncionalidade {

    private ContaId autorId;
    private ContaId outroUsuarioId;
    private JogoId jogoId;
    private List<Tag> tags;
    private String titulo;
    private String conteudo;
    private Post postAtual;
    private PostService postService;
    private boolean operacaoSucesso;
    private String mensagemErro;
    private List<Post> postsEncontrados;
    private URL imagemUrl;
    private boolean jogoTemTag18;
    private LocalDateTime dataAgendamento;

    // Mock do repositório
    private static class MockPostRepository implements PostRepository {
        private List<Post> posts = new ArrayList<>();

        @Override
        public void salvar(Post post) {
            posts.removeIf(p -> p.getId().equals(post.getId()));
            posts.add(post);
        }

        @Override
        public Post obterPorId(PostId id) {
            return posts.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
        }

        @Override
        public void remover(Post post) {
            posts.removeIf(p -> p.getId().equals(post.getId()));
        }

        @Override
        public List<Post> obterTodosPosts() {
            return new ArrayList<>(posts);
        }

        @Override
        public List<Post> obterPorAutor(ContaId autorId) {
            return posts.stream()
                    .filter(p -> p.getAutorId().equals(autorId))
                    .collect(Collectors.toList());
        }

        @Override
        public List<Post> buscarPorTag(String tag) {
            return posts.stream()
                    .filter(p -> p.getTags().stream()
                            .anyMatch(t -> t.getNome().equalsIgnoreCase(tag)))
                    .collect(Collectors.toList());
        }

        @Override
        public List<Post> obterPorStatus(PostStatus status) {
            return posts.stream()
                    .filter(p -> p.getStatus() == status)
                    .collect(Collectors.toList());
        }

        public List<Post> getPosts() {
            return new ArrayList<>(posts);
        }
    }

    // Mock do scanner de malware
    private static class MockMalwareScanner implements MalwareScanner {
        private static final String[] MALWARE_PATTERNS = {
                "virus", "malware", "trojan", "worm", "ransomware",
                ".exe", ".bat", ".cmd", ".sh", ".scr"
        };

        @Override
        public boolean contemMalware(URL imagemUrl) {
            Objects.requireNonNull(imagemUrl, "URL da imagem não pode ser nula");

            String urlString = imagemUrl.toString().toLowerCase();

            // Detecta padrões suspeitos no nome
            for (String pattern : MALWARE_PATTERNS) {
                if (urlString.contains(pattern)) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean contemMalware(byte[] fileBytes, String fileName) {
            Objects.requireNonNull(fileBytes, "Bytes do arquivo não podem ser nulos");
            Objects.requireNonNull(fileName, "Nome do arquivo não pode ser nulo");

            String fileNameLower = fileName.toLowerCase();

            // Detecta padrões suspeitos no nome do arquivo
            for (String pattern : MALWARE_PATTERNS) {
                if (fileNameLower.contains(pattern)) {
                    return true;
                }
            }

            // Detecta header "MZ" (executável Windows)
            if (fileBytes.length >= 2 && fileBytes[0] == 'M' && fileBytes[1] == 'Z') {
                return true;
            }

            return false;
        }
    }

    // Mock do compressor de imagens
    private static class MockImagemCompressor implements ImagemCompressor {
        private static final long LIMITE_BYTES = 2 * 1024 * 1024; // 2MB

        @Override
        public boolean excedeLimit(URL imagem) {
            Objects.requireNonNull(imagem, "URL da imagem não pode ser nula");
            // Mock: considera que imagens grandes (nome contendo "large" ou "big") excedem
            // limite
            String urlString = imagem.toString().toLowerCase();
            return urlString.contains("large") || urlString.contains("big") || urlString.contains("5mb");
        }

        @Override
        public URL compactar(URL imagem) {
            Objects.requireNonNull(imagem, "URL da imagem não pode ser nula");
            try {
                // Mock: retorna URL com sufixo "_compressed"
                String urlOriginal = imagem.toString();
                String urlComprimida = urlOriginal.replace(".jpg", "_compressed.jpg")
                        .replace(".png", "_compressed.png");
                return URI.create(urlComprimida).toURL();
            } catch (Exception e) {
                throw new SecurityException("Erro ao comprimir imagem", e);
            }
        }
    }

    // Mock do validador de conteúdo adulto
    private static class MockConteudoAdultoValidator implements ConteudoAdultoValidator {
        @Override
        public boolean contemConteudoAdulto(URL imagem) {
            Objects.requireNonNull(imagem, "URL da imagem não pode ser nula");
            // Mock: detecta se URL contém palavras relacionadas a conteúdo adulto
            String urlString = imagem.toString().toLowerCase();
            return urlString.contains("nsfw") || urlString.contains("adult") || urlString.contains("18+");
        }
    }

    // Mock do serviço de informações de jogo (inner class)
    private static class MockJogoInfo implements JogoInfo {
        private final Map<String, List<Tag>> jogosTags = new HashMap<>();
        private final Map<String, Boolean> jogosAdultos = new HashMap<>();

        public MockJogoInfo() {
            // Configurar dados mock padrão
            List<Tag> tagsAcao = new ArrayList<>();
            tagsAcao.add(new Tag(new TagId("1"), "Ação"));
            tagsAcao.add(new Tag(new TagId("2"), "Aventura"));
            tagsAcao.add(new Tag(new TagId("3"), "FPS"));

            jogosTags.put("jogo-789", tagsAcao);
            jogosAdultos.put("jogo-789", false);
        }

        public void configurarJogo(JogoId jogoId, List<Tag> tags, boolean isAdulto) {
            jogosTags.put(jogoId.getValue(), tags);
            jogosAdultos.put(jogoId.getValue(), isAdulto);
        }

        @Override
        public List<Tag> obterTagsDoJogo(JogoId jogoId) {
            Objects.requireNonNull(jogoId, "JogoId não pode ser nulo");
            List<Tag> tags = jogosTags.get(jogoId.getValue());
            if (tags == null) {
                throw new IllegalStateException("Jogo não encontrado: " + jogoId.getValue());
            }
            return new ArrayList<>(tags);
        }

        @Override
        public boolean isJogoAdulto(JogoId jogoId) {
            Objects.requireNonNull(jogoId, "JogoId não pode ser nulo");
            Boolean isAdulto = jogosAdultos.get(jogoId.getValue());
            if (isAdulto == null) {
                throw new IllegalStateException("Jogo não encontrado: " + jogoId.getValue());
            }
            return isAdulto;
        }
    }

    // Mock do serviço de notificações (inner class)
    private static class MockNotificacaoService implements NotificacaoService {
        private final List<String> notificacoesEnviadas = new ArrayList<>();

        @Override
        public void notificarFalhaAgendamento(ContaId autorId, PostId postId, String motivoErro) {
            notificacoesEnviadas
                    .add("FALHA_AGENDAMENTO:" + autorId.getValue() + ":" + postId.getId() + ":" + motivoErro);
        }

        @Override
        public void notificarImagemBloqueada(ContaId autorId, PostId postId) {
            notificacoesEnviadas.add("IMAGEM_BLOQUEADA:" + autorId.getValue() + ":" + postId.getId());
        }

        public List<String> getNotificacoesEnviadas() {
            return new ArrayList<>(notificacoesEnviadas);
        }

        public void limpar() {
            notificacoesEnviadas.clear();
        }
    }

    private MockPostRepository mockRepo;
    private MockMalwareScanner mockMalwareScanner;
    private MockImagemCompressor mockImagemCompressor;
    private MockConteudoAdultoValidator mockConteudoAdultoValidator;
    private MockJogoInfo mockJogoInfo;
    private MockNotificacaoService mockNotificacaoService;

    @Before
    public void setup() {
        this.mockRepo = new MockPostRepository();
        this.mockMalwareScanner = new MockMalwareScanner();
        this.mockImagemCompressor = new MockImagemCompressor();
        this.mockConteudoAdultoValidator = new MockConteudoAdultoValidator();
        this.mockJogoInfo = new MockJogoInfo();
        this.mockNotificacaoService = new MockNotificacaoService();

        // Criar PostService com todas as dependências mockadas
        this.postService = new PostService(
                mockRepo,
                mockMalwareScanner,
                mockImagemCompressor,
                mockConteudoAdultoValidator,
                mockJogoInfo,
                mockNotificacaoService);

        this.autorId = new ContaId("autor-123");
        this.outroUsuarioId = new ContaId("usuario-456");
        this.jogoId = new JogoId("jogo-789");
        this.tags = new ArrayList<>();
        this.titulo = "";
        this.conteudo = "";
        this.postAtual = null;
        this.operacaoSucesso = false;
        this.mensagemErro = "";
        this.postsEncontrados = new ArrayList<>();
        this.imagemUrl = null;
        this.jogoTemTag18 = false;
        this.dataAgendamento = null;
    }

    public PostFuncionalidade() {
    }

    // Método auxiliar para obter uma tag válida do jogo
    private Tag obterPrimeiraTagDoJogo() {
        return mockJogoInfo.obterTagsDoJogo(jogoId).get(0);
    }

    // Método auxiliar para adicionar uma tag válida do jogo à lista de tags
    private void adicionarTagValidaDoJogo() {
        List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);
        if (!tagsDoJogo.isEmpty()) {
            this.tags.add(tagsDoJogo.get(0));
        }
    }

    // Método auxiliar para criar tags e adicioná-las ao jogo mock
    private void criarEAdicionarTagsAoJogo(List<Tag> novasTags) {
        List<Tag> tagsDoJogo = new ArrayList<>(mockJogoInfo.obterTagsDoJogo(jogoId));
        tagsDoJogo.addAll(novasTags);
        mockJogoInfo.configurarJogo(jogoId, tagsDoJogo, false);
    }

    // --- Scenario: Criar post com sucesso ---
    @Given("que sou um usuário autenticado")
    public void que_sou_um_usuario_autenticado() {
        assertNotNull(this.autorId);
    }

    @And("quero criar um post sobre um jogo")
    public void quero_criar_um_post_sobre_um_jogo() {
        assertNotNull(this.jogoId);
        // Garantir que o jogo está configurado no mock
        if (mockJogoInfo != null) {
            try {
                mockJogoInfo.obterTagsDoJogo(jogoId);
            } catch (IllegalStateException e) {
                // Jogo não existe no mock, configurar um padrão
                List<Tag> tagsDefault = new ArrayList<>();
                tagsDefault.add(new Tag(new TagId("default-1"), "acao"));
                tagsDefault.add(new Tag(new TagId("default-2"), "aventura"));
                mockJogoInfo.configurarJogo(jogoId, tagsDefault, false);
            }
        }
    }

    @When("preencho o título com {string}")
    public void preencho_o_titulo_com(String titulo) {
        this.titulo = titulo;
    }

    @And("preencho o conteúdo com {string}")
    public void preencho_o_conteudo_com(String conteudo) {
        this.conteudo = conteudo;
    }

    @And("adiciono {int} tag\\(s)")
    public void adiciono_tags(Integer quantidade) {
        this.tags.clear();

        // Obter as tags válidas do jogo
        List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);

        // Usar as tags do jogo (até o limite solicitado)
        for (int i = 0; i < Math.min(quantidade, tagsDoJogo.size()); i++) {
            this.tags.add(tagsDoJogo.get(i));
        }

        // Se precisar de mais tags, criar novas e adicionar ao jogo
        if (quantidade > tagsDoJogo.size()) {
            for (int i = tagsDoJogo.size(); i < quantidade; i++) {
                Tag novaTag = new Tag(new TagId("tag-extra-" + i), "TagExtra" + i);
                this.tags.add(novaTag);
                tagsDoJogo.add(novaTag);
            }
            // Atualizar o mock com as novas tags
            mockJogoInfo.configurarJogo(jogoId, tagsDoJogo, false);
        }
    }

    @And("publico o post")
    public void publico_o_post() {
        try {
            // O PostService agora já faz a validação de malware internamente
            this.postAtual = postService.publicarPost(jogoId, autorId, titulo, conteudo, imagemUrl, tags);
            operacaoSucesso = true;
        } catch (IllegalArgumentException | SecurityException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o post deve ser criado com sucesso")
    public void o_post_deve_ser_criado_com_sucesso() {
        assertTrue(operacaoSucesso);
        assertNotNull(postAtual);
    }

    @And("o post deve ter status {string}")
    public void o_post_deve_ter_status(String status) {
        assertEquals(PostStatus.valueOf(status), postAtual.getStatus());
    }

    // --- Scenario: Falha ao criar post sem título ---
    @Then("devo receber um erro informando {string}")
    public void devo_receber_um_erro_informando(String mensagemEsperada) {
        assertFalse(operacaoSucesso);
        assertTrue(mensagemErro.contains(mensagemEsperada) ||
                mensagemErro.toLowerCase().contains(mensagemEsperada.toLowerCase()));
    }

    // --- Scenario: Editar post com sucesso ---
    @Given("que tenho um post publicado")
    public void que_tenho_um_post_publicado() {
        // Usar tags válidas do jogo
        List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);
        this.tags.clear();
        this.tags.add(tagsDoJogo.get(0)); // Usar primeira tag do jogo

        this.postAtual = postService.publicarPost(jogoId, autorId, "Título Original", "Conteúdo Original", null, tags);
        assertNotNull(postAtual);
    }

    @And("que sou o autor do post")
    public void que_sou_o_autor_do_post() {
        assertEquals(autorId, postAtual.getAutorId());
    }

    @When("edito o título para {string}")
    public void edito_o_titulo_para(String novoTitulo) {
        this.titulo = novoTitulo;
    }

    @And("edito o conteúdo para {string}")
    public void edito_o_conteudo_para(String novoConteudo) {
        this.conteudo = novoConteudo;
    }

    @And("salvo as alterações")
    public void salvo_as_alteracoes() {
        try {
            postService.editarPost(postAtual.getId(), autorId, titulo, conteudo);
            this.postAtual = mockRepo.obterPorId(postAtual.getId());
            operacaoSucesso = true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o post deve ser atualizado com sucesso")
    public void o_post_deve_ser_atualizado_com_sucesso() {
        assertTrue(operacaoSucesso);
    }

    @And("o título deve ser {string}")
    public void o_titulo_deve_ser(String tituloEsperado) {
        assertEquals(tituloEsperado, postAtual.getTitulo());
    }

    @And("o conteúdo deve ser {string}")
    public void o_conteudo_deve_ser(String conteudoEsperado) {
        assertEquals(conteudoEsperado, postAtual.getConteudo());
    }

    // --- Scenario: Curtir um post ---
    @Given("que existe um post publicado")
    public void que_existe_um_post_publicado() {
        // Usar tags válidas do jogo
        List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);
        this.tags.clear();
        this.tags.add(tagsDoJogo.get(0));

        this.postAtual = postService.publicarPost(jogoId, autorId, "Título do Post", "Conteúdo do Post", null, tags);
        assertNotNull(postAtual);
    }

    @When("um usuário curte o post")
    public void um_usuario_curte_o_post() {
        try {
            postService.curtirPost(postAtual.getId(), outroUsuarioId);
            this.postAtual = mockRepo.obterPorId(postAtual.getId());
            operacaoSucesso = true;
        } catch (IllegalArgumentException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o número de curtidas deve ser {int}")
    public void o_numero_de_curtidas_deve_ser(Integer numeroCurtidas) {
        assertEquals(numeroCurtidas, postAtual.getCurtidas().size());
    }

    // --- Scenario: Não permitir curtir o mesmo post duas vezes ---
    @Given("que um usuário já curtiu o post")
    public void que_um_usuario_ja_curtiu_o_post() {
        // Usar tags válidas do jogo
        List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);
        this.tags.clear();
        this.tags.add(tagsDoJogo.get(0));

        this.postAtual = postService.publicarPost(jogoId, autorId, "Título do Post", "Conteúdo do Post", null, tags);
        postService.curtirPost(postAtual.getId(), outroUsuarioId);
        this.postAtual = mockRepo.obterPorId(postAtual.getId());
    }

    @When("o mesmo usuário tenta curtir novamente")
    public void o_mesmo_usuario_tenta_curtir_novamente() {
        try {
            postService.curtirPost(postAtual.getId(), outroUsuarioId);
            operacaoSucesso = true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("a operação deve falhar")
    public void a_operacao_deve_falhar() {
        assertFalse(operacaoSucesso);
    }

    // --- Scenario: Adicionar comentário a um post ---
    @When("um usuário adiciona um comentário {string}")
    public void um_usuario_adiciona_um_comentario(String textoComentario) {
        try {
            postService.comentarPost(postAtual.getId(), outroUsuarioId, textoComentario);
            this.postAtual = mockRepo.obterPorId(postAtual.getId());
            operacaoSucesso = true;
        } catch (IllegalArgumentException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o número de comentários deve ser {int}")
    public void o_numero_de_comentarios_deve_ser(Integer numeroComentarios) {
        // Contar apenas comentários visíveis (não ocultos por soft delete)
        long comentariosVisiveis = postAtual.getComentarios().stream()
                .filter(c -> !c.isOculto())
                .count();
        assertEquals(numeroComentarios, (int) comentariosVisiveis);
    }

    @And("o comentário deve conter o texto {string}")
    public void o_comentario_deve_conter_o_texto(String textoEsperado) {
        assertTrue(postAtual.getComentarios().stream()
                .anyMatch(c -> c.getTexto().equals(textoEsperado)));
    }

    // --- Scenario: Criar rascunho de post ---
    @When("crio um rascunho com título {string} e conteúdo {string}")
    public void crio_um_rascunho_com_titulo_e_conteudo(String tituloRascunho, String conteudoRascunho) {
        try {
            // Usar tag válida do jogo
            List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);
            this.tags.clear();
            this.tags.add(tagsDoJogo.get(0));

            this.postAtual = postService.criarRascunho(jogoId, autorId, tituloRascunho, conteudoRascunho, null, tags);
            operacaoSucesso = true;
        } catch (IllegalArgumentException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o rascunho deve ser salvo")
    public void o_rascunho_deve_ser_salvo() {
        assertTrue(operacaoSucesso);
        assertNotNull(postAtual);
    }

    // --- Scenario: Remover post como autor ---
    @When("removo o post")
    public void removo_o_post() {
        try {
            postService.removerPost(postAtual.getId(), autorId);
            operacaoSucesso = true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o post deve ser removido com sucesso")
    public void o_post_deve_ser_removido_com_sucesso() {
        assertTrue(operacaoSucesso);
        assertTrue(mockRepo.obterPorId(postAtual.getId()) == null);
    }

    // --- Scenario: Falha ao remover post de outro autor ---
    @And("que não sou o autor do post")
    public void que_nao_sou_o_autor_do_post() {
        this.autorId = new ContaId("outro-autor-diferente");
    }

    @Then("devo receber um erro de autorização")
    public void devo_receber_um_erro_de_autorizacao() {
        assertFalse(operacaoSucesso);
        assertTrue(mensagemErro.toLowerCase().contains("autor") ||
                mensagemErro.toLowerCase().contains("pode"));
    }

    // --- Scenario: Buscar posts por tag ---
    @Given("que existem posts com a tag {string}")
    public void que_existem_posts_com_a_tag(String nomeTag) {
        // Criar tag e adicionar ao jogo
        Tag tag = new Tag(new TagId("rpg-tag"), nomeTag);
        List<Tag> tagsRpg = new ArrayList<>();
        tagsRpg.add(tag);

        // Adicionar tag ao jogo mock
        List<Tag> tagsDoJogo = new ArrayList<>(mockJogoInfo.obterTagsDoJogo(jogoId));
        tagsDoJogo.add(tag);
        mockJogoInfo.configurarJogo(jogoId, tagsDoJogo, false);

        postService.publicarPost(jogoId, autorId, "Post RPG 1", "Conteúdo RPG 1", null, tagsRpg);
        postService.publicarPost(jogoId, autorId, "Post RPG 2", "Conteúdo RPG 2", null, tagsRpg);
    }

    @When("busco posts pela tag {string}")
    public void busco_posts_pela_tag(String nomeTag) {
        this.postsEncontrados = mockRepo.buscarPorTag(nomeTag);
    }

    @Then("devo encontrar {int} post\\(s)")
    public void devo_encontrar_posts(Integer quantidadeEsperada) {
        assertEquals(quantidadeEsperada, postsEncontrados.size());
    }

    // --- Novos step definitions para tags específicas ---
    @And("adiciono as tags {string} e {string}")
    public void adiciono_as_tags_e(String tag1, String tag2) {
        this.tags.clear();

        // Criar as tags
        Tag tagObj1 = new Tag(new TagId("tag-" + tag1.toLowerCase()), tag1);
        Tag tagObj2 = new Tag(new TagId("tag-" + tag2.toLowerCase()), tag2);

        this.tags.add(tagObj1);
        this.tags.add(tagObj2);

        // Adicionar ao jogo mock
        List<Tag> tagsAtuais = new ArrayList<>(mockJogoInfo.obterTagsDoJogo(jogoId));
        tagsAtuais.add(tagObj1);
        tagsAtuais.add(tagObj2);
        mockJogoInfo.configurarJogo(jogoId, tagsAtuais, false);
    }

    @And("adiciono a tag {string}")
    public void adiciono_a_tag(String nomeTag) {
        this.tags.clear();

        // Criar a tag
        Tag tagObj = new Tag(new TagId("tag-" + nomeTag.toLowerCase()), nomeTag);
        this.tags.add(tagObj);

        // Adicionar ao jogo mock
        List<Tag> tagsAtuais = new ArrayList<>(mockJogoInfo.obterTagsDoJogo(jogoId));
        tagsAtuais.add(tagObj);
        mockJogoInfo.configurarJogo(jogoId, tagsAtuais, false);
    }

    @And("não adiciono nenhuma tag")
    public void nao_adiciono_nenhuma_tag() {
        this.tags.clear();
    }

    @And("adiciono as tags {string}, {string}, {string}, {string}, {string} e {string}")
    public void adiciono_seis_tags(String tag1, String tag2, String tag3, String tag4, String tag5, String tag6) {
        this.tags.clear();

        Tag tagObj1 = new Tag(new TagId("tag-" + tag1.toLowerCase()), tag1);
        Tag tagObj2 = new Tag(new TagId("tag-" + tag2.toLowerCase()), tag2);
        Tag tagObj3 = new Tag(new TagId("tag-" + tag3.toLowerCase()), tag3);
        Tag tagObj4 = new Tag(new TagId("tag-" + tag4.toLowerCase()), tag4);
        Tag tagObj5 = new Tag(new TagId("tag-" + tag5.toLowerCase()), tag5);
        Tag tagObj6 = new Tag(new TagId("tag-" + tag6.toLowerCase()), tag6);

        this.tags.add(tagObj1);
        this.tags.add(tagObj2);
        this.tags.add(tagObj3);
        this.tags.add(tagObj4);
        this.tags.add(tagObj5);
        this.tags.add(tagObj6);

        List<Tag> tagsAtuais = new ArrayList<>(mockJogoInfo.obterTagsDoJogo(jogoId));
        tagsAtuais.addAll(this.tags);
        mockJogoInfo.configurarJogo(jogoId, tagsAtuais, false);
    }

    // --- Step definitions para agendamento ---
    @Given("que tenho um rascunho")
    public void que_tenho_um_rascunho() {
        // Usar tag válida do jogo
        List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);
        this.tags.clear();
        this.tags.add(tagsDoJogo.get(0));

        this.postAtual = postService.criarRascunho(jogoId, autorId, "Título Rascunho", "Conteúdo Rascunho", null, tags);
        assertNotNull(postAtual);
        assertEquals(PostStatus.EM_RASCUNHO, postAtual.getStatus());
    }

    @When("agendo o post para publicar em {int} horas")
    public void agendo_o_post_para_publicar_em_horas(Integer horas) {
        try {
            this.dataAgendamento = LocalDateTime.now().plusHours(horas);
            postService.agendarPost(postAtual.getId(), dataAgendamento);
            this.postAtual = mockRepo.obterPorId(postAtual.getId());
            operacaoSucesso = true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o post deve ser agendado com sucesso")
    public void o_post_deve_ser_agendado_com_sucesso() {
        assertTrue(operacaoSucesso);
    }

    // --- Step definitions para imagens ---
    @And("adiciono a imagem {string} de tamanho {int}MB")
    public void adiciono_a_imagem_de_tamanho(String nomeImagem, Integer tamanhoMB) {
        try {
            // Imagem segura - sem padrões suspeitos
            this.imagemUrl = URI.create("https://example.com/images/" + nomeImagem).toURL();
        } catch (Exception e) {
            // Imagem inválida
            this.imagemUrl = null;
        }
    }

    @Given("quero criar um post sobre um jogo sem tag +18")
    public void quero_criar_um_post_sobre_um_jogo_sem_tag_18() {
        assertNotNull(this.jogoId);
        this.jogoTemTag18 = false;
    }

    @And("adiciono a imagem {string} infectada com malware")
    public void adiciono_a_imagem_infectada_com_malware(String nomeImagem) {
        try {
            // Usa URL com padrão "virus" que o MockMalwareScanner vai detectar
            this.imagemUrl = URI.create("https://example.com/images/virus.exe.png").toURL();
        } catch (Exception e) {
            this.imagemUrl = null;
        }
    }

    // --- Scenario: Falha ao criar post com conteúdo muito longo ---
    @And("preencho o conteúdo com {int} caracteres")
    public void preencho_o_conteudo_com_n_caracteres(Integer numeroCaracteres) {
        // Gera string com exatamente N caracteres
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numeroCaracteres; i++) {
            sb.append("a");
        }
        this.conteudo = sb.toString();
    }

    // --- Scenario: Falha ao editar post de outro autor ---
    @Given("que existe um post de outro autor")
    public void que_existe_um_post_de_outro_autor() {
        // Usar tag válida do jogo
        List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);
        this.tags.clear();
        this.tags.add(tagsDoJogo.get(0));

        // Criar post com outro usuário
        this.postAtual = postService.publicarPost(jogoId, outroUsuarioId, "Post de Outro", "Conteúdo de Outro", null,
                tags);
        assertNotNull(postAtual);
        // Garantir que autorId é diferente do autor do post
        this.autorId = new ContaId("usuario-tentando-hackear");
    }

    @When("tento editar o título para {string}")
    public void tento_editar_o_titulo_para(String novoTitulo) {
        this.titulo = novoTitulo;
    }

    @And("tento editar o conteúdo para {string}")
    public void tento_editar_o_conteudo_para(String novoConteudo) {
        this.conteudo = novoConteudo;
    }

    @And("tento salvar as alterações")
    public void tento_salvar_as_alteracoes() {
        try {
            postService.editarPost(postAtual.getId(), autorId, titulo, conteudo);
            this.postAtual = mockRepo.obterPorId(postAtual.getId());
            operacaoSucesso = true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    // --- Scenario: Remover comentário como autor do post ---
    @Given("que existe um post com comentário de outro usuário")
    public void que_existe_um_post_com_comentario_de_outro_usuario() {
        // Usar tag válida do jogo
        List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);
        this.tags.clear();
        this.tags.add(tagsDoJogo.get(0));

        this.postAtual = postService.publicarPost(jogoId, autorId, "Meu Post", "Meu Conteúdo", null, tags);

        // Outro usuário comenta
        postService.comentarPost(postAtual.getId(), outroUsuarioId, "Comentário de outro usuário");
        this.postAtual = mockRepo.obterPorId(postAtual.getId());

        assertEquals(1, postAtual.getComentarios().size());
    }

    @When("removo o comentário como dono do post")
    public void removo_o_comentario_como_dono_do_post() {
        try {
            ComentarioId comentarioId = postAtual.getComentarios().get(0).getId();
            postService.removerComentario(postAtual.getId(), comentarioId, autorId);
            this.postAtual = mockRepo.obterPorId(postAtual.getId());
            operacaoSucesso = true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o comentário deve ser removido com sucesso")
    public void o_comentario_deve_ser_removido_com_sucesso() {
        assertTrue(operacaoSucesso);
    }

    // --- Scenario: Falha ao remover comentário sem autorização ---
    @Given("que existe um comentário em post de outro autor")
    public void que_existe_um_comentario_em_post_de_outro_autor() {
        // Usar tag válida do jogo
        List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);
        this.tags.clear();
        this.tags.add(tagsDoJogo.get(0));

        // Outro usuário cria o post
        ContaId autorDoPost = new ContaId("autor-do-post-123");
        this.postAtual = postService.publicarPost(jogoId, autorDoPost, "Post Alheio", "Conteúdo Alheio", null, tags);

        // Outro usuário comenta
        ContaId autorDoComentario = new ContaId("autor-comentario-456");
        postService.comentarPost(postAtual.getId(), autorDoComentario, "Comentário de outro");
        this.postAtual = mockRepo.obterPorId(postAtual.getId());
    }

    @And("que não sou o autor do comentário nem do post")
    public void que_nao_sou_o_autor_do_comentario_nem_do_post() {
        // Usuário tentando remover é diferente
        this.autorId = new ContaId("intruso-789");
    }

    @When("tento remover o comentário")
    public void tento_remover_o_comentario() {
        try {
            ComentarioId comentarioId = postAtual.getComentarios().get(0).getId();
            postService.removerComentario(postAtual.getId(), comentarioId, autorId);
            operacaoSucesso = true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    // --- Scenario: Publicar rascunho com sucesso ---
    @When("publico o rascunho")
    public void publico_o_rascunho() {
        try {
            postService.publicarRascunho(postAtual.getId(), autorId);
            this.postAtual = mockRepo.obterPorId(postAtual.getId());
            operacaoSucesso = true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o rascunho deve ser publicado com sucesso")
    public void o_rascunho_deve_ser_publicado_com_sucesso() {
        assertTrue(operacaoSucesso);
        assertNotNull(postAtual.getDataPublicacao());
    }

    // --- Scenario: Descurtir um post ---
    @Given("que já curtí um post")
    public void que_ja_curti_um_post() {
        // Usar tag válida do jogo
        List<Tag> tagsDoJogo = mockJogoInfo.obterTagsDoJogo(jogoId);
        this.tags.clear();
        this.tags.add(tagsDoJogo.get(0));

        this.postAtual = postService.publicarPost(jogoId, outroUsuarioId, "Post para descurtir", "Conteúdo", null,
                tags);

        // Curtir como autorId
        postService.curtirPost(postAtual.getId(), autorId);
        this.postAtual = mockRepo.obterPorId(postAtual.getId());

        assertEquals(1, postAtual.getCurtidas().size());
    }

    @When("descurto o post")
    public void descurto_o_post() {
        try {
            postService.descurtirPost(postAtual.getId(), autorId);
            this.postAtual = mockRepo.obterPorId(postAtual.getId());
            operacaoSucesso = true;
        } catch (IllegalArgumentException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    @Then("a operação deve ser bem-sucedida")
    public void a_operacao_deve_ser_bem_sucedida() {
        assertTrue(operacaoSucesso);
    }

    // --- Scenario: Falha ao descurtir post não curtido ---
    @And("que não curtí o post")
    public void que_nao_curti_o_post() {
        // Garantir que autorId não curtiu
        boolean jaCurtiu = postAtual.getCurtidas().stream()
                .anyMatch(c -> c.getContaId().equals(autorId));
        assertFalse(jaCurtiu);
    }

    @When("tento descurtir o post")
    public void tento_descurtir_o_post() {
        try {
            postService.descurtirPost(postAtual.getId(), autorId);
            this.postAtual = mockRepo.obterPorId(postAtual.getId());
            operacaoSucesso = true;
        } catch (IllegalArgumentException | IllegalStateException e) {
            operacaoSucesso = false;
            mensagemErro = e.getMessage();
        }
    }

    // --- Scenario: Buscar por tag sem resultados ---
    @Given("que não existem posts com a tag {string}")
    public void que_nao_existem_posts_com_a_tag(String nomeTag) {
        // Garantir que não há posts com essa tag no repositório
        List<Post> postsComTag = mockRepo.buscarPorTag(nomeTag);
        assertTrue(postsComTag.isEmpty());
    }
}
