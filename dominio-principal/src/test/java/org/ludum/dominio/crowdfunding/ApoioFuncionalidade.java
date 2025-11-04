package org.ludum.dominio.crowdfunding;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.crowdfunding.entidades.Apoio;
import org.ludum.dominio.crowdfunding.entidades.Campanha;
import org.ludum.dominio.crowdfunding.entidades.CampanhaId;
import org.ludum.dominio.crowdfunding.entidades.Periodo;
import org.ludum.dominio.crowdfunding.enums.StatusCampanha;
import org.ludum.dominio.crowdfunding.repositorios.ApoioRepository;
import org.ludum.dominio.crowdfunding.repositorios.CampanhaRepository;
import org.ludum.dominio.crowdfunding.services.ApoioService;
import org.ludum.dominio.financeiro.transacao.entidades.TransacaoId;
import org.ludum.dominio.identidade.conta.entities.ContaId;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ApoioFuncionalidade {

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
                    .collect(Collectors.toList());
        }

        // Helper para testes
        public void adicionarCampanha(Campanha campanha) {
            campanhas.put(campanha.getId(), campanha);
        }
         public Campanha getCampanhaPorNomeJogo(String nomeJogo) {
            return campanhas.values().stream()
                .filter(c -> {
                    // Precisa buscar o jogo pelo ID da campanha e comparar o nome
                    // Isso exigiria acesso ao JogoRepository aqui, o que complica.
                    // Alternativa: Armazenar nome do jogo no mock ou usar ID diretamente.
                    // Por simplicidade, vamos assumir que o ID da campanha pode ser derivado do nome do jogo no teste.
                    // Ou, melhor ainda, que o cenário passa o CampanhaId correto.
                    // Se não, o mock precisa ser mais complexo ou o teste ajustado.
                    // Vamos usar uma convenção simples para o teste: CampanhaId("campanha-" + nomeJogo)
                    CampanhaId idEsperado = new CampanhaId("campanha-" + nomeJogo.toLowerCase().replace(" ", "-"));
                     return c.getId().equals(idEsperado); // Assume que CampanhaId tem equals baseado no value
                })
                .findFirst()
                .orElse(null);
        }
    }

    private static class MockApoioRepository implements ApoioRepository {
        private final List<Apoio> apoios = new ArrayList<>();

        @Override
        public void salvar(Apoio apoio) {
            // Remove o antigo se já existir (simula atualização)
            apoios.removeIf(a -> a.getId().equals(apoio.getId()));
            apoios.add(apoio);
        }

        @Override
        public Optional<Apoio> buscarPorId(String id) {
            return apoios.stream().filter(a -> a.getId().equals(id)).findFirst();
        }

        @Override
        public List<Apoio> obterApoiosDaCampanha(CampanhaId id) {
            return apoios.stream()
                    .filter(a -> a.getCampanhaId().equals(id) && !a.isCancelado())
                    .collect(Collectors.toList());
        }

        // Helper para testes
        public List<Apoio> getApoios() {
            return new ArrayList<>(apoios);
        }
    }

    private ApoioService apoioService;
    private MockCampanhaRepository mockCampanhaRepository;
    private MockApoioRepository mockApoioRepository;

    private ContaId usuarioId;
    private CampanhaId campanhaIdAtual;
    private Campanha campanhaAtual;
    private Apoio apoioAtual;
    private Exception excecaoLancada;
    private String mensagemErro;
    private boolean acaoBemSucedida;
    private BigDecimal valorArrecadadoAntes;

    @Before
    public void setup() {
        mockCampanhaRepository = new MockCampanhaRepository();
        mockApoioRepository = new MockApoioRepository();
        apoioService = new ApoioService(mockApoioRepository, mockCampanhaRepository);

        usuarioId = null;
        campanhaIdAtual = null;
        campanhaAtual = null;
        apoioAtual = null;
        excecaoLancada = null;
        mensagemErro = null;
        acaoBemSucedida = false;
        valorArrecadadoAntes = null;
    }

    // Helpers
    private Campanha criarCampanhaSimulada(String nomeJogo, String status, BigDecimal meta, int duracaoDias) {
         campanhaIdAtual = new CampanhaId("campanha-" + nomeJogo.toLowerCase().replace(" ", "-")); // Convenção ID
         ContaId devId = new ContaId("dev-dono-" + nomeJogo.toLowerCase());
         JogoId jogoId = new JogoId("jogo-" + nomeJogo.toLowerCase().replace(" ", "-")); // Convenção ID

         LocalDateTime inicio, fim;
         if ("ATIVA".equalsIgnoreCase(status) || "ENCERRADA".equalsIgnoreCase(status)) {
             inicio = LocalDateTime.now().minusDays(duracaoDias / 2); // Começou no passado
             fim = LocalDateTime.now().plusDays(duracaoDias / 2);    // Termina no futuro (se ativa)
         } else { // Encerrada
             inicio = LocalDateTime.now().minusDays(duracaoDias + 1); // Começou e terminou no passado
             fim = LocalDateTime.now().minusDays(1);
         }
         Periodo periodo = new Periodo(inicio, fim);

         Campanha campanha = new Campanha(jogoId, devId, meta, periodo);

         // Ajusta o status interno da campanha simulada
          if (!"EM_PREPARACAO".equalsIgnoreCase(status)) {
                campanha.iniciar(); // Tenta iniciar
                if ("ENCERRADA".equalsIgnoreCase(status)) {
                    // Força o encerramento se necessário (pode precisar de reflexão)
                     try {
                         java.lang.reflect.Field statusField = Campanha.class.getDeclaredField("status");
                         statusField.setAccessible(true);
                         statusField.set(campanha, StatusCampanha.ENCERRADA); // Ou NAO_FINANCIADA, FINANCIADA
                         // Para o teste, FECHADA pode ser suficiente se impedir contribuição
                     } catch (Exception e) {
                         fail("Não foi possível forçar status ENCERRADA via reflexão.");
                     }
                } else if (!"ATIVA".equalsIgnoreCase(status) && campanha.getStatus() == StatusCampanha.ATIVA) {
                     // Se o status desejado não for ATIVA, mas ela está ATIVA, força outro status
                     // Ex: FINANCIADA, NAO_FINANCIADA (requer reflexão ou método de teste)
                }
         }


         mockCampanhaRepository.adicionarCampanha(campanha);
         return campanha;
    }

    private Apoio criarApoioSimulado(CampanhaId campanhaId, ContaId apoiadorId, BigDecimal valor, LocalDateTime dataApoio) {
        TransacaoId transacaoId = new TransacaoId("tx-" + UUID.randomUUID()); // Transação simulada
        Apoio apoio = new Apoio(campanhaId, apoiadorId, transacaoId, valor);

        // Ajusta a data do apoio via reflexão para simular prazos
        try {
            java.lang.reflect.Field dataField = Apoio.class.getDeclaredField("data");
            dataField.setAccessible(true);
            dataField.set(apoio, dataApoio);
        } catch (Exception e) {
            fail("Não foi possível alterar a data do apoio via reflexão.");
        }

        mockApoioRepository.salvar(apoio);
        return apoio;
    }

    // Contribuição de jogadores

    @Given("que sou uma {string} autenticado")
    public void apoiador_autenticado(String tipoUsuario) {
        usuarioId = new ContaId(tipoUsuario.toLowerCase() + "-" + UUID.randomUUID());
    }

    @And("existe uma campanha de financiamento ativa para o jogo {string}")
    public void existe_uma_campanha_ativa_para_o_jogo(String nomeJogo) {
        campanhaAtual = criarCampanhaSimulada(nomeJogo, "ATIVA", new BigDecimal("10000.00"), 30);
        campanhaIdAtual = campanhaAtual.getId();
        assertEquals(StatusCampanha.ATIVA, campanhaAtual.getStatus());
    }

    @When("eu contribuo com {string} para a campanha")
    public void contribuo_para_campanha(String valorStr) {
        try {
            BigDecimal valor = new BigDecimal(valorStr.replace("R$ ", "").trim());
            campanhaAtual = mockCampanhaRepository.buscarPorId(campanhaIdAtual).orElseThrow();
            valorArrecadadoAntes = campanhaAtual.getValorArrecadado();

            assertNotNull(mockCampanhaRepository.buscarPorId(campanhaIdAtual).orElse(null));

            apoioAtual = apoioService.apoiarCampanha(usuarioId, campanhaIdAtual, valor);
            acaoBemSucedida = true;
            campanhaAtual = mockCampanhaRepository.buscarPorId(campanhaIdAtual).orElse(null);
        } catch (IllegalStateException | IllegalArgumentException e) {
            acaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = e.getMessage();
        } catch (Exception e) {
            acaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = "Erro inesperado ao contribuir: " + e.getMessage();
        }
    }

    @Then("a minha contribuição deve ser processada com sucesso")
    public void contribuicao_deve_ser_processada_com_sucesso() {
        assertTrue(acaoBemSucedida);
        assertNotNull(apoioAtual);
        assertNotNull(mockApoioRepository.buscarPorId(apoioAtual.getId()).orElse(null));
    }

    @And("o valor total arrecadado da campanha deve ser incrementado em {string}")
    public void valor_arrecadado_deve_ser_incrementado(String valorStr) {
        assertNotNull(campanhaAtual);
        BigDecimal incremento = new BigDecimal(valorStr.replace("R$ ", "").trim());
        BigDecimal valorEsperado = valorArrecadadoAntes.add(incremento);
        assertEquals(0, valorEsperado.compareTo(campanhaAtual.getValorArrecadado()));
    }


    // @Given("que sou um {string} autenticado")
    @And("a campanha de financiamento para o jogo {string} já foi encerrada")
    public void a_campanha_de_financiamento_para_o_jogo_ja_foi_encerrada(String nomeJogo) {
        campanhaAtual = criarCampanhaSimulada(nomeJogo, "ENCERRADA", new BigDecimal("10000.00"), 30);
        campanhaIdAtual = campanhaAtual.getId();

        assertNotNull(campanhaAtual);
        assertTrue(campanhaAtual.getStatus() != StatusCampanha.ATIVA);
    }

    @When("eu tento contribuir com {string} para a campanha")
    public void tento_contribuir_para_campanha(String valorStr) {
        contribuo_para_campanha(valorStr);
    }

    @Then("o sistema deve rejeitar a contribuição e informar que a campanha não está mais ativa")
    public void sistema_deve_rejeitar_a_contribuicao_e_informar_campanha_inativa() {
        assertFalse(acaoBemSucedida);
        assertNotNull(excecaoLancada);
        assertTrue(excecaoLancada instanceof IllegalStateException);
        assertTrue(mensagemErro != null && (mensagemErro.toLowerCase().contains("não está ativa") || mensagemErro.toLowerCase().contains("campanha não encontrada"))); // ApoioService pode lançar não encontrada se buscar e falhar
    }

    // Reembolso a pedido do apoiador

    @Given("que sou um {string} que contribuiu com {string} para a campanha de {string}")
    public void sou_um_jogador_que_contribuiu(String tipoUsuario, String valorStr, String nomeJogo) {
        usuarioId = new ContaId(tipoUsuario.toLowerCase() + "-" + UUID.randomUUID());
        BigDecimal valor = new BigDecimal(valorStr.replace("R$ ", "").trim());

        campanhaAtual = mockCampanhaRepository.getCampanhaPorNomeJogo(nomeJogo);
        if (campanhaAtual == null) {
            campanhaAtual = criarCampanhaSimulada(nomeJogo, "ATIVA", new BigDecimal("10000.00"), 30);
            campanhaIdAtual = campanhaAtual.getId();
        } else {
             campanhaIdAtual = campanhaAtual.getId();
        }

        apoioAtual = criarApoioSimulado(campanhaIdAtual, usuarioId, valor, LocalDateTime.now());
        
        valorArrecadadoAntes = campanhaAtual.getValorArrecadado();
        campanhaAtual.adicionarApoio(valor);
        mockCampanhaRepository.salvar(campanhaAtual);
    }

    @And("se passaram menos de 24 horas desde a minha contribuição")
    public void se_passaram_menos_de_24_horas() {
        LocalDateTime dataDentroDoPrazo = LocalDateTime.now().minusHours(12);
        try {
            Field dataField = Apoio.class.getDeclaredField("data");
            dataField.setAccessible(true);
            dataField.set(apoioAtual, dataDentroDoPrazo);
            mockApoioRepository.salvar(apoioAtual);
        } catch (Exception e) {
            fail("Não foi possível alterar a data do apoio via reflexão.");
        }
    }

    @When("eu solicito o reembolso da minha contribuição")
    public void eu_solicito_reembolso() {
        try {
            assertNotNull(apoioAtual);
            campanhaAtual = mockCampanhaRepository.buscarPorId(campanhaIdAtual).orElseThrow();
            valorArrecadadoAntes = campanhaAtual.getValorArrecadado();

            apoioService.solicitarReembolso(apoioAtual.getId(), usuarioId);
            acaoBemSucedida = true;

            apoioAtual = mockApoioRepository.buscarPorId(apoioAtual.getId()).orElse(null);
             campanhaAtual = mockCampanhaRepository.buscarPorId(campanhaIdAtual).orElse(null);
        } catch (IllegalStateException | IllegalArgumentException e) {
            acaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = e.getMessage();
        } catch (Exception e) {
            acaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = "Erro inesperado ao solicitar reembolso: " + e.getMessage();
        }
    }

    @Then("o valor de {string} deve ser estornado para o meu saldo na plataforma")
    public void o_valor_deve_ser_estornado_para_meu_saldo(String valorStr) {
        assertTrue(acaoBemSucedida);
        assertNotNull(apoioAtual);
        assertTrue(apoioAtual.isCancelado());
        System.out.println("Simulação: Valor " + valorStr + " estornado para o saldo do usuário " + usuarioId.getValue());
    }

    @And("o valor total arrecadado da campanha deve ser decrementado")
    public void o_valor_total_arrecadado_da_campanha_deve_ser_decrementado() {
        assertNotNull(campanhaAtual);

        Apoio apoioOriginal = mockApoioRepository.buscarPorId(apoioAtual.getId()).orElse(null);
        assertNotNull(apoioOriginal);

        BigDecimal valorEsperado = valorArrecadadoAntes.subtract(apoioOriginal.getValor());
        assertEquals(0, valorEsperado.compareTo(campanhaAtual.getValorArrecadado()));
    }


    // @Given("que sou um {string} que contribuiu com {string} para a campanha de {string}")
    @And("se passaram mais de 24 horas desde a minha contribuição")
    public void se_passaram_mais_de_24_horas() {
        LocalDateTime dataForaDoPrazo = LocalDateTime.now().minusHours(25);
        try {
            Field dataField = Apoio.class.getDeclaredField("data");
            dataField.setAccessible(true);
            dataField.set(apoioAtual, dataForaDoPrazo);
            mockApoioRepository.salvar(apoioAtual);
        } catch (Exception e) {
            fail("Não foi possível alterar a data do apoio via reflexão.");
        }
    }

    @When("eu tento solicitar o reembolso da minha contribuição")
    public void eu_tento_solicitar_o_reembolso() {
        eu_solicito_reembolso();
    }

    @Then("o sistema deve negar a solicitação e informar que o prazo para reembolso expirou")
    public void o_sistema_deve_negar_o_reembolso() {
        assertFalse(acaoBemSucedida);
        assertNotNull(excecaoLancada);
        assertTrue(excecaoLancada instanceof IllegalStateException);
        assertTrue(mensagemErro != null && mensagemErro.toLowerCase().contains("prazo"));
    }
}
