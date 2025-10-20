Feature: Avaliação de Jogos

  # Regra 1: Jogo deve existir no sistema

  Scenario: Usuário avalia jogo existente
    Given um "jogo" "existe" no sistema
    And o jogo "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário avalia o jogo
    Then o sistema salva a avaliação

  Scenario: Usuário tenta avaliar jogo inexistente
    Given um "jogo" "não existe" no sistema
    When o usuário tenta avaliar o jogo
    Then o sistema informa que o jogo não foi encontrado

  # Regra 2: Jogo deve estar publicado

  Scenario: Usuário avalia jogo publicado
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário avalia o jogo
    Then o sistema salva a avaliação

  Scenario: Usuário tenta avaliar jogo não publicado
    Given um "jogo" "não está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário tenta avaliar o jogo
    Then o sistema informa que o jogo não está publicado

  # Regra 3: Usuário deve possuir o jogo na biblioteca

  Scenario: Usuário avalia jogo que possui na biblioteca
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário avalia o jogo
    Then o sistema salva a avaliação

  Scenario: Usuário tenta avaliar jogo que não possui
    Given um "jogo" "está" publicado
    And o usuário "não possui" o jogo na biblioteca
    When o usuário tenta avaliar o jogo
    Then o sistema informa que o usuário precisa ter o jogo na biblioteca

  # Regra 4: Usuário não pode avaliar o mesmo jogo duas vezes

  Scenario: Usuário avalia jogo pela primeira vez
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    And o usuário "não avaliou" o jogo anteriormente
    When o usuário avalia o jogo
    Then o sistema salva a avaliação

  Scenario: Usuário tenta avaliar o mesmo jogo duas vezes
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    And o usuário "já avaliou" o jogo anteriormente
    When o usuário tenta avaliar o jogo novamente
    Then o sistema informa que o jogo já foi avaliado

  # Regra 5: Review deve conter título válido

  Scenario: Usuário avalia jogo com título válido
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário avalia o jogo com título preenchido
    Then o sistema salva a avaliação

  Scenario: Usuário tenta avaliar sem título
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário tenta avaliar o jogo sem título
    Then o sistema informa que o título não pode estar vazio

  # Regra 6: Review deve conter texto válido

  Scenario: Usuário avalia jogo com texto válido
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário avalia o jogo com texto preenchido
    Then o sistema salva a avaliação

  Scenario: Usuário tenta avaliar sem texto
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário tenta avaliar o jogo sem texto
    Then o sistema informa que o texto não pode estar vazio

  # Regra 7: Nota deve estar entre 0 e 5

  Scenario: Usuário avalia jogo com nota válida
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário avalia o jogo com nota entre 0 e 5
    Then o sistema salva a avaliação

  Scenario: Usuário tenta avaliar com nota inválida
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário tenta avaliar o jogo com nota inválida
    Then o sistema informa que a nota deve estar entre 0 e 5
