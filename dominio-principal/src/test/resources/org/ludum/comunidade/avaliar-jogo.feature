Feature: Avaliação de Jogos

  # H-1: Como usuário, fazer uma review sobre um jogo que eu baixei

  Scenario: Usuário avalia jogo que possui na biblioteca
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    And o usuário "não avaliou" o jogo anteriormente
    When o usuário avalia o jogo com nota válida
    Then o sistema salva a avaliação
    And o sistema marca a avaliação como publicada

  Scenario: Usuário tenta avaliar jogo que não possui na biblioteca
    Given um "jogo" "está" publicado
    And o usuário "não possui" o jogo na biblioteca
    When o usuário tenta avaliar o jogo
    Then o sistema informa que o usuário precisa ter o jogo na biblioteca

  Scenario: Usuário tenta avaliar jogo não publicado
    Given um "jogo" "não está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário tenta avaliar o jogo
    Then o sistema informa que o jogo não está publicado

  Scenario: Usuário tenta avaliar o mesmo jogo duas vezes
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    And o usuário "já avaliou" o jogo anteriormente
    When o usuário tenta avaliar o jogo novamente
    Then o sistema informa que o jogo já foi avaliado

  Scenario: Usuário tenta avaliar com nota inválida
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário tenta avaliar o jogo com nota inválida
    Then o sistema informa que a nota deve estar entre 0 e 5

  Scenario: Usuário tenta avaliar sem título
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário tenta avaliar o jogo sem título
    Then o sistema informa que o título não pode estar vazio

  Scenario: Usuário tenta avaliar sem texto
    Given um "jogo" "está" publicado
    And o usuário "possui" o jogo na biblioteca
    When o usuário tenta avaliar o jogo sem texto
    Then o sistema informa que o texto não pode estar vazio

  Scenario: Usuário tenta avaliar jogo inexistente
    Given um "jogo" "não existe"
    When o usuário tenta avaliar o jogo
    Then o sistema informa que o jogo não foi encontrado
