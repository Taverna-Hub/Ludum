Feature: Saldo
  Como usuário quero gerenciar meu saldo para usar em compras e saques

  Scenario: Adicionar saldo com pagamento confirmado (positivo)
    Given que adicionei R$50 na carteira
    And o pagamento foi confirmado
    When verifico meu saldo
    Then devo ver R$50 disponível

  Scenario: Adicionar saldo com pagamento pendente/recusado (negativo)
    Given que adicionei R$50 na carteira
    And o pagamento está pendente OU foi recusado
    When verifico meu saldo
    Then o saldo disponível não deve ser alterado e a transação de depósito não deve ser confirmada

  Scenario: Depósito acima de R$100 fica bloqueado imediatamente (positivo)
    Given que adicionei R$150 na carteira
    And o pagamento foi confirmado
    When verifico meu saldo imediatamente
    Then R$150 deve constar como bloqueado/pendente por 24h e o saldo disponível para uso não aumenta

  Scenario: Tentar usar saldo bloqueado antes de 24h (negativo)
    Given que adicionei R$150 na carteira e está dentro do período de bloqueio de 24h
    And tento comprar um jogo que custa R$50
    When finalizo a compra imediatamente
    Then a compra não deve ser concluída e o saldo bloqueado deve permanecer inalterado

  Scenario: Usar saldo em compra com saldo suficiente (positivo)
    Given que tenho R$30 disponíveis na carteira
    And o jogo custa R$25
    When compro o jogo
    Then o saldo deve ser debitado em R$25 e a compra deve ser concluída com sucesso

  Scenario: Tentar usar saldo insuficiente (negativo)
    Given que tenho R$20 disponíveis na carteira
    And o jogo custa R$25
    When tento comprar o jogo
    Then a compra não deve ser concluída e nenhum débito deve ocorrer no saldo

  Scenario: Reembolso válido é creditado na carteira (positivo)
    Given que solicitei reembolso válido (dentro do prazo e não baixei)
    And o reembolso foi processado pelo sistema
    When verifico minha carteira
    Then o valor reembolsado deve constar como saldo disponível

  Scenario: Reembolso inválido não é creditado (negativo)
    Given que solicitei reembolso inválido (fora do prazo OU após download)
    When o sistema avalia a solicitação
    Then o reembolso deve ser negado e nenhum valor deve ser creditado na carteira

  Scenario: Desenvolvedor saca saldo após 24h (positivo)
    Given que vendi um jogo para um usuário
    And o valor está disponível na conta do desenvolvedor há >24h
    And minha conta externa está vinculada/validada
    When solicito saque
    Then o valor deve ser transferido para minha conta externa e o saldo na plataforma deve ser reduzido

  Scenario: Desenvolvedor tenta sacar antes de 24h (negativo)
    Given que vendi um jogo há menos de 24h
    When solicito saque desse valor
    Then a solicitação de saque deve ser recusada e o valor permanece indisponível para saque

  Scenario: Saque com conta externa validada (positivo)
    Given que tenho saldo disponível para saque
    And minha conta bancária/external está vinculada e verificada
    When solicito saque
    Then o saque deve ser processado e transferido para a conta externa

  Scenario: Saque sem conta externa vinculada (negativo)
    Given que tenho saldo disponível para saque
    And não tenho conta externa vinculada/validada
    When solicito saque
    Then a solicitação de saque deve ser recusada e o saldo do desenvolvedor não deve ser alterado

  Scenario: Desenvolvedor saca saldo de crowdfunding após meta e 1 dia (positivo)
    Given que finalizei uma campanha
    And a meta mínima foi atingida
    And já passou 1 dia do término da campanha
    And minha conta externa está validada
    When solicito saque do valor arrecadado
    Then o valor arrecadado deve ser liberado e transferido para minha conta externa

  Scenario: Tentativa de saque de crowdfunding antes de 1 dia OU meta não atingida (negativo)
    Given que finalizei uma campanha
    And (a meta mínima NÃO foi atingida OU ainda não passou 1 dia do término)
    When solicito saque do valor arrecadado
    Then o saque deve ser bloqueado e nenhum valor deve ser liberado