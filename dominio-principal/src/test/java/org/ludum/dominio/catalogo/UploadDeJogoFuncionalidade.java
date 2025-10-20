package org.ludum.dominio.catalogo;

import io.cucumber.java.en.*;
import io.cucumber.java.Before;

import org.ludum.catalogo.jogo.entidades.*;
import org.ludum.catalogo.jogo.repositorios.JogoRepository;
import org.ludum.catalogo.jogo.services.GestaoDeJogosService;
import org.ludum.catalogo.tag.entidades.Tag;
import org.ludum.catalogo.tag.entidades.TagId;
import org.ludum.identidade.conta.entities.Conta;
import org.ludum.identidade.conta.entities.ContaId;
import org.ludum.identidade.conta.enums.StatusConta;
import org.ludum.identidade.conta.enums.TipoConta;
import org.ludum.identidade.conta.repositories.ContaRepository;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UploadDeJogoFuncionalidade {

    private GestaoDeJogosService gestaoDeJogosService;

    private JogoRepository jogoRepository;
    private ContaRepository contaRepository;


    private Jogo jogo;
    private ContaId contaId = new ContaId(UUID.randomUUID().toString());
    private Conta conta;
    private Versao versao;
    private String arquivoNome;

    private boolean passou = false;
    private Exception e;

    @Before
    public void setup() {

        this.jogoRepository = mock(JogoRepository.class);
        this.contaRepository = mock(ContaRepository.class);


        this.gestaoDeJogosService = new GestaoDeJogosService(this.jogoRepository, this.contaRepository);

        this.e = null;

        try {
            this.jogo = new Jogo(new JogoId(UUID.randomUUID().toString()), contaId, "jogo-jogo-jogo", "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")), false, LocalDate.of(2021, 3, 15));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    @Given("que eu sou um usuário logado com o perfil de {string}")
    public void queEuSouUmUsuarioLogadoComOPerfilDe(String str) {
        this.conta = new Conta(contaId, "abc", "123", TipoConta.valueOf(str), StatusConta.ATIVA);

    }

    @And("o padrão de nome de arquivo definido é {string}")
    public void oPadraoDeNome(String str) {

        try {
            String[] list = str.split("_");
            String[] digitos = list[1].split("\\.");
            String formato = digitos[digitos.length - 1];

            if (list.length != 2 || digitos.length != 4) {
                throw new IllegalArgumentException("Nome da versão não está formatado corretamente");
            }
            for (int i = 0; i < digitos.length - 1; i++) {
                try {
                    Integer.parseInt(digitos[i]);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Versão só pode conter números inteiros");
                }
            }

            if (!formato.equals("zip")) {
                throw new IllegalArgumentException("Arquivo com formato incorreto");
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            this.e = new IllegalArgumentException("Arquivo com nome formatado errado");
        }
        if (str.isEmpty()) {
            throw new IllegalArgumentException("Nome da versão vazio");
        }
    }

    @When("eu tento enviar o arquivo {string}")
    public void oPadraoDeNomeDeArquivoDefinidoE(String str) {
        try {
            when(this.contaRepository.obterPorId(this.conta.getId())).thenReturn(this.conta);
            when(this.jogoRepository.obterPorId(this.jogo.getId())).thenReturn(this.jogo);
            this.gestaoDeJogosService.processarUpload(this.conta.getId(), this.jogo.getId(), new PacoteZip(new byte[10]), new VersaoId(UUID.randomUUID().toString()), str, "a");
            this.arquivoNome = str;
            this.passou = true;
        } catch (IllegalStateException | IllegalArgumentException e) {
            this.passou = false;
            this.e = e;
        } catch (ArrayIndexOutOfBoundsException e) {
            this.passou = false;
        }
    }

    @Then("o sistema deve aceitar o arquivo para a próxima etapa de validação")
    public void oSistemaDeveAceitarOAraquivo() {
        if (!this.passou) {
            throw new IllegalStateException("O arquivo não passou a verificação");
        }
    }

    @Then("o sistema deve bloquear o meu acesso")
    public void oSistemaDeveBloquearOMeuAcesso() {
        if (this.passou) {
            throw new IllegalStateException("Sistema não bloqueou o acesso");
        }
    }

    @And("deve exibir uma mensagem informando o erro")
    public void deveExibirMensagemInformandoOErro() {
        if (!this.passou) {
            this.e.printStackTrace();
        }
    }

    @When("eu tento enviar o arquivo {string} para um jogo que não é meu")
    public void euTentoEnviarOArquivoJogoZip(String str) {
        try {
            Jogo newJogo = new Jogo(new JogoId(UUID.randomUUID().toString()), new ContaId(UUID.randomUUID().toString()), "jogo-jogo-jogo", "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")), false, LocalDate.of(2021, 3, 15));
            when(this.contaRepository.obterPorId(this.conta.getId())).thenReturn(this.conta);
            when(this.jogoRepository.obterPorId(this.jogo.getId())).thenReturn(newJogo);
            this.gestaoDeJogosService.processarUpload(this.conta.getId(), this.jogo.getId(), new PacoteZip(new byte[10]), new VersaoId(UUID.randomUUID().toString()), str, "a");
            this.passou = true;
        } catch (IllegalStateException e) {
            this.e = e;
        } catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Then("o sistema deve rejeitar o upload")
    public void oSistemaDeveRejeitarOUpload() {
        if (this.passou) {
            throw new IllegalStateException("Upload Aceito");
        }
    }

    @Given("que um desenvolvedor enviou um arquivo .zip para a plataforma")
    public void queUmDesenvolvedorEnviou(){
        this.conta = new Conta(contaId, "abc", "123", TipoConta.DESENVOLVEDORA, StatusConta.ATIVA);
        this.versao = new Versao(new PacoteZip(new byte[10]), this.jogo.getId(), new VersaoId(UUID.randomUUID().toString()), "jogo-jogo-jogo_1.0.0.zip", "a");
    }

    @When("o sistema de verificação de segurança analisa o arquivo {string}")
    public void  oSistemaDeverificacaoAnalisaOArquivo(String str) {
        try {
            this.gestaoDeJogosService.verificarMalware(this.versao.getPacoteZip(), str);
            this.passou = true;
        }catch (IllegalStateException e){
            this.passou = false;
            this.e = e;
        }
    }

    @And("nenhum malware é detectado")
    public void nenhumMalwareDetectado() {
        if (!this.passou) {
            throw new IllegalStateException("Não passou na detecao de malware");
        }
    }

    @And("detecta uma assinatura de malware")
    public void detectaUmaAssinaturaDeMalware() {
        if (this.passou) {
            throw new IllegalStateException("Passou na deteccao de malware");
        }
    }

    @Then("o arquivo enviado deve ser descartado")
    public void oArquivoDescartadoEnviadoDeMalware() {
        this.versao = null;
    }

    @And("o arquivo passou na validação de nome e formato")
    public void oArquivoPassouNomeEFormato() {
        if(!this.passou){
            throw new IllegalStateException("Não passou na nome e formato");
        }
    }

    @And("o arquivo passou na verificação de malware")
    public void oArquivoPassouNaVerificacaoDeMalware() {
        if(!this.passou){
            throw new IllegalStateException("Não passou na nome e formato");
        }
        this.versao = this.jogo.getVersaoHistory().getLast();
    }

    @Then("o arquivo {string} deve ser salvo no banco de dados")
    public void oArquivoSalvoNoBancoDeDados(String str) {
        when(this.jogoRepository.obterPorId(this.jogo.getId())).thenReturn(this.jogo);
        Jogo currentJogo = this.jogoRepository.obterPorId(this.jogo.getId());

        if (!currentJogo.getVersaoHistory().getLast().equals(this.versao)) {
            throw new IllegalStateException("Não salvo no banco de dados");
        }

    }
    @And("metadados como ID da versao e data do upload devem ser armazenados")
    public void metadadosComoIDDoDesenvolvedorEDataDoUpload() {
        when(this.jogoRepository.obterPorId(this.jogo.getId())).thenReturn(this.jogo);
        Jogo currentJogo = this.jogoRepository.obterPorId(this.jogo.getId());

        Versao currentVersao = currentJogo.getVersaoHistory().getLast();

        if(currentVersao.getDataUpload() != this.versao.getDataUpload() || currentVersao.getId() != this.versao.getId()){
            throw new IllegalStateException("Metadados não salvos");
        }

    }
}