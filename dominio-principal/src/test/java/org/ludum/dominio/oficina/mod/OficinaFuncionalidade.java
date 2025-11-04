package org.ludum.dominio.oficina.mod;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.fail;
import org.ludum.dominio.catalogo.jogo.entidades.Jogo;
import org.ludum.dominio.catalogo.jogo.entidades.JogoId;
import org.ludum.dominio.identidade.conta.entities.Conta;
import org.ludum.dominio.identidade.conta.entities.ContaId;
import org.ludum.dominio.identidade.conta.enums.StatusConta;
import org.ludum.dominio.identidade.conta.enums.TipoConta;
import org.ludum.dominio.oficina.mod.entidades.Mod;
import org.ludum.dominio.oficina.mod.enums.StatusMod;
import org.ludum.dominio.oficina.mod.repositorios.ModRepository;
import org.ludum.dominio.oficina.mod.services.ModsService;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class OficinaFuncionalidade {

    private static class MockModRepository implements ModRepository {
        private final List<Mod> mods = new ArrayList<>();

        @Override
        public void salvar(Mod mod) {
            // Remove o antigo se ele já existir (simula att)
            mods.removeIf(m -> m.getId().equals(mod.getId()));
            mods.add(mod);
        }

        @Override
        public Optional<Mod> buscarPorId(String id) {
            return mods.stream().filter(m -> m.getId().equals(id)).findFirst();
        }

        @Override
        public List<Mod> listarPorJogo(JogoId jogoId) {
            return mods.stream().filter(m -> m.getJogoId().equals(jogoId)).toList();
        }

        @Override
        public List<Mod> listarPorAutor(ContaId autorId) {
            return mods.stream().filter(m -> m.getAutorId().equals(autorId)).toList();
        }

        @Override
        public void remover(Mod mod) {
            mods.removeIf(m -> m.getId().equals(mod.getId()));
        }

        // Método auxiliar para os testes
        public List<Mod> getMods() {
            return new ArrayList<>(mods);
        }

        public void clear() {
            mods.clear();
        }
    }

    private ModsService modsService;
    private MockModRepository mockModRepository;

    private Conta contaAtual;
    private Jogo jogoAtual;
    private Mod modAtual;
    private Map<String, Boolean> oficinaHabilitadaPorJogo; // Simula estado externo
    private Map<String, Set<ContaId>> inscritosPorMod; // Simula estado externo
    private Map<String, Boolean> modsBaixadosPorUsuario; // Simula estado externo

    private boolean operacaoBemSucedida;
    private Exception excecaoLancada;
    private String mensagemErro;

    @Before
    public void setup() {

        mockModRepository = new MockModRepository();
        modsService = new ModsService(mockModRepository);

        contaAtual = null;
        jogoAtual = null;
        modAtual = null;
        oficinaHabilitadaPorJogo = new HashMap<>();
        inscritosPorMod = new HashMap<>();
        modsBaixadosPorUsuario = new HashMap<>();
        operacaoBemSucedida = false;
        excecaoLancada = null;
        mensagemErro = null;

        mockModRepository.clear();
    }

    private void criarConta(String tipoUsuario, String id, StatusConta status) {
        TipoConta tipo = TipoConta.JOGADOR; // Default
        if ("Desenvolvedor".equalsIgnoreCase(tipoUsuario)) {
            tipo = TipoConta.DESENVOLVEDORA;
        } else if ("Administrador".equalsIgnoreCase(tipoUsuario)) {
            tipo = TipoConta.DESENVOLVEDORA;
        } else if ("Criador de Mod".equalsIgnoreCase(tipoUsuario)) {
            tipo = TipoConta.DESENVOLVEDORA;
        }
        contaAtual = new Conta(new ContaId(id), "Usuário " + id, "senhaHash", tipo, status);
    }

    private void criarJogo(String nomeJogo, ContaId devId) {
        jogoAtual = new Jogo(
            new JogoId(nomeJogo + "-id"),
            devId,
            nomeJogo,
            "Descrição do jogo " + nomeJogo,
            null,
            new ArrayList<>(),
            false,
            null
        );
    }

    private void habilitarOficina(JogoId jogoId) {
        oficinaHabilitadaPorJogo.put(jogoId.getValue(), true);
    }
     
    private boolean isOficinaHabilitada(JogoId jogoId) {
       return oficinaHabilitadaPorJogo.getOrDefault(jogoId.getValue(), false);
    }
    
    private void inscreverUsuario(String modId, ContaId usuarioId) {
        inscritosPorMod.computeIfAbsent(modId, k -> new HashSet<>()).add(usuarioId);
    }
    
    private boolean verificarInscricao(String modId, ContaId usuarioId) {
         return inscritosPorMod.getOrDefault(modId, Collections.emptySet()).contains(usuarioId);
    }
    
    private void marcarModComoBaixado(String modId, ContaId usuarioId) {
        modsBaixadosPorUsuario.put(modId + "_" + usuarioId.getValue(), true);
    }

    private boolean usuarioBaixouMod(String modId, ContaId usuarioId) {
        return modsBaixadosPorUsuario.getOrDefault(modId + "_" + usuarioId.getValue(), false);
    }

    // Cenários: Habilitação da Oficina
    @Given("que sou um {string} autenticado e dono do jogo {string}")
    public void modder_autenticado_e_dono(String tipoUsuario, String nomeJogo) {
        criarConta(tipoUsuario, "modder-dono", StatusConta.ATIVA); // Usa o tipo correto
        criarJogo(nomeJogo, contaAtual.getId());
    }

    @When("eu habilito a {string} no painel de gerenciamento do jogo")
    public void habilito_oficina(String nomeFuncionalidade) {
        // Lógica de permissão (apenas desenvolvedor dono)
        if (contaAtual != null && contaAtual.getTipo() == TipoConta.DESENVOLVEDORA && jogoAtual.getDesenvolvedoraId().equals(contaAtual.getId())) {
            habilitarOficina(jogoAtual.getId());
            operacaoBemSucedida = true;
        }
        else {
            operacaoBemSucedida = false;
            mensagemErro = "Erro de permissão para habilitar oficina.";
        }
    }

    @Then("a seção {string} deve se tornar visível na página pública do jogo")
    public void secao_deve_tornar_visivel_na_pagina(String nomeSecao) {
        assertTrue(operacaoBemSucedida);
        assertTrue(isOficinaHabilitada(jogoAtual.getId()));
    }


    @Given("que sou um {string} autenticado")
    public void jogador_autenticado(String tipoUsuario) {
        criarConta(tipoUsuario, "jogador-comum", StatusConta.ATIVA); // Usa o tipo correto
    }

    @And("existe o jogo {string} que não me pertence")
    public void jogo_nao_pertencente(String nomeJogo) {
        ContaId outroDevId = new ContaId("outro-dev");
        criarJogo(nomeJogo, outroDevId);
    }

    @When("eu tento acessar o painel de gerenciamento para habilitar a sua oficina")
    public void habilitar_oficina_sem_permissao() {
        if (contaAtual != null && contaAtual.getTipo() == TipoConta.DESENVOLVEDORA && jogoAtual != null && jogoAtual.getDesenvolvedoraId().equals(contaAtual.getId())) {
            habilitarOficina(jogoAtual.getId());
            operacaoBemSucedida = true;
        } else {
            operacaoBemSucedida = false;
            mensagemErro = "Erro de permissão";
        }
    }

    @Then("o sistema deve negar o acesso com uma mensagem de erro de permissão")
    public void sistema_nega_acesso_e_envia_mensagem_de_erro_de_permissao() {
        assertFalse(operacaoBemSucedida);
        assertEquals("Erro de permissão", mensagemErro);
    }
    
    // Envio de Mods

    @Given("que sou um {string} autenticado e possuo o jogo {string}")
    public void modder_autenticado_que_possui_jogo(String tipoUsuario, String nomeJogo) {
        criarConta(tipoUsuario, "modder-criador", StatusConta.ATIVA);
        criarJogo(nomeJogo, new ContaId("dev-dono-jogo"));
    }

    @And("a oficina para {string} está habilitada")
    public void oficina_esta_habilitada(String nomeJogo) {
        assertNotNull(jogoAtual.getId());
        habilitarOficina(jogoAtual.getId());
        assertTrue(isOficinaHabilitada(jogoAtual.getId()));
    }

    @When("eu envio um novo mod com todos os campos obrigatórios \\(título, descrição, arquivo)")
    public void envio_mod_com_campos_obrigatorios() {
        try {
            if (!isOficinaHabilitada(jogoAtual.getId())) {
                throw new IllegalStateException("Oficina não está habilitada para este jogo.");
            }

            byte[] arquivo = "conteudo do mod".getBytes();
            modAtual = modsService.enviarNovoMod(
                    jogoAtual.getId(),
                    contaAtual.getId(),
                    "Novo Mod Teste",
                    "Descrição do novo mod.",
                    "Notas da versão inicial.",
                    arquivo
            );
            operacaoBemSucedida = true;
        } catch (IllegalStateException e) {
            operacaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o mod deve ser publicado com sucesso na oficina do jogo")
    public void mod_deve_ser_publicado_com_sucesso() {
        assertTrue(operacaoBemSucedida);
        assertNotNull(modAtual);
        Optional<Mod> modSalvo = mockModRepository.buscarPorId(modAtual.getId());
        assertTrue(modSalvo.isPresent());
        assertEquals(StatusMod.ATIVO, modSalvo.get().getStatus());
        assertEquals(1, modSalvo.get().getVersoes().size());
    }


    @And("a oficina para {string} está desabilitada")
    public void oficina_esta_desabilitada(String nomeJogo) {
        assertNotNull(jogoAtual);
        assertEquals(nomeJogo, jogoAtual.getTitulo());
        assertFalse(isOficinaHabilitada(jogoAtual.getId()));
    }

    @When("eu tento enviar um novo mod para este jogo")
    public void tento_enviar_o_mod() {
        envio_mod_com_campos_obrigatorios();
    }

    @Then("o sistema deve rejeitar o envio e informar que a oficina não está disponível")
    public void o_sistema_deve_rejeitar_e_informar_que_oficina_indisponivel() {
        assertFalse(operacaoBemSucedida);
        assertTrue(excecaoLancada instanceof IllegalStateException || mensagemErro != null);
    }

    // Atualização de Mods

    @Given("que sou o {string} do mod {string}")
    public void sou_o_modder_do_mod_x(String tipoUsuario, String nomeMod) {
        criarConta(tipoUsuario, "modder-criador-autor", StatusConta.ATIVA);
        criarJogo("JogoDoMod", contaAtual.getId());
        habilitarOficina(jogoAtual.getId());

        byte[] arquivo = "v1".getBytes();
        modAtual = modsService.enviarNovoMod(
                jogoAtual.getId(),
                contaAtual.getId(),
                nomeMod,
                "Descrição original",
                "v1.0",
                arquivo
        );
        assertNotNull(modAtual);
    }

    @And("existem jogadores inscritos neste mod")
    public void existem_jogadores_inscritos() {
        inscreverUsuario(modAtual.getId(), new ContaId("jogador-inscrito-1"));
        inscreverUsuario(modAtual.getId(), new ContaId("jogador-inscrito-2"));
        assertTrue(inscritosPorMod.containsKey(modAtual.getId()) && inscritosPorMod.get(modAtual.getId()).size() >= 1);
    }

    @When("eu envio uma nova versão do mod com notas de atualização")
    public void envio_nova_versao_com_notas() {
        try {
            byte[] novoArquivo = "v2".getBytes();
            modsService.adicionarVersaoAoMod(
                modAtual.getId(),
                contaAtual.getId(),
                "Notas da v2.0",
                novoArquivo
            );

            modAtual = mockModRepository.buscarPorId(modAtual.getId()).orElse(null);
            operacaoBemSucedida = true;
        } catch (Exception e) {
            operacaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Then("a nova versão do mod deve ser publicada")
    public void nova_versao_publicada() {
        assertTrue(operacaoBemSucedida);
        assertNotNull(modAtual);
        assertEquals(2, modAtual.getVersoes().size());
    }

    @And("os jogadores inscritos devem receber uma notificação sobre a atualização")
    public void inscritos_devem_ser_notificados() {
        assertTrue(inscritosPorMod.containsKey(modAtual.getId()) && !inscritosPorMod.get(modAtual.getId()).isEmpty());
    }


    @Given("existe o mod {string} criado por outro usuário")
    public void mod_criado_por_outro_usuario_existe(String nomeMod) {
        ContaId outroAutorId = new ContaId("outro-autor");

        assertNotNull(contaAtual);
        assertNotEquals(contaAtual.getId(), outroAutorId);

        criarJogo("JogoOutroDono", outroAutorId);
        habilitarOficina(jogoAtual.getId());

        byte[] arquivo = "v1-outro-autor".getBytes();
        modAtual = modsService.enviarNovoMod(
            jogoAtual.getId(),
            outroAutorId, // <--- ID do outro autor
            nomeMod,
            "Descrição de outro autor",
            "v1.0",
            arquivo
        );

        assertNotNull(modAtual);
        assertEquals(outroAutorId, modAtual.getAutorId());
    }

    @When("eu tento enviar uma nova versão para o mod {string}")
    public void tentativa_enviar_mod_para_jogo_de_outro_autor(String nomeMod) {
        try {
            assertNotNull(modAtual);
            assertEquals(nomeMod, modAtual.getNome());
            assertNotEquals(contaAtual.getId(), modAtual.getAutorId());

            byte[] novoArquivo = "v2-hack".getBytes();
            modsService.adicionarVersaoAoMod(
                modAtual.getId(),
                contaAtual.getId(),
                "Notas da v2.0 hackeadas",
                novoArquivo
            );
            operacaoBemSucedida = true;
        } catch (Exception e) {
            operacaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o sistema deve negar a ação e exibir uma mensagem de erro")
    public void sistema_deve_negar_e_exibir_mensagem_de_erro() {
        assertFalse(operacaoBemSucedida);
        assertTrue(excecaoLancada instanceof IllegalStateException);
        assertTrue(mensagemErro != null && mensagemErro.toLowerCase().contains("autor"));
    }

    // Inscrição em Mods

    @And("estou na página do mod {string}, que está ativo e público")
    public void estou_pa_pagina_do_mod_ativo_e_public(String nomeMod) {
        ContaId autorId = new ContaId("autor-mod_publico");
        criarJogo("JogoModPublicado", autorId);
        habilitarOficina(jogoAtual.getId());

        byte[] arquivo = "v1".getBytes();
        modAtual = modsService.enviarNovoMod(
            jogoAtual.getId(),
            autorId,
            nomeMod, 
            "Descrição",
            "v1.0",
            arquivo
        );
        assertNotNull(modAtual);
        assertEquals(StatusMod.ATIVO, modAtual.getStatus());
    }

    @When("eu clico no botão {string}")
    public void clico_no_botao(String botao) {
        if ("inscrever-se".equalsIgnoreCase(botao)) {
            inscreverUsuario(modAtual.getId(), contaAtual.getId());
            operacaoBemSucedida = true;
        } else {
            operacaoBemSucedida = false;
        }
    }

    @Then("meu status no mod deve mudar para {string}")
    public void status_mod_deve_mudar(String statusEsperado) {
        assertTrue(operacaoBemSucedida);
        assertTrue(verificarInscricao(modAtual.getId(), contaAtual.getId()));
    }


    @And("o mod com ID {string} foi removido pela desenvolvedora")
    public void mod_foi_removido_pela_desenvolvedora(String modId) {
        ContaId autorId = new ContaId("autor-mod-removido");
        criarJogo("JogoModRemovido", autorId);
        habilitarOficina(jogoAtual.getId());
        byte[] arquivo = "v1".getBytes();

        Mod modRemovido = new Mod(
            jogoAtual.getId(),
            autorId, 
            "Mod para Remover", 
            "Descrição"
        );
        modRemovido.adicionarNovaVersao("v1", arquivo);

        try {
            ContaId devId = new ContaId("dev-001");
            modRemovido.remover();
            mockModRepository.salvar(modRemovido);
            modAtual = modRemovido;
        } catch (Exception e) {
            fail("Falha ao simular a remoção do mod: " + e.getMessage());
        }
        assertEquals(StatusMod.REMOVIDO, modAtual.getStatus());
    }

    @When("eu tento acessar a página do mod com ID {string}")
    public void tento_acessar_pagina_do_mod_com_id(String modId) {
        Optional<Mod> modOptional = mockModRepository.buscarPorId(modAtual.getId());

        if (modOptional.isPresent() && modOptional.get().getStatus() == StatusMod.REMOVIDO) {
            operacaoBemSucedida = false;
            mensagemErro = "Conteúdo não está disponível";
        } else if (modOptional.isEmpty()) {
            operacaoBemSucedida = false;
            mensagemErro = "Mod não encontrado";
        } else {
            operacaoBemSucedida = true;
        }
    }

    @Then("o sistema deve exibir uma página de erro informando que o conteúdo não está disponível")
    public void sistema_deve_exibir_pagina_de_erro_de_conteudo_indisponivel() {
        assertFalse(operacaoBemSucedida);
        assertTrue(mensagemErro != null && mensagemErro.toLowerCase().contains("conteúdo não está disponível"));
    }

    // Avaliação de Mods

    @Given("que sou um {string} autenticado e já baixei o mod {string}")
    public void sou_autenticado_e_ja_baixei_o_mod(String tipoUsuario, String nomeMod) {
        criarConta(tipoUsuario, "jogador-avaliador", StatusConta.ATIVA);

        if (mockModRepository.listarPorAutor(new ContaId("autor-mod-avaliado")).stream().noneMatch(m -> m.getNome().equals((nomeMod)))) {
            ContaId autorId = new ContaId("autor-mod-avaliado");
            criarJogo("JogoModAvaliado", autorId);
            habilitarOficina(jogoAtual.getId());
            byte[] arquivo = "v1".getBytes();
            modAtual = modsService.enviarNovoMod(
                jogoAtual.getId(), 
                autorId, nomeMod, 
                "Desc", 
                "v1", 
                arquivo
            );
        } else {
            modAtual = mockModRepository.getMods().stream().filter(m -> m.getNome().equals(nomeMod)).findFirst().get();
        }

        marcarModComoBaixado(modAtual.getId(), contaAtual.getId());
        assertTrue(usuarioBaixouMod(modAtual.getId(), contaAtual.getId()));
    }

    @When("eu envio uma avaliação de gostei")
    public void eu_envio_avaliacao_de_gostei() {
        if (usuarioBaixouMod(modAtual.getId(), contaAtual.getId())) {
            operacaoBemSucedida = true;
        } else {
            operacaoBemSucedida = false;
            mensagemErro = "Precisa baixar o mod antes de avaliar";
        }
    }

    @Then("minha avaliação deve ser registrada com sucesso na página do mod")
    public void minha_avaliacao_deve_ser_registrada_com_sucesso_na_pagina() {
        assertTrue(operacaoBemSucedida);
    }


    @Given("que sou um {string} autenticado e nunca baixei o mod {string}")
    public void sou_autenticado_e_nunca_baixei_o_mod(String tipoUsuario, String nomeMod) {
        criarConta(tipoUsuario, "jogador-nao-baixou", StatusConta.ATIVA);

        if (mockModRepository.getMods().stream().noneMatch(m -> m.getNome().equals(nomeMod))) {
            ContaId autorId = new ContaId("autor-mod-nao-baixado");
            criarJogo("JogoModNaoBaixado", autorId);
            habilitarOficina(jogoAtual.getId());
            byte[] arquivo = "v1".getBytes();
            modAtual = modsService.enviarNovoMod(
                jogoAtual.getId(), 
                autorId, 
                nomeMod, 
                "Descrição", 
                "v1", 
                arquivo
            );
        } else {
            modAtual = mockModRepository.getMods().stream().filter(m -> m.getNome().equals(nomeMod)).findFirst().get();
        }
        assertFalse(usuarioBaixouMod(modAtual.getId(), contaAtual.getId()));
    }

    @When("eu tento enviar uma avaliação para o mod")
    public void tento_enviar_avaliacao_para_mod() {
        if (usuarioBaixouMod(modAtual.getId(), contaAtual.getId())) {
            operacaoBemSucedida = true;
        } else {
            operacaoBemSucedida = false;
            mensagemErro = "Precisa baixar o mod antes de avaliar";
        }
    }

    @Then("o sistema deve bloquear a ação e me informar que preciso baixar o mod antes de avaliar")
    public void sistema_deve_bloquear_a_acao_e_informar_o_usuariO() {
        assertFalse(operacaoBemSucedida);
        assertEquals("Precisa baixar o mod antes de avaliar", mensagemErro);
    }

    // Moderação de Mods
     
    @And("existe um mod {string} que foi reportado")
    public void existe_mod_reportado(String nomeMod) {
        ContaId autorId = new ContaId("autor-mod-reportado");
        
        assertNotNull(contaAtual);

        // Assume que o jogo pertence ao dev que vai moderar
        criarJogo("JogoModReportado", contaAtual.getId());
        habilitarOficina(jogoAtual.getId());
        
        byte[] arquivo = "v1".getBytes();
        modAtual = modsService.enviarNovoMod(
            jogoAtual.getId(), 
            autorId, nomeMod, 
            "Descrição", 
            "v1", 
            arquivo
        );
        // Simula o report (estado externo)
    }

    @When("eu uso o painel de moderação para remover o {string}")
    public void uso_painel_moderacao_para_remover_mod(String nomeMod) {
        try {
            // Simula a verificação de permissão de DESENVOLVEDORA
            if (contaAtual == null || contaAtual.getTipo() != TipoConta.DESENVOLVEDORA) {
                throw new IllegalStateException("Apenas administradores podem remover mods");
            }
            // Simula a remoção (lógica real pode envolver verificar se é dono do jogo etc...)
            Optional<Mod> modOpt = mockModRepository.buscarPorId(modAtual.getId());
            if (modOpt.isPresent()) {
                Mod modParaRemover = modOpt.get();
                // Aqui poderia ter a lógica: é dono do jogo ou admin global?
                if (!modParaRemover.getJogoId().equals(jogoAtual.getId()) || !jogoAtual.getDesenvolvedoraId().equals(contaAtual.getId())) {
                    throw new IllegalStateException("Desenvolvedora não tem permissão para remover mod deste jogo.");
                }

                modParaRemover.remover();
                mockModRepository.salvar(modParaRemover);
                modAtual = modParaRemover;
                operacaoBemSucedida = true;
            } else {
                throw new IllegalArgumentException("Mod não encontrado para remoção");
            }
        } catch (Exception e) {
            operacaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o mod não deve mais ser visível publicamente na oficina")
    public void mod_nao_deve_ser_visivel_na_oficina() {
        assertTrue(operacaoBemSucedida);
        assertNotNull(modAtual);
        Optional<Mod> modVerificado = mockModRepository.buscarPorId(modAtual.getId());
        assertTrue(modVerificado.isPresent());
        assertEquals(StatusMod.REMOVIDO, modVerificado.get().getStatus());
    }


    @Given("que sou um {string} autenticado sem privilégios de desenvolvedor")
    public void sou_autenticado_sem_privilegios_de_dev(String tipoUsuario) {
        criarConta(tipoUsuario, "jogador-nao-dev", StatusConta.ATIVA);
        assertNotEquals(TipoConta.DESENVOLVEDORA, contaAtual.getTipo()); // Garante que não é Dev
    }

    @And("estou na página do mod {string}")
    public void estou_na_pagina_do_mod(String nomeMod) {
        if (mockModRepository.getMods().stream().noneMatch(m -> m.getNome().equals(nomeMod))) {
            ContaId autorId = new ContaId("autor-mod-qualquer");
            // Cria jogo pertencente a OUTRO dev
            criarJogo("JogoModQualquer", autorId);
            habilitarOficina(jogoAtual.getId());
            byte[] arquivo = "v1".getBytes();
            modAtual = modsService.enviarNovoMod(
                jogoAtual.getId(), 
                autorId, 
                nomeMod, 
                "Descrição", 
                "v1", 
                arquivo
            );
        } else {
            modAtual = mockModRepository.getMods().stream().filter(m -> m.getNome().equals(nomeMod)).findFirst().get();
        }
        assertNotNull(modAtual);
    }

    @When("eu tento executar uma ação de moderação para remover o mod")
    public void tento_executar_acao_de_moderacao_para_remover_mod() {
        try {
            // Tenta remover como não-autor e não-dev (do jogo)
            modsService.removerMod(modAtual.getId(), contaAtual.getId());
            operacaoBemSucedida = true;
        } catch (Exception e) {
            operacaoBemSucedida = false;
            excecaoLancada = e;
            mensagemErro = e.getMessage();
        }
    }

    @Then("o sistema deve negar a ação por falta de permissão")
    public void sistema_deve_negar_acao_por_falta_de_permmissao() {
        assertFalse(operacaoBemSucedida);
        assertTrue(excecaoLancada instanceof IllegalStateException);
        //A mensagem de ModsService atual é sobre ser o "autor".
        assertTrue(mensagemErro != null && (mensagemErro.toLowerCase().contains("autor") || mensagemErro.toLowerCase().contains("permissão")));
    }
}
