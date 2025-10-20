Feature: Download de Jogos
  Para acessar e jogar títulos que desejo,
  Como um usuário da plataforma,
  Eu quero poder baixar jogos gratuitos e pagos que possuo.

  Scenario: Usuário com conta ativa baixa um jogo gratuito já lançado
    Given que eu sou um usuário com uma conta no status "ATIVA"
    And existe um jogo chamado "jogo-jogo-jogo" que é gratuito, já foi lançado e se encontra na minha biblioteca
    When eu seleciono a opção para baixar "jogo-jogo-jogo"
    Then o download do jogo deve iniciar
    And após a conclusão, "jogo-jogo-jogo" deve aparecer na minha lista de jogos baixados

  Scenario: Usuário com conta inativa tenta baixar um jogo gratuito
    Given que eu sou um usuário com uma conta no status "INATIVA"
    And existe um jogo chamado "jogo-jogo-jogo" que é gratuito, já foi lançado e se encontra na minha biblioteca
    When eu seleciono a opção para baixar "jogo-jogo-jogo"
    Then o sistema deve bloquear o download
    And deve exibir a mensagem de erro adequada

  Scenario: Usuário tenta baixar um jogo gratuito ainda não lançado
    Given que eu sou um usuário com uma conta no status "ATIVA"
    And existe um jogo chamado "jogo-jogo-jogo" que é gratuito, mas sua data de lançamento é futura
    When eu seleciono a opção para baixar "jogo-jogo-jogo"
    Then o sistema deve bloquear o download
    And deve exibir a mensagem de erro adequada

  Scenario: Usuário baixa um jogo comprado que já foi lançado
    Given que eu sou um usuário com uma conta no status "ATIVA"
    And existe um jogo chamado "jogo-jogo-jogo" que é pago, já foi lançado e se encontra na minha biblioteca
    When eu seleciono a opção para baixar "jogo-jogo-jogo"
    Then o download do jogo deve iniciar
    And após a conclusão, "jogo-jogo-jogo" deve aparecer na minha lista de jogos baixados

  Scenario: Usuário baixa novamente um jogo que já possui e já baixou antes
    Given que eu possuo o jogo "jogo-jogo-jogo" na minha biblioteca
    And "jogo-jogo-jogo" já consta na minha lista de jogos baixados
    When eu seleciono a opção para baixar "jogo-jogo-jogo"
    Then um novo download do jogo "jogo-jogo-jogo" deve ser iniciado com sucesso
    And o sistema não deve me impedir de realizar um novo download e deve registrar o novo download na minha lista de jogos baixados