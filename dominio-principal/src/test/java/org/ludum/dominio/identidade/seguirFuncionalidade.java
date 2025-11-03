package org.ludum.dominio.identidade;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.catalogo.jogo.entidades.Slug;
import org.ludum.dominio.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.dominio.catalogo.tag.TagRepository;
import org.ludum.dominio.catalogo.tag.entidades.Tag;
import org.ludum.dominio.catalogo.tag.entidades.TagId;
import org.ludum.dominio.identidade.bloqueio.entities.Bloqueio;
import org.ludum.dominio.identidade.bloqueio.entities.BloqueioId;
import org.ludum.dominio.identidade.bloqueio.repositories.BloqueioRepository;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.enums.StatusConta;
import org.ludum.dominio.identidade.conta.enums.TipoConta;
import org.ludum.dominio.identidade.conta.repositories.ContaRepository;
import org.ludum.dominio.identidade.seguimento.entities.AlvoId;
import org.ludum.dominio.identidade.seguimento.entities.Seguimento;
import org.ludum.dominio.identidade.seguimento.entities.SeguimentoId;
import org.ludum.dominio.identidade.seguimento.enums.TipoAlvo;
import org.ludum.dominio.identidade.seguimento.repositories.SeguimentoRepository;
import org.ludum.dominio.identidade.seguimento.services.RelacionamentoService;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class seguirFuncionalidade {

    private RelacionamentoService relacionamentoService;
    private MockSeguimentoRepository mockSeguimentoRepository;
    private MockContaRepository mockContaRepository;
    private MockBloqueioRepository mockBloqueioRepository;
    private MockJogoRepository mockJogoRepository;
    private MockTagRepository mockTagRepository;

    private ContaId usuarioId;
    private AlvoId alvoId;
    private TipoAlvo tipoAlvo;
    private Conta usuario;
    private Conta contaAlvo;
    private Jogo jogo;
    private Tag tag;
    private Exception excecaoLancada;

    // Mock Repositories
    private static class MockSeguimentoRepository implements SeguimentoRepository {
        private List<Seguimento> seguimentos = new ArrayList<>();

        @Override
        public void salvar(Seguimento seguimento) {
            seguimentos.add(seguimento);
        }

        @Override
        public void remover(Seguimento seguimento) {
            seguimentos.remove(seguimento);
        }

        @Override
        public Optional<Seguimento> obter(ContaId seguidorId, AlvoId seguidoId) {
            return seguimentos.stream()
                    .filter(s -> s.getSeguidorId().getValue().equals(seguidorId.getValue()) 
                              && s.getSeguidoId().getValue().equals(seguidoId.getValue()))
                    .findFirst();
        }

        @Override
        public List<Seguimento> obterSeguidoresDe(AlvoId seguidoId) {
            return new ArrayList<>();
        }

        @Override
        public List<Seguimento> obterSeguidosPor(ContaId seguidorId) {
            return new ArrayList<>();
        }

        public List<Seguimento> getSeguimentos() {
            return new ArrayList<>(seguimentos);
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

    private static class MockBloqueioRepository implements BloqueioRepository {
        private List<Bloqueio> bloqueios = new ArrayList<>();

        @Override
        public void salvar(Bloqueio bloqueio) {
            bloqueios.add(bloqueio);
        }

        @Override
        public void remover(Bloqueio bloqueio) {
            bloqueios.remove(bloqueio);
        }

        @Override
        public Optional<Bloqueio> buscar(ContaId bloqueadorId, ContaId alvoId) {
            return bloqueios.stream()
                    .filter(b -> b.getBloqueadorId().getValue().equals(bloqueadorId.getValue()) 
                              && b.getBloqueadoId().getValue().equals(alvoId.getValue()))
                    .findFirst();
        }

        public void adicionarBloqueio(Bloqueio bloqueio) {
            bloqueios.add(bloqueio);
        }
    }

    private static class MockJogoRepository implements JogoRepository {
        private List<Jogo> jogos = new ArrayList<>();

        @Override
        public Jogo obterPorId(JogoId id) {
            return jogos.stream()
                    .filter(j -> j.getId().getValue().equals(id.getValue()))
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
            return jogos.stream()
                    .filter(j -> j.getTags().stream()
                            .anyMatch(t -> t.getId().getValue().equals(tag.getValue())))
                    .collect(java.util.stream.Collectors.toList());
        }
    }

    private static class MockTagRepository implements TagRepository {
        private List<Tag> tags = new ArrayList<>();

        @Override
        public void salvar(Tag tag) {
            tags.add(tag);
        }

        @Override
        public Tag obterPorId(TagId id) {
            return tags.stream()
                    .filter(t -> t.getId().getValue().equals(id.getValue()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public Tag obterPorNome(String nome) {
            return null;
        }

        @Override
        public List<Tag> obterTodas() {
            return new ArrayList<>(tags);
        }

        @Override
        public void remover(Tag tag) {

        }

        public void adicionarTag(Tag tag) {
            tags.add(tag);
        }
    }

    @Before
    public void setup() {
        this.mockSeguimentoRepository = new MockSeguimentoRepository();
        this.mockContaRepository = new MockContaRepository();
        this.mockBloqueioRepository = new MockBloqueioRepository();
        this.mockJogoRepository = new MockJogoRepository();
        this.mockTagRepository = new MockTagRepository();

        this.relacionamentoService = new RelacionamentoService(
                mockSeguimentoRepository,
                mockContaRepository,
                mockBloqueioRepository,
                mockJogoRepository,
                mockTagRepository
        );

        this.usuarioId = new ContaId("usuario-123");
        this.alvoId = null;
        this.tipoAlvo = TipoAlvo.CONTA;
        this.usuario = null;
        this.contaAlvo = null;
        this.jogo = null;
        this.tag = null;
        this.excecaoLancada = null;
    }

    public seguirFuncionalidade() {
    }

    // ========== Regra 1: Não é permitido seguir a si próprio ==========

    @Given("um {string} {string} e está ativo")
    public void um_usuario_existe_e_esta_ativo(String entidade, String estado) {
        if (estado.equals("existe")) {
            criarUsuarioAtivo();
        }
    }

    @And("o {string} {string} o próprio usuário")
    public void o_alvo_e_ou_nao_proprio_usuario(String entidade, String estado) {
        if (estado.equals("é")) {
            // Alvo é o próprio usuário
            alvoId = new AlvoId(usuarioId.getValue());
        } else if (estado.equals("não é")) {
            // Alvo é outra conta
            contaAlvo = criarContaAlvo();
            alvoId = new AlvoId(contaAlvo.getId().getValue());

        }
    }

    @When("o usuário segue o alvo")
    public void o_usuario_segue_o_alvo() {
        try {
            relacionamentoService.seguirAlvo(usuarioId, alvoId, TipoAlvo.CONTA);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("o sistema cria o seguimento")
    public void o_sistema_cria_o_seguimento() {
        assertEquals(1, mockSeguimentoRepository.getSeguimentos().size());
        Seguimento seguimento = mockSeguimentoRepository.getSeguimentos().getFirst();
        assertNotNull(seguimento);
        assertEquals(usuarioId.getValue(), seguimento.getSeguidorId().getValue());
        assertEquals(alvoId.getValue(), seguimento.getSeguidoId().getValue());
    }

    @When("o usuário tenta seguir o alvo")
    public void o_usuario_tenta_seguir_o_alvo() {
        try {
            relacionamentoService.seguirAlvo(usuarioId, alvoId, TipoAlvo.CONTA);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Then("o sistema informa que não é possível seguir a si mesmo")
    public void o_sistema_informa_que_nao_pode_seguir_si_mesmo() {
        assertNotNull(excecaoLancada);
        assertEquals("Não é possível seguir a si mesmo.", excecaoLancada.getMessage());
    }

    // ========== Regra 2: Seguidor deve existir e estar ativo ==========

    @Given("um {string} {string} ativo")
    public void um_usuario_esta_ou_nao_ativo(String entidade, String estado) {
        if (estado.equals("está")) {
            criarUsuarioAtivo();
        } else if (estado.equals("não está")) {
            criarUsuarioInativo();
        }
    }

    @And("o {string} {string}")
    public void o_alvo_existe(String entidade, String estado) {
        if (estado.equals("existe")) {
            contaAlvo = criarContaAlvo();
            alvoId = new AlvoId(contaAlvo.getId().getValue());
        }
    }

    @Then("o sistema informa que a conta não está ativa")
    public void o_sistema_informa_que_conta_nao_esta_ativa() {
        assertNotNull(excecaoLancada);
        assertEquals("Conta do seguidor não está ativa.", excecaoLancada.getMessage());
    }

    // ========== Regra 3: Alvo do tipo CONTA deve existir ==========

    @And("uma {string} {string}")
    public void uma_entidade_existe_ou_nao(String entidade, String estado) {
        // Método genérico que trata conta, tag e desenvolvedora
        if (entidade.equals("conta alvo")) {
            if (estado.equals("existe")) {
                contaAlvo = criarContaAlvo();
                alvoId = new AlvoId(contaAlvo.getId().getValue());
            } else if (estado.equals("não existe")) {
                alvoId = new AlvoId("conta-inexistente-999");
            }
        } else if (entidade.equals("tag")) {
            if (estado.equals("existe")) {
                tag = criarTag();
                alvoId = new AlvoId(tag.getId().getValue());
                tipoAlvo = TipoAlvo.TAG;
            }
        } else if (entidade.equals("desenvolvedora")) {
            if (estado.equals("existe")) {
                contaAlvo = criarDesenvolvedora();
                alvoId = new AlvoId(contaAlvo.getId().getValue());
                tipoAlvo = TipoAlvo.DESENVOLVEDORA;
            } else if (estado.equals("não existe")) {
                alvoId = new AlvoId("dev-inexistente-999");
                tipoAlvo = TipoAlvo.DESENVOLVEDORA;
            }
        }
    }

    @When("o usuário segue a conta")
    public void o_usuario_segue_a_conta() {
        try {
            relacionamentoService.seguirAlvo(usuarioId, alvoId, TipoAlvo.CONTA);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o usuário tenta seguir a conta")
    public void o_usuario_tenta_seguir_a_conta() {
        o_usuario_segue_a_conta();
    }

    @Then("o sistema informa que o usuário alvo não foi encontrado")
    public void o_sistema_informa_que_usuario_alvo_nao_foi_encontrado() {
        assertNotNull(excecaoLancada);
        assertEquals("Usuário alvo não encontrado.", excecaoLancada.getMessage());
    }

    // ========== Regra 4: Não pode existir seguimento duplicado ==========

    @And("o usuário {string} o alvo")
    public void o_usuario_ja_segue_ou_nao_o_alvo(String estado) {
        if (estado.equals("já segue")) {
            // Cria seguimento anterior
            Seguimento seguimentoAnterior = new Seguimento(
                    new SeguimentoId("seguimento-anterior"),
                    usuarioId,
                    alvoId,
                    TipoAlvo.CONTA
            );
            mockSeguimentoRepository.salvar(seguimentoAnterior);
        }
        // Se "não segue", não fazemos nada
    }

    @When("o usuário tenta seguir o alvo novamente")
    public void o_usuario_tenta_seguir_o_alvo_novamente() {
        o_usuario_tenta_seguir_o_alvo();
    }

    @Then("o sistema informa que já existe seguimento entre estas entidades")
    public void o_sistema_informa_que_ja_existe_seguimento() {
        assertNotNull(excecaoLancada);
        assertEquals("Já existe um seguimento entre estas entidades.", excecaoLancada.getMessage());
    }

    // ========== Regra 5: Alvo do tipo JOGO deve existir ==========

    @And("um {string} {string}")
    public void um_jogo_existe_ou_nao(String entidade, String estado) {
        if (estado.equals("existe")) {
            jogo = criarJogo();
            alvoId = new AlvoId(jogo.getId().getValue());
            tipoAlvo = TipoAlvo.JOGO;
        } else if (estado.equals("não existe")) {
            alvoId = new AlvoId("jogo-inexistente-999");
            tipoAlvo = TipoAlvo.JOGO;
        }
    }

    @When("o usuário segue o jogo")
    public void o_usuario_segue_o_jogo() {
        try {
            relacionamentoService.seguirAlvo(usuarioId, alvoId, TipoAlvo.JOGO);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o usuário tenta seguir o jogo")
    public void o_usuario_tenta_seguir_o_jogo() {
        o_usuario_segue_o_jogo();
    }

    @Then("o sistema informa que o jogo não foi encontrado")
    public void o_sistema_informa_que_jogo_nao_foi_encontrado() {
        assertNotNull(excecaoLancada);
        assertEquals("Jogo não encontrado.", excecaoLancada.getMessage());
    }

    // ========== Regra 6: Alvo do tipo TAG deve existir e ter jogos ==========

    @And("a tag {string} jogos atrelados")
    public void a_tag_possui_ou_nao_jogos(String estado) {
        if (estado.equals("possui")) {
            // Cria jogo com a tag
            jogo = criarJogoComTag(tag);
        }
        // Se "não possui", não adicionamos jogos
    }

    @When("o usuário segue a tag")
    public void o_usuario_segue_a_tag() {
        try {
            relacionamentoService.seguirAlvo(usuarioId, alvoId, TipoAlvo.TAG);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o usuário tenta seguir a tag")
    public void o_usuario_tenta_seguir_a_tag() {
        o_usuario_segue_a_tag();
    }

    @Then("o sistema informa que a tag não está disponível")
    public void o_sistema_informa_que_tag_nao_esta_disponivel() {
        assertNotNull(excecaoLancada);
        assertEquals("Tag não disponivel, nenhum jogo atrelado á ela", excecaoLancada.getMessage());
    }

    // ========== Regra 7: Usuário bloqueado não pode seguir quem o bloqueou ==========

    @And("o usuário {string} pela conta alvo")
    public void o_usuario_esta_ou_nao_bloqueado_pela_conta_alvo(String estado) {
        if (estado.equals("está bloqueado")) {
            // Conta alvo bloqueou o usuário
            Bloqueio bloqueio = new Bloqueio(
                    new BloqueioId("bloqueio-123"),
                    contaAlvo.getId(),
                    usuarioId
            );
            mockBloqueioRepository.adicionarBloqueio(bloqueio);
        }
        // Se "não está bloqueado", não fazemos nada
    }

    @Then("o sistema informa que está bloqueado por este usuário")
    public void o_sistema_informa_que_esta_bloqueado() {
        assertNotNull(excecaoLancada);
        assertEquals("Você está bloqueado por este usuário.", excecaoLancada.getMessage());
    }

    // ========== Regra 8: Usuário não pode seguir quem bloqueou ==========

    @And("o usuário {string} a conta alvo")
    public void o_usuario_bloqueou_ou_nao_conta_alvo(String estado) {
        if (estado.equals("bloqueou")) {
            // Usuário bloqueou a conta alvo
            Bloqueio bloqueio = new Bloqueio(
                    new BloqueioId("bloqueio-456"),
                    usuarioId,
                    contaAlvo.getId()
            );
            mockBloqueioRepository.adicionarBloqueio(bloqueio);
        }
        // Se "não bloqueou", não fazemos nada
    }

    @Then("o sistema informa que bloqueou este usuário")
    public void o_sistema_informa_que_bloqueou_usuario() {
        assertNotNull(excecaoLancada);
        assertEquals("Você bloqueou este usuário.", excecaoLancada.getMessage());
    }

    // ========== Regra 9: Alvo do tipo DESENVOLVEDORA deve existir ==========

    @When("o usuário segue a desenvolvedora")
    public void o_usuario_segue_a_desenvolvedora() {
        try {
            relacionamentoService.seguirAlvo(usuarioId, alvoId, TipoAlvo.DESENVOLVEDORA);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @When("o usuário tenta seguir a desenvolvedora")
    public void o_usuario_tenta_seguir_a_desenvolvedora() {
        o_usuario_segue_a_desenvolvedora();
    }

    // ========== Métodos Helper ==========

    private void criarUsuarioAtivo() {
        usuario = new Conta(
                usuarioId,
                "João Silva",
                "hashedPassword123",
                TipoConta.JOGADOR,
                StatusConta.ATIVA
        );
        mockContaRepository.adicionarConta(usuario);
    }

    private void criarUsuarioInativo() {
        usuario = new Conta(
                usuarioId,
                "João Silva",
                "hashedPassword123",
                TipoConta.JOGADOR,
                StatusConta.INATIVA
        );
        mockContaRepository.adicionarConta(usuario);
    }

    private Conta criarContaAlvo() {
        Conta conta = new Conta(
                new ContaId("conta-alvo-456"),
                "Maria Santos",
                "hashedPassword456",
                TipoConta.JOGADOR,
                StatusConta.ATIVA
        );
        mockContaRepository.adicionarConta(conta);
        return conta;
    }

    private Conta criarDesenvolvedora() {
        Conta dev = new Conta(
                new ContaId("dev-789"),
                "Studio Games",
                "hashedPassword789",
                TipoConta.DESENVOLVEDORA,
                StatusConta.ATIVA
        );
        mockContaRepository.adicionarConta(dev);
        return dev;
    }

    private Jogo criarJogo() {
        try {
            Jogo novoJogo = new Jogo(
                    new JogoId("jogo-123"),
                    new ContaId("dev-123"),
                    "Aventura Espacial",
                    "Um jogo de aventura incrível",
                    new URL("http://exemplo.com/capa.jpg"),
                    new ArrayList<>(),
                    false,
                    LocalDate.now()
            );
            mockJogoRepository.adicionarJogo(novoJogo);
            return novoJogo;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar jogo", e);
        }
    }

    private Tag criarTag() {
        Tag novaTag = new Tag(
                new TagId("tag-123"),
                "Aventura"
        );
        mockTagRepository.adicionarTag(novaTag);
        return novaTag;
    }

    private Jogo criarJogoComTag(Tag tag) {
        try {
            List<Tag> tags = new ArrayList<>();
            tags.add(tag);
            
            Jogo novoJogo = new Jogo(
                    new JogoId("jogo-com-tag-456"),
                    new ContaId("dev-123"),
                    "Jogo com Tag",
                    "Um jogo categorizado",
                    new URL("http://exemplo.com/capa2.jpg"),
                    tags,
                    false,
                    LocalDate.now()
            );
            mockJogoRepository.adicionarJogo(novoJogo);
            return novoJogo;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar jogo com tag", e);
        }
    }
}
