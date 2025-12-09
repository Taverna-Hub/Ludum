package org.ludum.dominio.comunidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ludum.dominio.catalogo.biblioteca.entidades.Biblioteca;
import org.ludum.dominio.catalogo.biblioteca.enums.ModeloDeAcesso;
import org.ludum.dominio.catalogo.biblioteca.repositorios.BibliotecaRepository;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.Slug;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.comunidade.review.entidades.Review;
import org.ludum.dominio.comunidade.review.entidades.ReviewId;
import org.ludum.dominio.comunidade.review.enums.StatusReview;
import org.ludum.dominio.comunidade.review.repositorios.ReviewRepository;
import org.ludum.dominio.comunidade.review.services.ReviewService;
import org.ludum.dominio.comunidade.review.observer.NotificacaoDesenvolvedorObserver;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AvaliarJogoFuncionalidade {

    private ReviewService reviewService;
    private MockJogoRepository mockJogoRepository;
    private MockBibliotecaRepository mockBibliotecaRepository;
    private MockReviewRepository mockReviewRepository;

    private ContaId usuarioId;
    private JogoId jogoId;
    private Jogo jogo;
    private Biblioteca biblioteca;
    private Review reviewCriada;
    private Exception excecaoLancada;

    // Mock Repositories
    private static class MockJogoRepository implements JogoRepository {
        private List<Jogo> jogos = new ArrayList<>();

        @Override
        public Jogo obterPorId(JogoId id) {
            return jogos.stream()
                    .filter(j -> j.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void salvar(Jogo jogo) {
            jogos.add(jogo);
        }

        public void adicionarJogo(Jogo jogo) {
            jogos.add(jogo);
        }

        // Métodos não utilizados mas necessários pela interface
        @Override
        public Jogo obterPorSlug(Slug slug) {
            return null;
        }

        @Override
        public boolean existeSlugParaDesenvolvedora(ContaId devId, Slug slug) {
            return false;
        }

        @Override
        public List<Jogo> obterJogosPorTag(TagId tag) {
            return new ArrayList<>();
        }
    }

    private static class MockBibliotecaRepository implements BibliotecaRepository {
        private List<Biblioteca> bibliotecas = new ArrayList<>();

        @Override
        public Biblioteca obterPorJogador(ContaId jogadorId) {
            return bibliotecas.stream()
                    .filter(b -> b.getContaId().equals(jogadorId))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void salvar(Biblioteca biblioteca) {
            bibliotecas.add(biblioteca);
        }

        public void adicionarBiblioteca(Biblioteca biblioteca) {
            bibliotecas.add(biblioteca);
        }
    }

    private static class MockReviewRepository implements ReviewRepository {
        private List<Review> reviews = new ArrayList<>();

        @Override
        public void salvar(Review review) {
            reviews.add(review);
        }

        @Override
        public Review obterPorId(ReviewId id) {
            return reviews.stream()
                    .filter(r -> r.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public List<Review> obterPorJogo(JogoId jogoId, int pagina, int tamanhoPagina) {
            return new ArrayList<>();
        }

        @Override
        public List<Review> obterPorAutor(ContaId autorId, int pagina, int tamanhoPagina) {
            return new ArrayList<>();
        }

        @Override
        public void remover(Review review) {
            reviews.remove(review);
        }

        @Override
        public Optional<Review> obterPorAutorEJogo(ContaId autorId, JogoId jogoId) {
            return reviews.stream()
                    .filter(r -> r.getAutorId().equals(autorId) && r.getJogoId().equals(jogoId))
                    .findFirst();
        }

        @Override
        public List<Review> obterTodasPorJogo(JogoId jogoId) {
            return reviews.stream()
                    .filter(r -> r.getJogoId().equals(jogoId))
                    .collect(java.util.stream.Collectors.toList());
        }

        public List<Review> getReviews() {
            return new ArrayList<>(reviews);
        }
    }

    @Before
    public void setup() {
        this.mockJogoRepository = new MockJogoRepository();
        this.mockBibliotecaRepository = new MockBibliotecaRepository();
        this.mockReviewRepository = new MockReviewRepository();

        this.reviewService = new ReviewService(
                mockReviewRepository,
                mockJogoRepository,
                mockBibliotecaRepository
        );
        
        this.reviewService.adicionarObservador(new NotificacaoDesenvolvedorObserver());

        this.usuarioId = new ContaId("usuario-123");
        this.jogoId = new JogoId("jogo-456");
        this.jogo = null;
        this.biblioteca = null;
        this.reviewCriada = null;
        this.excecaoLancada = null;
    }

    public AvaliarJogoFuncionalidade() {
    }

    // ========== Regra 1: Jogo deve existir no sistema ==========

    @Given("um {string} {string} no sistema")
    public void um_jogo_existe_ou_nao_no_sistema(String entidade, String estado) {
        if (estado.equals("existe")) {
            criarJogoPublicado();
        }
        // Se "não existe", não fazemos nada (jogo fica null)
    }

    @And("o jogo {string} publicado")
    public void o_jogo_esta_ou_nao_publicado(String estado) {
        if (estado.equals("está") && jogo != null) {
            // Jogo já está publicado pelo helper
        }
    }

    @And("o usuário {string} o jogo na biblioteca")
    public void o_usuario_possui_ou_nao_jogo_na_biblioteca(String estado) {
        if (estado.equals("possui")) {
            criarBibliotecaComJogo();
        } else if (estado.equals("não possui")) {
            biblioteca = new Biblioteca(usuarioId);
            mockBibliotecaRepository.adicionarBiblioteca(biblioteca);
        }
    }

    @When("o usuário avalia o jogo")
    public void o_usuario_avalia_o_jogo() {
        try {
            reviewService.avaliarJogo(
                    jogoId,
                    usuarioId,
                    5,
                    "Excelente jogo!",
                    "Adorei a jogabilidade e os gráficos",
                    true
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("o sistema salva a avaliação")
    public void o_sistema_salva_a_avaliacao() {
        assertEquals(1, mockReviewRepository.getReviews().size());
        Review review = mockReviewRepository.getReviews().getFirst();
        assertNotNull(review);
        assertEquals(jogoId, review.getJogoId());
        assertEquals(usuarioId, review.getAutorId());
    }

    @When("o usuário tenta avaliar o jogo")
    public void o_usuario_tenta_avaliar_o_jogo() {
        try {
            reviewService.avaliarJogo(
                    jogoId,
                    usuarioId,
                    5,
                    "Teste",
                    "Texto de teste",
                    true
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("o sistema informa que o jogo avaliado não foi encontrado")
    public void o_sistema_informa_que_jogo_nao_foi_encontrado() {
        assertNotNull(excecaoLancada);
        assertEquals("Jogo não encontrado.", excecaoLancada.getMessage());
    }

    // ========== Regra 2: Jogo deve estar publicado ==========

    @Given("um {string} {string} publicado")
    public void um_jogo_esta_ou_nao_publicado(String entidade, String estado) {
        if (estado.equals("está")) {
            criarJogoPublicado();
        } else if (estado.equals("não está")) {
            criarJogoNaoPublicado();
        }
    }

    @Then("o sistema informa que o jogo avaliado não está publicado")
    public void o_sistema_informa_que_jogo_nao_esta_publicado() {
        assertNotNull(excecaoLancada);
        assertEquals("Não é possível avaliar um jogo que não está publicado.", excecaoLancada.getMessage());
    }

    // ========== Regra 3: Usuário deve possuir o jogo na biblioteca ==========

    @Then("o sistema informa que o usuário precisa ter o jogo na biblioteca")
    public void o_sistema_informa_que_precisa_ter_jogo_na_biblioteca() {
        assertNotNull(excecaoLancada);
        assertEquals("Você precisa ter o jogo na sua biblioteca para avaliá-lo.", excecaoLancada.getMessage());
    }

    // ========== Regra 4: Usuário não pode avaliar o mesmo jogo duas vezes ==========

    @And("o usuário {string} o jogo anteriormente")
    public void o_usuario_avaliou_ou_nao_jogo_anteriormente(String estado) {
        if (estado.equals("já avaliou")) {
            // Cria uma review anterior
            Review reviewAnterior = new Review(
                    new ReviewId("review-anterior"),
                    jogoId,
                    usuarioId,
                    4,
                    "Review anterior",
                    "Já avaliei antes",
                    new java.util.Date(),
                    true,
                    StatusReview.PUBLICADO
            );
            mockReviewRepository.salvar(reviewAnterior);
        }
        // Se "não avaliou", não fazemos nada
    }

    @When("o usuário tenta avaliar o jogo novamente")
    public void o_usuario_tenta_avaliar_jogo_novamente() {
        o_usuario_tenta_avaliar_o_jogo();
    }

    @Then("o sistema informa que o jogo avaliado já foi avaliado")
    public void o_sistema_informa_que_jogo_ja_foi_avaliado() {
        assertNotNull(excecaoLancada);
        assertEquals("Você já avaliou este jogo. Use a função de editar para alterar sua avaliação.", excecaoLancada.getMessage());
    }

    // ========== Regra 5: Review deve conter título válido ==========

    @When("o usuário avalia o jogo com título preenchido")
    public void o_usuario_avalia_jogo_com_titulo_preenchido() {
        o_usuario_avalia_o_jogo();
    }

    @When("o usuário tenta avaliar o jogo sem título")
    public void o_usuario_tenta_avaliar_jogo_sem_titulo() {
        try {
            reviewService.avaliarJogo(
                    jogoId,
                    usuarioId,
                    5,
                    "",  // Título vazio
                    "Texto válido",
                    true
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("o sistema informa que o título não pode estar vazio")
    public void o_sistema_informa_que_titulo_nao_pode_estar_vazio() {
        assertNotNull(excecaoLancada);
        assertEquals("O título da review não pode estar vazio.", excecaoLancada.getMessage());
    }

    // ========== Regra 6: Review deve conter texto válido ==========

    @When("o usuário avalia o jogo com texto preenchido")
    public void o_usuario_avalia_jogo_com_texto_preenchido() {
        o_usuario_avalia_o_jogo();
    }

    @When("o usuário tenta avaliar o jogo sem texto")
    public void o_usuario_tenta_avaliar_jogo_sem_texto() {
        try {
            reviewService.avaliarJogo(
                    jogoId,
                    usuarioId,
                    5,
                    "Título válido",
                    "",  // Texto vazio
                    true
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("o sistema informa que o texto não pode estar vazio")
    public void o_sistema_informa_que_texto_nao_pode_estar_vazio() {
        assertNotNull(excecaoLancada);
        assertEquals("O texto da review não pode estar vazio.", excecaoLancada.getMessage());
    }

    // ========== Regra 7: Nota deve estar entre 0 e 5 ==========

    @When("o usuário avalia o jogo com nota entre {int} e {int}")
    public void o_usuario_avalia_jogo_com_nota_valida(int min, int max) {
        o_usuario_avalia_o_jogo();
    }

    @When("o usuário tenta avaliar o jogo com nota inválida")
    public void o_usuario_tenta_avaliar_jogo_com_nota_invalida() {
        try {
            reviewService.avaliarJogo(
                    jogoId,
                    usuarioId,
                    10,  // Nota inválida
                    "Título",
                    "Texto",
                    true
            );
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("o sistema informa que a nota deve estar entre {int} e {int}")
    public void o_sistema_informa_que_nota_deve_estar_entre(int min, int max) {
        assertNotNull(excecaoLancada);
        assertEquals("A nota deve estar entre 0 e 5.", excecaoLancada.getMessage());
    }

    // ========== Métodos Helper ==========

    private void criarJogoPublicado() {
        try {
            Tag tagAventura = new Tag(new TagId("tag-aventura"), "Aventura");
            List<Tag> tagsDoJogo = new ArrayList<>();
            tagsDoJogo.add(tagAventura);

            jogo = new Jogo(
                    jogoId,
                    new ContaId("dev-123"),
                    "Aventura Cósmica",
                    "Um jogo incrível de aventura espacial",
                    new URL("http://exemplo.com/capa.jpg"),
                    tagsDoJogo, // Passando a lista de tags (assumindo que este é o 6º parâmetro)
                    false,
                    LocalDate.now()
            );

            jogo.adicionarScreenshot(new URL("http://exemplo.com/screenshot1.jpg"));

            jogo.publicar();

            mockJogoRepository.adicionarJogo(jogo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar jogo", e);
        }
    }

    private void criarJogoNaoPublicado() {
        try {
            jogo = new Jogo(
                    jogoId,
                    new ContaId("dev-123"),
                    "Jogo em Desenvolvimento",
                    "Um jogo ainda não publicado",
                    new URL("http://exemplo.com/capa.jpg"),
                    new ArrayList<>(),
                    false,
                    LocalDate.now()
            );
            // Status já é AGUARDANDO_VALIDACAO por padrão
            mockJogoRepository.adicionarJogo(jogo);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar jogo não publicado", e);
        }
    }

    private void criarBibliotecaComJogo() {
        biblioteca = new Biblioteca(usuarioId);
        biblioteca.adicionarJogo(ModeloDeAcesso.PAGO, jogoId);
        mockBibliotecaRepository.adicionarBiblioteca(biblioteca);
    }
}

