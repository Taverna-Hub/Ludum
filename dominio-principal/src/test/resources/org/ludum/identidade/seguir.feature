Feature: Seguir Alvos (Contas, Jogos, Tags, Desenvolvedoras)

  # Regra 1: Não é permitido seguir a si próprio

  Scenario: Usuário segue outro usuário
    Given um "usuário" "existe" e está ativo
    And o "alvo" "não é" o próprio usuário
    When o usuário segue o alvo
    Then o sistema cria o seguimento

  Scenario: Usuário tenta seguir a si mesmo
    Given um "usuário" "existe" e está ativo
    And o "alvo" "é" o próprio usuário
    When o usuário tenta seguir o alvo
    Then o sistema informa que não é possível seguir a si mesmo

  # Regra 2: Seguidor deve existir e estar ativo

  Scenario: Usuário ativo segue um alvo
    Given um "usuário" "está" ativo
    And o "alvo" "existe"
    When o usuário segue o alvo
    Then o sistema cria o seguimento

  Scenario: Usuário inativo tenta seguir um alvo
    Given um "usuário" "não está" ativo
    And o "alvo" "existe"
    When o usuário tenta seguir o alvo
    Then o sistema informa que a conta não está ativa

  # Regra 3: Alvo do tipo CONTA deve existir

  Scenario: Usuário segue conta existente
    Given um "usuário" "existe" e está ativo
    And uma "conta alvo" "existe"
    When o usuário segue a conta
    Then o sistema cria o seguimento

  Scenario: Usuário tenta seguir conta inexistente
    Given um "usuário" "existe" e está ativo
    And uma "conta alvo" "não existe"
    When o usuário tenta seguir a conta
    Then o sistema informa que o usuário alvo não foi encontrado

  # Regra 4: Não pode existir seguimento duplicado

  Scenario: Usuário segue alvo pela primeira vez
    Given um "usuário" "existe" e está ativo
    And o "alvo" "existe"
    And o usuário "não segue" o alvo
    When o usuário segue o alvo
    Then o sistema cria o seguimento

  Scenario: Usuário tenta seguir alvo que já segue
    Given um "usuário" "existe" e está ativo
    And o "alvo" "existe"
    And o usuário "já segue" o alvo
    When o usuário tenta seguir o alvo novamente
    Then o sistema informa que já existe seguimento entre estas entidades

  # Regra 5: Alvo do tipo JOGO deve existir

  Scenario: Usuário segue jogo existente
    Given um "usuário" "existe" e está ativo
    And um "jogo" "existe"
    When o usuário segue o jogo
    Then o sistema cria o seguimento

  Scenario: Usuário tenta seguir jogo inexistente
    Given um "usuário" "existe" e está ativo
    And um "jogo" "não existe"
    When o usuário tenta seguir o jogo
    Then o sistema informa que o jogo não foi encontrado

  # Regra 6: Alvo do tipo TAG deve existir e ter jogos

  Scenario: Usuário segue tag com jogos atrelados
    Given um "usuário" "existe" e está ativo
    And uma "tag" "existe"
    And a tag "possui" jogos atrelados
    When o usuário segue a tag
    Then o sistema cria o seguimento

  Scenario: Usuário tenta seguir tag sem jogos
    Given um "usuário" "existe" e está ativo
    And uma "tag" "existe"
    And a tag "não possui" jogos atrelados
    When o usuário tenta seguir a tag
    Then o sistema informa que a tag não está disponível

  # Regra 7: Usuário bloqueado não pode seguir quem o bloqueou

  Scenario: Usuário não bloqueado segue outro usuário
    Given um "usuário" "existe" e está ativo
    And uma "conta alvo" "existe"
    And o usuário "não está bloqueado" pela conta alvo
    When o usuário segue a conta
    Then o sistema cria o seguimento

  Scenario: Usuário bloqueado tenta seguir quem o bloqueou
    Given um "usuário" "existe" e está ativo
    And uma "conta alvo" "existe"
    And o usuário "está bloqueado" pela conta alvo
    When o usuário tenta seguir a conta
    Then o sistema informa que está bloqueado por este usuário

  # Regra 8: Usuário não pode seguir quem bloqueou

  Scenario: Usuário segue conta que não bloqueou
    Given um "usuário" "existe" e está ativo
    And uma "conta alvo" "existe"
    And o usuário "não bloqueou" a conta alvo
    When o usuário segue a conta
    Then o sistema cria o seguimento

  Scenario: Usuário tenta seguir conta que bloqueou
    Given um "usuário" "existe" e está ativo
    And uma "conta alvo" "existe"
    And o usuário "bloqueou" a conta alvo
    When o usuário tenta seguir a conta
    Then o sistema informa que bloqueou este usuário

  # Regra 9: Alvo do tipo DESENVOLVEDORA deve existir

  Scenario: Usuário segue desenvolvedora existente
    Given um "usuário" "existe" e está ativo
    And uma "desenvolvedora" "existe"
    When o usuário segue a desenvolvedora
    Then o sistema cria o seguimento

  Scenario: Usuário tenta seguir desenvolvedora inexistente
    Given um "usuário" "existe" e está ativo
    And uma "desenvolvedora" "não existe"
    When o usuário tenta seguir a desenvolvedora
    Then o sistema informa que o usuário alvo não foi encontrado