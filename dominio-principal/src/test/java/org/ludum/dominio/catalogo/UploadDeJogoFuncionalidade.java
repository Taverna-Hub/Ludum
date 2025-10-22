package org.ludum.dominio.catalogo;

import io.cucumber.java.en.*;
import io.cucumber.java.Before;

import org.ludum.catalogo.jogo.entidades.*;
import org.ludum.catalogo.jogo.enums.StatusPublicacao;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class UploadDeJogoFuncionalidade {

    private GestaoDeJogosService gestaoDeJogosService;

    private MockJogoRepository jogoRepository;
    private MockContaRepository contaRepository;


    private JogoId jogoId;
    private VersaoId versaoId;
    private ContaId contaId;
    private Versao versao;

    private boolean passou = false;
    private Exception e;

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

    @Before
    public void setup() {

        this.jogoRepository = new MockJogoRepository();
        this.contaRepository = new MockContaRepository();

        this.jogoId = new JogoId(UUID.randomUUID().toString());
        this.contaId = new ContaId(UUID.randomUUID().toString());
        this.versaoId = new VersaoId(UUID.randomUUID().toString());

        this.gestaoDeJogosService = new GestaoDeJogosService(this.jogoRepository, this.contaRepository);

        this.e = null;

        try {
            this.jogoRepository.salvar(new Jogo(this.jogoId, this.contaId, "jogo-jogo-jogo", "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")), false, LocalDate.of(2021, 3, 15)));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    @Given("que eu sou um usuário logado com o perfil de {string}")
    public void queEuSouUmUsuarioLogadoComOPerfilDe(String str) {
        this.contaRepository.salvar(new Conta(this.contaId, "abc", "123", TipoConta.valueOf(str), StatusConta.ATIVA));

    }

    @And("o padrão de nome de arquivo definido é {string}")
    public void oPadraoDeNome(String str) {

        try {
            this.versao = new Versao(new PacoteZip(new byte[10]), this.jogoId, this.versaoId, str, "a");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        if (str.isEmpty()) {
            throw new IllegalArgumentException("Nome da versão vazio");
        }
    }

    @When("eu tento enviar o arquivo {string}")
    public void oPadraoDeNomeDeArquivoDefinidoE(String str) {
        try {
            this.gestaoDeJogosService.processarUpload(this.contaId, this.jogoId, new PacoteZip(new byte[10]), new VersaoId(UUID.randomUUID().toString()), str, "a");
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
            JogoId newJogoId = new JogoId(UUID.randomUUID().toString());
            this.jogoRepository.salvar(new Jogo(newJogoId, new ContaId(UUID.randomUUID().toString()), "jogo-jogo-jogo", "JogoJogoJogo", new URL("https://exemplo.com/capa.jpg"), List.of(new Tag(new TagId("a"), "aaa")), false, LocalDate.of(2021, 3, 15)));
            this.gestaoDeJogosService.processarUpload(this.contaId, newJogoId, new PacoteZip(new byte[10]), new VersaoId(UUID.randomUUID().toString()), str, "a");
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
    public void queUmDesenvolvedorEnviou() {
        this.versao = new Versao(new PacoteZip(new byte[10]), this.jogoId, new VersaoId(UUID.randomUUID().toString()), "jogo-jogo-jogo_1.0.0.zip", "a");
    }

    @When("o sistema de verificação de segurança analisa o arquivo {string}")
    public void oSistemaDeverificacaoAnalisaOArquivo(String str) {
        try {
            this.gestaoDeJogosService.verificarMalware(this.versao.getPacoteZip(), str);
            this.passou = true;
        } catch (IllegalStateException e) {
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

    @And("o arquivo passou nas validações de nome e formato e na verificação de malware")
    public void oArquivoPassouNomeEFormato() {
        if (!this.passou) {
            throw new IllegalStateException("Não passou na verificação nome e formato ou na verificação de malware");
        }
    }

    @Then("o arquivo {string} deve ser salvo no banco de dados")
    public void oArquivoSalvoNoBancoDeDados(String str) {
        Jogo currentJogo = this.jogoRepository.obterPorId(jogoId);
        this.versao = currentJogo.getVersaoHistory().getLast();

        if (!this.versao.getNomeVersao().equals(str)) {
            throw new IllegalStateException("Não salvo no banco de dados");
        }

    }

    @And("metadados como ID da versao e data do upload devem ser armazenados")
    public void metadadosComoIDDoDesenvolvedorEDataDoUpload() {
        Jogo currentJogo = this.jogoRepository.obterPorId(jogoId);

        Versao currentVersao = currentJogo.getVersaoHistory().getLast();

        if (currentVersao.getDataUpload() != this.versao.getDataUpload() || currentVersao.getId() != this.versao.getId()) {
            throw new IllegalStateException("Metadados não salvos");
        }

    }
}