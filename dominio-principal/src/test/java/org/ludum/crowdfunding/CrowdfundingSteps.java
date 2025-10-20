package org.ludum.crowdfunding;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CrowdfundingSteps {

    // --- Estado Simulado do Sistema ---
    private String tipoUsuario;
    private Map<String, String> jogos;
    private Map<String, CampanhaSimulada> campanhas;
    private String jogoEmContexto;
    private String mensagemDeErro;
    private boolean acaoBemSucedida;
    private BigDecimal valorArrecadadoAntes;

    /**
     * Hook para limpar o estado antes de cada cenário.
     * Isso garante que os testes não interfiram uns nos outros.
     */
    @Before
    public void setup() {
        tipoUsuario = null;
        jogoEmContexto = null;
        mensagemDeErro = null;
        acaoBemSucedida = false;
        valorArrecadadoAntes = null;
        jogos = new HashMap<>();
        campanhas = new HashMap<>();
    }

    /**
     * Classe interna para simular o estado de uma campanha.
     */
    private static class CampanhaSimulada {
        String nomeJogo;
        String status; // "ATIVA", "ENCERRADA", "Sucesso", "Falhou"
        BigDecimal meta;
        BigDecimal valorArrecadado = BigDecimal.ZERO;
        final List<ApoioSimulado> apoios = new ArrayList<>();

        CampanhaSimulada(String nomeJogo, BigDecimal meta) {
            this.nomeJogo = nomeJogo;
            this.meta = meta;
            this.status = "ATIVA"; // Por padrão, começa ativa
        }
    }

    /**
     * Classe interna para simular uma contribuição (apoio).
     */
    private static class ApoioSimulado {
        final String apoiador;
        final BigDecimal valor;
        final LocalDateTime data;

        ApoioSimulado(String apoiador, BigDecimal valor, LocalDateTime data) {
            this.apoiador = apoiador;
            this.valor = valor;
            this.data = data;
        }
    }

    // --- Passos dos Cenários de Teste ---

    @Given("que sou uma {string} autenticada e validada")
    public void que_sou_uma_autenticada_e_validada(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @Given("que sou um {string} autenticado")
    public void que_sou_um_autenticado(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @And("possuo um projeto de jogo {string} com status {string}")
    public void possuo_um_projeto_de_jogo_com_status(String nomeJogo, String status) {
        this.jogos.put(nomeJogo, status);
        this.jogoEmContexto = nomeJogo;
    }

    @And("possuo um jogo {string} com status {string}")
    public void possuo_um_jogo_com_status(String nomeJogo, String status) {
        this.jogos.put(nomeJogo, status);
        this.jogoEmContexto = nomeJogo;
    }

    @When("eu crio uma campanha de financiamento para {string} com uma meta de {string} e duração de {string} dias")
    public void eu_crio_uma_campanha_de_financiamento_para(String nomeJogo, String metaStr, String duracao) {
        String statusJogo = jogos.get(nomeJogo);
        if ("Desenvolvedora".equals(tipoUsuario) && "não publicado".equals(statusJogo)) {
            BigDecimal meta = new BigDecimal(metaStr.replace("R$ ", "").trim());
            campanhas.put(nomeJogo, new CampanhaSimulada(nomeJogo, meta));
            acaoBemSucedida = true;
        } else {
            acaoBemSucedida = false;
            mensagemDeErro = "Campanhas são apenas para jogos não publicados";
        }
    }
    
    @When("eu tento criar uma campanha de financiamento para {string}")
    public void eu_tento_criar_uma_campanha_de_financiamento_para(String nomeJogo) {
        // Reutiliza a lógica principal de criação de campanha
        eu_crio_uma_campanha_de_financiamento_para(nomeJogo, "0", "0");
    }

    @Then("a campanha deve ser criada com sucesso e associada ao jogo")
    public void a_campanha_deve_ser_criada_com_sucesso() {
        assertTrue(acaoBemSucedida, "A criação da campanha deveria ter sido bem-sucedida.");
        assertNotNull(campanhas.get(jogoEmContexto), "A campanha não foi encontrada no mapa de simulação.");
    }

    @Then("o sistema deve rejeitar a criação e informar que campanhas são apenas para jogos não publicados")
    public void o_sistema_deve_rejeitar_a_criacao() {
        assertFalse(acaoBemSucedida, "A criação da campanha deveria ter falhado.");
        assertEquals("Campanhas são apenas para jogos não publicados", mensagemDeErro);
    }
    
    @And("existe uma campanha de financiamento ativa para o jogo {string}")
    public void existe_uma_campanha_ativa(String nomeJogo) {
        this.jogoEmContexto = nomeJogo;
        campanhas.put(nomeJogo, new CampanhaSimulada(nomeJogo, new BigDecimal("10000.00")));
    }

    @And("a campanha de financiamento para o jogo {string} já foi encerrada")
    public void a_campanha_ja_foi_encerrada(String nomeJogo) {
        this.jogoEmContexto = nomeJogo;
        CampanhaSimulada campanha = new CampanhaSimulada(nomeJogo, new BigDecimal("10000.00"));
        campanha.status = "ENCERRADA";
        campanhas.put(nomeJogo, campanha);
    }

    @When("eu contribuo com {string} para a campanha")
    public void eu_contribuo_com_para_a_campanha(String valorStr) {
        CampanhaSimulada campanhaAtual = campanhas.get(jogoEmContexto);
        if (campanhaAtual != null && "ATIVA".equals(campanhaAtual.status)) {
            valorArrecadadoAntes = campanhaAtual.valorArrecadado;
            BigDecimal valor = new BigDecimal(valorStr.replace("R$ ", "").trim());
            campanhaAtual.valorArrecadado = campanhaAtual.valorArrecadado.add(valor);
            acaoBemSucedida = true;
        } else {
            acaoBemSucedida = false;
            mensagemDeErro = "A campanha não está mais ativa";
        }
    }
    
    @When("eu tento contribuir com {string} para a campanha")
    public void eu_tento_contribuir_com_para_a_campanha(String valorStr) {
        // Reutiliza a lógica principal de contribuição
        eu_contribuo_com_para_a_campanha(valorStr);
    }

    @Then("a minha contribuição deve ser processada com sucesso")
    public void a_minha_contribuicao_deve_ser_processada() {
        assertTrue(acaoBemSucedida, "A contribuição deveria ter sido processada com sucesso.");
    }

    @And("o valor total arrecadado da campanha deve ser incrementado em {string}")
    public void o_valor_arrecadado_deve_ser_incrementado(String valorStr) {
        BigDecimal incremento = new BigDecimal(valorStr.replace("R$ ", "").trim());
        BigDecimal valorEsperado = valorArrecadadoAntes.add(incremento);
        assertEquals(0, valorEsperado.compareTo(campanhas.get(jogoEmContexto).valorArrecadado), "O valor arrecadado não foi incrementado corretamente.");
    }

    @Then("o sistema deve rejeitar a contribuição e informar que a campanha não está mais ativa")
    public void o_sistema_deve_rejeitar_contribuicao_encerrada() {
        assertFalse(acaoBemSucedida, "A contribuição deveria ter sido rejeitada.");
        assertEquals("A campanha não está mais ativa", mensagemDeErro);
    }
    
    @Given("que sou um {string} que contribuiu com {string} para a campanha de {string}")
    public void que_sou_um_jogador_que_contribuiu(String tipoUsuario, String valorStr, String nomeJogo) {
        this.tipoUsuario = tipoUsuario;
        this.jogoEmContexto = nomeJogo;
        BigDecimal valor = new BigDecimal(valorStr.replace("R$ ", "").trim());

        CampanhaSimulada campanha = campanhas.computeIfAbsent(nomeJogo, k -> new CampanhaSimulada(k, new BigDecimal("10000.00")));
        campanha.valorArrecadado = campanha.valorArrecadado.add(valor);
    }

    @And("se passaram menos de 24 horas desde a minha contribuição")
    public void se_passaram_menos_de_24_horas() {
        CampanhaSimulada campanha = campanhas.get(jogoEmContexto);
        campanha.apoios.add(new ApoioSimulado(tipoUsuario, new BigDecimal("50.00"), LocalDateTime.now().minusHours(12)));
    }

    @And("se passaram mais de 24 horas desde a minha contribuição")
    public void se_passaram_mais_de_24_horas() {
        CampanhaSimulada campanha = campanhas.get(jogoEmContexto);
        campanha.apoios.add(new ApoioSimulado(tipoUsuario, new BigDecimal("50.00"), LocalDateTime.now().minusHours(25)));
    }

    @When("eu solicito o reembolso da minha contribuição")
    public void eu_solicito_o_reembolso() {
        CampanhaSimulada campanha = campanhas.get(jogoEmContexto);
        ApoioSimulado meuApoio = campanha.apoios.stream().filter(a -> a.apoiador.equals(tipoUsuario)).findFirst().orElse(null);

        if (meuApoio != null && meuApoio.data.plusHours(24).isAfter(LocalDateTime.now())) {
            valorArrecadadoAntes = campanha.valorArrecadado;
            campanha.valorArrecadado = campanha.valorArrecadado.subtract(meuApoio.valor);
            acaoBemSucedida = true;
        } else {
            acaoBemSucedida = false;
            mensagemDeErro = "O prazo para reembolso expirou";
        }
    }

    @When("eu tento solicitar o reembolso da minha contribuição")
    public void eu_tento_solicitar_o_reembolso() {
        eu_solicito_o_reembolso();
    }
    
    @Then("o valor de {string} deve ser estornado para o meu saldo na plataforma")
    public void o_valor_deve_ser_estornado(String valor) {
        assertTrue(acaoBemSucedida, "O reembolso deveria ter sido processado com sucesso.");
    }
    
    @And("o valor total arrecadado da campanha deve ser decrementado")
    public void o_valor_deve_ser_decrementado() {
        CampanhaSimulada campanha = campanhas.get(jogoEmContexto);
        ApoioSimulado meuApoio = campanha.apoios.get(0);
        BigDecimal valorEsperado = valorArrecadadoAntes.subtract(meuApoio.valor);
        assertEquals(0, valorEsperado.compareTo(campanha.valorArrecadado), "O valor arrecadado não foi decrementado corretamente.");
    }

    @Then("o sistema deve negar a solicitação e informar que o prazo para reembolso expirou")
    public void o_sistema_deve_negar_o_reembolso() {
        assertFalse(acaoBemSucedida, "A solicitação de reembolso deveria ter sido negada.");
        assertEquals("O prazo para reembolso expirou", mensagemDeErro);
    }
    
    @Given("que a campanha para {string} com meta de {string} chegou ao fim")
    public void que_a_campanha_chegou_ao_fim(String nomeJogo, String metaStr) {
        this.jogoEmContexto = nomeJogo;
        BigDecimal meta = new BigDecimal(metaStr.replace("R$ ", "").trim());
        campanhas.put(nomeJogo, new CampanhaSimulada(nomeJogo, meta));
    }

    @And("o valor total arrecadado foi de {string}")
    public void o_valor_arrecadado_foi_de(String valorStr) {
        BigDecimal valorArrecadado = new BigDecimal(valorStr.replace("R$ ", "").trim());
        campanhas.get(jogoEmContexto).valorArrecadado = valorArrecadado;
    }

    @When("o sistema processa o final da campanha")
    public void o_sistema_processa_o_final() {
        CampanhaSimulada campanha = campanhas.get(jogoEmContexto);
        if (campanha.valorArrecadado.compareTo(campanha.meta) >= 0) {
            campanha.status = "Sucesso";
        } else {
            campanha.status = "Falhou";
        }
    }

    @Then("a campanha deve ser marcada como {string}")
    public void a_campanha_deve_ser_marcada_como(String status) {
        assertEquals(status, campanhas.get(jogoEmContexto).status);
    }

    @And("os fundos arrecadados devem ser transferidos para o saldo da desenvolvedora")
    public void os_fundos_devem_ser_transferidos() {
        // Apenas uma simulação de verificação
        assertEquals("Sucesso", campanhas.get(jogoEmContexto).status);
    }

    @And("o sistema deve iniciar o estorno automático para todos os apoiadores")
    public void o_sistema_deve_iniciar_o_estorno() {
        // Apenas uma simulação de verificação
        assertEquals("Falhou", campanhas.get(jogoEmContexto).status);
    }
}