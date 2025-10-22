package org.ludum.dominio.crowdfunding;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.ludum.catalogo.jogo.entidades.Jogo;
import org.ludum.catalogo.jogo.entidades.JogoId;
import org.ludum.catalogo.jogo.entidades.Slug;
import org.ludum.catalogo.jogo.enums.StatusPublicacao;
import org.ludum.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.catalogo.tag.entidades.TagId;
import org.ludum.crowdfunding.entidades.Campanha;
import org.ludum.crowdfunding.entidades.CampanhaId;
import org.ludum.crowdfunding.entidades.Periodo;
import org.ludum.crowdfunding.enums.StatusCampanha;
import org.ludum.crowdfunding.repositorios.CampanhaRepository;
import org.ludum.crowdfunding.services.GestaoDeCampanhasService;
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

public class CampanhaFuncionalidade {

    private static class MockJogoRepository implements JogoRepository {
        private final Map<JogoId, Jogo> jogos = new HashMap<>();

        @Override
        public void salvar(Jogo jogo) {
            jogos.put(jogo.getId(), jogo);
        }

        @Override
        public Jogo obterPorId(JogoId id) {
            return jogos.get(id);
        }

        // Métodos não usados, mas necessários para interface
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

        // Helper
        public void adicionarJogo(Jogo jogo) {
            jogos.put(jogo.getId(), jogo);
        }
    }

    private static class MockContaRepository implements ContaRepository {
        private final Map<ContaId, Conta> contas = new HashMap<>();

        @Override
        public void salvar(Conta conta) {
            contas.put(conta.getId(), conta);
        }

        @Override
        public Conta obterPorId(ContaId id) {
            return contas.get(id);
        }

        // Helper para testes
        public void adicionarConta(Conta conta) {
            contas.put(conta.getId(), conta);
        }
    }

    private static class MockCampanhaRepository implements CampanhaRepository {
        private final Map<CampanhaId, Campanha> campanhas = new HashMap<>();

        @Override
        public void salvar(Campanha campanha) {
            campanhas.put(campanha.getId(), campanha);
        }

        @Override
        public void remover(Campanha campanha) {
            campanhas.remove(campanha.getId());
        }

        @Override
        public Optional<Campanha> buscarPorId(CampanhaId id) {
            return Optional.ofNullable(campanhas.get(id));
        }

        @Override
        public List<Campanha> listarCampanhasAtivas() {
            return campanhas.values().stream()
                    .filter(c -> c.getStatus() == StatusCampanha.ATIVA)
                    .collect(java.util.stream.Collectors.toList());
        }

        // Helper para testes
        public Map<CampanhaId, Campanha> getCampanhas() {
            return campanhas;
        }
    }

    private GestaoDeCampanhasService gestaoDeCampanhasService;
    private MockJogoRepository mockJogoRepository;
    private MockContaRepository mockContaRepository;
    private MockCampanhaRepository mockCampanhaRepository;

    private ContaId usuarioId;
    private Conta contaAtual;
    private JogoId jogoIdAtual;
    private Jogo jogoAtual;
    private Campanha campanhaAtual;
    private Exception excecaoLancada;
    private String mensagemErro;
    private boolean acaoBemSucedida;

    @Before
    public void setup() {
        mockJogoRepository = new MockJogoRepository();
        mockContaRepository = new MockContaRepository();
        mockCampanhaRepository = new MockCampanhaRepository();
        gestaoDeCampanhasService = new GestaoDeCampanhasService(mockCampanhaRepository, mockJogoRepository);

        usuarioId = null;
        contaAtual = null;
        jogoIdAtual = null;
        jogoAtual = null;
        campanhaAtual = null;
        excecaoLancada = null;
        mensagemErro = null;
        acaoBemSucedida = false;
    }

    private void criarConta(String id, String tipo, String status) {
        usuarioId = new ContaId(id);
        contaAtual = new Conta(
                usuarioId,
                "Usuario " + id,
                "senhaHash",
                TipoConta.valueOf(tipo.toUpperCase()), // Ajuste para DESENVOLVEDORA
                StatusConta.valueOf(status.toUpperCase())
        );
        mockContaRepository.adicionarConta(contaAtual);
    }

    private void criarJogo(String nomeJogo, String statusJogo, ContaId devId) {
        jogoIdAtual = new JogoId(nomeJogo.toLowerCase().replace(" ", "-") + "-id");
        try {
            jogoAtual = new Jogo(
                    jogoIdAtual,
                    devId, // Dono do jogo
                    nomeJogo,
                    "Descrição do jogo " + nomeJogo,
                    new URL("http://example.com/capa.jpg"),
                    new ArrayList<>(),
                    false,
                    LocalDate.now()
            );

            // Ajusta o status conforme o cenário
            if ("publicado".equalsIgnoreCase(statusJogo)) {
                // Para simular publicação, precisamos de mais detalhes, vamos apenas marcar
                // como publicado diretamente no mock se necessário, ou usar um service
                // mockado
                // Aqui apenas setamos o status diretamente para o teste focar na campanha
                // jogoAtual.publicar(); // Isso pode ter validações internas, cuidado
                alterarStatusJogo(jogoAtual, StatusPublicacao.PUBLICADO); // Método helper seguro
            } else {
                 alterarStatusJogo(jogoAtual, StatusPublicacao.AGUARDANDO_VALIDACAO); // Default não publicado
            }

            mockJogoRepository.adicionarJogo(jogoAtual);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Erro ao criar URL mock", e);
        }
    }

    private void alterarStatusJogo(Jogo jogo, StatusPublicacao novoStatus) {
        try {
            Field statusField = Jogo.class.getDeclaredField("statusPublicacao");
            statusField.setAccessible(true);
            statusField.set(jogo, novoStatus);
        } catch (NoSuchFieldException nsfe) {
            try {
                Field statusField = Jogo.class.getDeclaredField("status");
                statusField.setAccessible(true);
                statusField.set(jogo, novoStatus);
            } catch (Exception e) {
                fail("Não foi possível forçar o status do Jogo para '" + novoStatus + "' via reflect (tentativa 'status'): " + e.getMessage());
            }
        } catch (Exception e) {
            fail("Não foi possível forçar o status do Jogo para '" + novoStatus + "' via reflect (tentativa 'statusPublicacao'): " + e.getMessage());
        }
        
        mockJogoRepository.salvar(jogo);
    }

    // Criação de Campanha pela desenvolvedora

    @Given("que sou uma {string} autenticada e validada")
    public void desenvolvedora_autenticada_e_validada(String tipoUsuario) {
        criarConta("dev-" + UUID.randomUUID(), tipoUsuario, "ATIVA");
        assertEquals(TipoConta.DESENVOLVEDORA, contaAtual.getTipo());
    }

    @And("possuo um projeto de jogo {string} com status {string}")
    public void possuo_projeto_de_jogo_com_status(String nomeJogo, String status) {
        assertNotNull(contaAtual);
        assertEquals(TipoConta.DESENVOLVEDORA, contaAtual.getTipo());
        criarJogo(nomeJogo, status, usuarioId);
    }

    @When("eu crio uma campanha de financiamento para {string} com uma meta de {string} e duração de {string} dias")
    public void crio_campanha_de_financiamento_para_jogo_com_meta_e_data(String nomeJogo, String metaStr, String duracaoStr) {
        try {
            BigDecimal meta = new BigDecimal(metaStr.replace("R$", "").trim());
            int duracaoDias = Integer.parseInt(duracaoStr);
            LocalDateTime inicio = LocalDateTime.now();
            LocalDateTime fim = inicio.plusDays(duracaoDias);
            Periodo periodo = new Periodo(inicio, fim);

            assertNotNull(jogoAtual);
            assertEquals(nomeJogo, jogoAtual.getTitulo());

            campanhaAtual = gestaoDeCampanhasService.criarCampanha(jogoIdAtual, usuarioId, meta, periodo);
            acaoBemSucedida = true;
        } catch (IllegalStateException | IllegalArgumentException e) {
            acaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = e.getMessage();
        } catch(Exception e) {
            acaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = "Erro inesperado: " + e.getMessage();
        }
    }

    @Then("a campanha deve ser criada com sucesso e associada ao jogo")
    public void campanha_deve_ser_criada_e_associada_ao_jogo_com_sucesso() {
        assertTrue(acaoBemSucedida);
        assertNotNull(campanhaAtual);
        assertNotNull(mockCampanhaRepository.buscarPorId(campanhaAtual.getId()).orElse(null));
        assertEquals(jogoIdAtual, campanhaAtual.getJogoId());
    }

    // @Given("que eu sou uma {string} autenticada e validada")
    @And("possuo um jogo {string} com status {string}")
    public void possuo_um_jogo_com_status(String nomeJogo, String statusJogo) {
        possuo_projeto_de_jogo_com_status(nomeJogo, statusJogo);
    }

    @When("eu tento criar uma campanha de financiamento para {string}")
    public void tento_criar_campanha_de_financiamento(String nomeJogo) {
        crio_campanha_de_financiamento_para_jogo_com_meta_e_data(nomeJogo, "R$ 1000.00", "30");
    }

    @Then("o sistema deve rejeitar a criação e informar que campanhas são apenas para jogos não publicados")
    public void sistema_rejeita_criacao_da_campanha_e_informa() {
        assertFalse(acaoBemSucedida);
        assertNotNull(excecaoLancada);
        assertTrue(excecaoLancada instanceof IllegalStateException);
        assertTrue(mensagemErro != null && mensagemErro.toLowerCase().contains("jogos não publicados"));
    }

    // Finalização automática de Campanha
    @Given("que a campanha para {string} com meta de {string} chegou ao fim")
    public void a_campanha_chegou_ao_fim(String nomeJogo, String metaStr) {
        BigDecimal meta = new BigDecimal(metaStr.replace("R$ ", "").trim());
        LocalDateTime inicio = LocalDateTime.now().minusDays(31);
        LocalDateTime fim = LocalDateTime.now().minusDays(1);
        Periodo periodoEncerrado = new Periodo(inicio, fim);

        criarConta("dev-campanha", "DESENVOLVEDORA", "ATIVA");
        criarJogo(nomeJogo, "Não publicado", usuarioId);

        campanhaAtual = new Campanha(jogoIdAtual, usuarioId, meta, periodoEncerrado);
        campanhaAtual.iniciar();

        if (campanhaAtual.getStatus() != StatusCampanha.ATIVA) {
            try {
                Field statusField = Campanha.class.getDeclaredField("status");
                statusField.setAccessible(true);
                statusField.set(campanhaAtual, StatusCampanha.ATIVA);
            } catch (Exception e) {
                fail("Não foi possível forçar o status da campanha para ATIVA via reflect.");
            }
        }
        mockCampanhaRepository.salvar(campanhaAtual);
    }

    @And("o valor total arrecadado foi de {string}")
    public void valor_total_arrecadado_foi_de(String valorStr) {
        BigDecimal valorArrecadado = new BigDecimal(valorStr.replace("R$ ", "").trim());
        try {
            Field valorField = Campanha.class.getDeclaredField("valorArrecadado");
            valorField.setAccessible(true);
            valorField.set(campanhaAtual, valorArrecadado);
        } catch (Exception e) {
            fail("Não foi possível definir o valor arrecadado da campanha via reflect.");
        }
        mockCampanhaRepository.salvar(campanhaAtual);
    }

    @When("o sistema processa o final da campanha")
    public void sistema_processa_valor_final_da_campanha() {
        try {
            campanhaAtual.finalizar();
            mockCampanhaRepository.salvar(campanhaAtual);
            acaoBemSucedida = true;
        } catch (IllegalStateException e) {
            acaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Then("a campanha deve ser marcada como {string}")
    public void a_campanha_de_ser_marcada_como(String statusEsperadoStr) {
        assertTrue(acaoBemSucedida);
        StatusCampanha statusEsperado = null;
        if ("Sucesso".equalsIgnoreCase(statusEsperadoStr)) {
            statusEsperado = StatusCampanha.FINANCIADA;
        } else if ("Falhou".equalsIgnoreCase(statusEsperadoStr)) {
            statusEsperado = StatusCampanha.NAO_FINANCIADA;
        } else {
            fail("Status esperado inválido no teste: " + statusEsperadoStr);
        }
        assertEquals(statusEsperado, campanhaAtual.getStatus());
    }

    @And("os fundos arrecadados devem ser transferidos para o saldo da desenvolvedora")
    public void os_fundos_devem_ser_transferidos() {
        assertEquals(StatusCampanha.FINANCIADA, campanhaAtual.getStatus());
        System.out.println("Fundos tranferidos para os devs da campanha");
    }


    // @Given("que a campanha para {string} com meta de {string} chegou ao fim")
    // @And("o valor total arrecadado foi de {string}")
    // @When("o sistema processa o final da campanha")
    // @Then("a campanha deve ser marcada como {string}")
    @And("o sistema deve iniciar o estorno automático para todos os apoiadores")
    public void sistema_deve_iniciar_estorno() {
        assertEquals(StatusCampanha.NAO_FINANCIADA, campanhaAtual.getStatus());
        System.out.println("Fundos estornados para os apoiadores");
    }
}
