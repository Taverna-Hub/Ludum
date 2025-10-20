Feature: Oficina de Mods
  Como um membro da plataforma
  Quero criar, compartilhar, descobrir e instalar mods
  Para expandir a experiência dos jogos e fomentar a criatividade da comunidade

  # --- Habilitação da Oficina pelo Desenvolvedor ---
  Scenario: Desenvolvedor habilita a oficina para seu próprio jogo
    Given que sou um "Desenvolvedor" autenticado e dono do jogo "Crônicas de Tupi"
    And a oficina para "Crônicas de Tupi" está desabilitada
    When eu habilito a "Oficina de Mods" no painel de gerenciamento do jogo
    Then a seção "Oficina" deve se tornar visível na página pública do jogo

  Scenario: Usuário tenta habilitar a oficina para um jogo que não lhe pertence
    Given que sou um "Jogador" autenticado
    And existe o jogo "Crônicas de Tupi" que não me pertence
    When eu tento acessar o painel de gerenciamento para habilitar a sua oficina
    Then o sistema deve negar o acesso com uma mensagem de erro de permissão

  # --- Envio de Mods ---
  Scenario: Enviar um novo mod para um jogo habilitado que possuo
    Given que sou um "Criador de Mod" autenticado e possuo o jogo "Crônicas de Tupi"
    And a oficina para "Crônicas de Tupi" está habilitada
    When eu envio um novo mod com todos os campos obrigatórios (título, descrição, arquivo)
    Then o mod deve ser publicado com sucesso na oficina do jogo

  Scenario: Tentar enviar um mod para um jogo cuja oficina está desabilitada
    Given que sou um "Criador de Mod" autenticado e possuo o jogo "Crônicas de Tupi"
    And a oficina para "Crônicas de Tupi" está desabilitada
    When eu tento enviar um novo mod para este jogo
    Then o sistema deve rejeitar o envio e informar que a oficina não está disponível

  # --- Atualização de Mods ---
  Scenario: Criador atualiza seu próprio mod com uma nova versão
    Given que sou o "Criador de Mod" do mod "Texturas Realistas"
    And existem jogadores inscritos neste mod
    When eu envio uma nova versão do mod com notas de atualização
    Then a nova versão do mod deve ser publicada
    And os jogadores inscritos devem receber uma notificação sobre a atualização

  Scenario: Usuário tenta atualizar um mod que não lhe pertence
    Given que sou um "Criador de Mod" autenticado
    And existe o mod "Texturas Realistas" criado por outro usuário
    When eu tento enviar uma nova versão para o mod "Texturas Realistas"
    Then o sistema deve negar a ação e exibir uma mensagem de erro

  # --- Inscrição em Mods pelo Jogador ---
  Scenario: Jogador se inscreve em um mod ativo e público
    Given que sou um "Jogador" autenticado
    And estou na página do mod "Texturas Realistas", que está ativo e público
    When eu clico no botão "Inscrever-se"
    Then meu status no mod deve mudar para "Inscrito"

  Scenario: Jogador tenta interagir com um mod que foi removido
    Given que sou um "Jogador" autenticado
    And o mod com ID "12345" foi removido pela moderação
    When eu tento acessar a página do mod com ID "12345"
    Then o sistema deve exibir uma página de erro informando que o conteúdo não está disponível

  # --- Avaliação de Mods ---
  Scenario: Jogador avalia um mod que já baixou
    Given que sou um "Jogador" autenticado e já baixei o mod "Texturas Realistas"
    When eu envio uma avaliação de “gostei”
    Then minha avaliação deve ser registrada com sucesso na página do mod

  Scenario: Jogador tenta avaliar um mod que nunca baixou
    Given que sou um "Jogador" autenticado e nunca baixei o mod "Texturas Realistas"
    When eu tento enviar uma avaliação para o mod
    Then o sistema deve bloquear a ação e me informar que preciso baixar o mod antes de avaliar

  # --- Moderação de Conteúdo ---
  Scenario: Administrador remove um mod que viola as diretrizes
    Given que sou um "Administrador" autenticado
    And existe um mod "Mod Inapropriado" que foi reportado
    When eu uso o painel de moderação para remover o "Mod Inapropriado"
    Then o mod não deve mais ser visível publicamente na oficina

  Scenario: Jogador comum tenta remover um mod
    Given que sou um "Jogador" autenticado sem privilégios de administrador
    And estou na página do mod "Mod Inapropriado"
    When eu tento executar uma ação de moderação para remover o mod
    Then o sistema deve negar a ação por falta de permissão
