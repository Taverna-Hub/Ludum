Feature: Financiamento Coletivo (Crowdfunding)
  Como um membro da comunidade,
  Quero criar e apoiar campanhas de financiamento para jogos não publicados
  Para ajudar a viabilizar o desenvolvimento de novos jogos independentes

  # --- Criação de Campanhas pela Desenvolvedora ---
  Scenario: [Ação Correta] Desenvolvedora validada cria uma campanha para um jogo não publicado
    Given que sou uma "Desenvolvedora" autenticada e validada
    And possuo um projeto de jogo "A Lenda do Agreste" com status "não publicado"
    When eu crio uma campanha de financiamento para "A Lenda do Agreste" com uma meta de "R$ 10000.00" e duração de "30" dias
    Then a campanha deve ser criada com sucesso e associada ao jogo

  Scenario: [Ação Incorreta] Desenvolvedora tenta criar uma campanha para um jogo já publicado
    Given que sou uma "Desenvolvedora" autenticada e validada
    And possuo um jogo "Cangaço Cyberpunk" com status "publicado"
    When eu tento criar uma campanha de financiamento para "Cangaço Cyberpunk"
    Then o sistema deve rejeitar a criação e informar que campanhas são apenas para jogos não publicados

  # --- Contribuição de Jogadores ---
  Scenario: [Ação Correta] Jogador contribui para uma campanha ativa
    Given que sou uma "Jogador" autenticado
    And existe uma campanha de financiamento ativa para o jogo "A Lenda do Agreste"
    When eu contribuo com "R$ 50.00" para a campanha
    Then a minha contribuição deve ser processada com sucesso
    And o valor total arrecadado da campanha deve ser incrementado em "R$ 50.00"

  Scenario: [Ação Incorreta] Jogador tenta contribuir para uma campanha já encerrada
    Given que sou uma "Jogador" autenticado
    And a campanha de financiamento para o jogo "A Lenda do Agreste" já foi encerrada
    When eu tento contribuir com "R$ 50.00" para a campanha
    Then o sistema deve rejeitar a contribuição e informar que a campanha não está mais ativa

  # --- Reembolso a Pedido do Apoiador ---
  Scenario: [Ação Correta] Apoiador solicita reembolso dentro de 24 horas
    Given que sou um "Jogador" que contribuiu com "R$ 50.00" para a campanha de "A Lenda do Agreste"
    And se passaram menos de 24 horas desde a minha contribuição
    When eu solicito o reembolso da minha contribuição
    Then o valor de "R$ 50.00" deve ser estornado para o meu saldo na plataforma
    And o valor total arrecadado da campanha deve ser decrementado

  Scenario: [Ação Incorreta] Apoiador solicita reembolso após 24 horas
    Given que sou um "Jogador" que contribuiu com "R$ 50.00" para a campanha de "A Lenda do Agreste"
    And se passaram mais de 24 horas desde a minha contribuição
    When eu tento solicitar o reembolso da minha contribuição
    Then o sistema deve negar a solicitação e informar que o prazo para reembolso expirou

  # --- Finalização Automática da Campanha ---
  Scenario: [Resultado de Sucesso] Campanha termina e atinge a meta mínima
    Given que a campanha para "A Lenda do Agreste" com meta de "R$ 10000.00" chegou ao fim
    And o valor total arrecadado foi de "R$ 12000.00"
    When o sistema processa o final da campanha
    Then a campanha deve ser marcada como "Sucesso"
    And os fundos arrecadados devem ser transferidos para o saldo da desenvolvedora

  Scenario: [Resultado de Falha] Campanha termina e não atinge a meta mínima
    Given que a campanha para "A Lenda do Agreste" com meta de "R$ 10000.00" chegou ao fim
    And o valor total arrecadado foi de "R$ 8000.00"
    When o sistema processa o final da campanha
    Then a campanha deve ser marcada como "Falhou"
    And o sistema deve iniciar o estorno automático para todos os apoiadores
