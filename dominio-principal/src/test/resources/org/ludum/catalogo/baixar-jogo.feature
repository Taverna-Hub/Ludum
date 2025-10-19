Feature: Download de Jogos
  Para acessar e jogar títulos que desejo,
  Como um usuário da plataforma,
  Eu quero poder baixar jogos gratuitos e pagos que possuo.

  Scenario: Usuário com conta ativa baixa um jogo gratuito já lançado
    Given que eu sou um usuário com uma conta no status "ativa"
    And existe um jogo chamado "Aventura nas Estrelas" que é gratuito e já foi lançado
    When eu seleciono a opção para baixar "Aventura nas Estrelas"
    Then o download do jogo deve iniciar
    And após a conclusão, "Aventura nas Estrelas" deve aparecer na minha lista de jogos baixados

  Scenario: Usuário com conta inativa tenta baixar um jogo gratuito
    Given que eu sou um usuário com uma conta no status "inativa"
    And existe um jogo chamado "Aventura nas Estrelas" que é gratuito e já foi lançado
    When eu tento baixar "Aventura nas Estrelas"
    Then o sistema deve bloquear o download
    And deve exibir uma mensagem informando que minha conta precisa estar ativa

  Scenario: Usuário tenta baixar um jogo gratuito ainda não lançado
    Given que eu sou um usuário com uma conta no status "ativa"
    And existe um jogo chamado "Lendas do Amanhã" que é gratuito mas sua data de lançamento é futura
    When eu tento baixar "Lendas do Amanhã"
    Then o sistema deve impedir o início do download
    And deve me informar que o jogo ainda não foi lançado

  Scenario: Usuário baixa um jogo comprado que já foi lançado
    Given que eu sou um usuário com uma conta ativa
    And o jogo "Guerreiros do Cosmos" está na minha biblioteca com o status "comprado"
    And "Guerreiros do Cosmos" já foi lançado oficialmente
    When eu inicio o download de "Guerreiros do Cosmos"
    Then o download deve começar
    And após o término, o jogo deve ser listado como "baixado"

  Scenario: Usuário baixa novamente um jogo que já possui e já baixou antes
    Given que eu possuo o jogo "Guerreiros do Cosmos" na minha biblioteca
    And "Guerreiros do Cosmos" já consta na minha lista de jogos baixados
    When eu acesso a página do jogo e clico para baixar novamente
    Then um novo download do jogo "Guerreiros do Cosmos" deve ser iniciado com sucesso
    And o sistema não deve me impedir de realizar um novo download
