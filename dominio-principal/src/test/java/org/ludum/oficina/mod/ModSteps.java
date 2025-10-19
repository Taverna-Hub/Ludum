package org.ludum.oficina.mod;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class ModSteps {

    private String tipoUsuario;
    private String nomeJogo;
    private boolean oficinaHabilitada;

    @Given("que eu sou {string} autenticado e dono do jogo {string}")
    public void que_eu_sou_um_autenticado_e_dono_do_jogo(String tipoUsuario, String nomeJogo) {
        this.tipoUsuario = tipoUsuario;
        this.nomeJogo = nomeJogo;
        System.out.println("GIVEN: Usuário é: " + tipoUsuario + " e dono do jogo " + nomeJogo);
    }

    @Given("a oficina para {string} está desabilitada")
    public void a_oficina_para_esta_desabilitada(String nomeJogo) {
        this.oficinaHabilitada = false;
        System.out.println("AND: Oficina para " + nomeJogo + " está desabilitada.");
        assertFalse(this.oficinaHabilitada);
    }

    @When("eu habilito a {string} no painel de gerenciamento do jogo")
    public void eu_habilito_a_no_painel_de_gerenciamento_do_jogo(String nomeFuncionalidade) {
        if ("Desenvolvedor".equals(this.tipoUsuario)) {
            this.oficinaHabilitada = true;
            System.out.println("WHEN: A funcionalidade '" + nomeFuncionalidade + "' foi habilitada.");
        } else {
            System.out.println("WHEN: Acesso negado para habilitar a funcionalidade.");
        }
    }

    @Then("a seção {string} deve se tornar visível na página pública do jogo")
    public void a_secao_deve_se_tornar_visivel_na_pagina_publica_do_jogo(String nomeSecao) {
        System.out.println("THEN: Verificando se a seção '" + nomeSecao + "' está visível.");
        assertTrue(this.oficinaHabilitada, "A oficina deveria estar habilitada e visível.");
        System.out.println("SUCCESS: A oficina agora está visível!");
    }
}
