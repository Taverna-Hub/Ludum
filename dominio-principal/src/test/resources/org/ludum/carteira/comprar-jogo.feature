Feature: Comprar jogo
  Como usuário quero comprar jogos publicados para jogar títulos indies

  Scenario: Compra com saldo + pagamento complementar (positivo)
    Given que sou um usuário com saldo de R$20
    And o jogo está publicado e custa R$50
    And minha forma de pagamento complementar autoriza a cobrança de R$30
    When finalizo a compra usando R$20 de saldo e cobrando R$30 no cartão
    Then a compra é concluída, o jogo é adicionado à minha biblioteca e meu saldo é zerado

  Scenario: Falha na forma de pagamento complementar (negativo)
    Given que sou um usuário com saldo de R$20
    And o jogo está publicado e custa R$50
    And a tentativa de cobrar R$30 no cartão é recusada
    When finalizo a compra
    Then a compra deve falhar e meu saldo permanece R$20

  Scenario: Compra de jogo publicado com arquivo disponível (positivo)
    Given que o jogo está publicado, disponível para venda e o arquivo do jogo está presente no repositório
    And eu tenho forma de pagamento válida
    When eu realizo a compra
    Then a compra é concluída com sucesso e recebo acesso para baixar o jogo

  Scenario: Tentativa de comprar jogo publicado sem arquivo disponível (negativo)
    Given que o jogo está publicado e disponível para venda
    And o arquivo binário/instalador do jogo NÃO está disponível no repositório (arquivo ausente)
    When tento realizar a compra
    Then a transação é bloqueada e nenhuma cobrança é realizada

  Scenario: Primeiro pagamento — usuário não possui o jogo (positivo)
    Given que não sou proprietário do jogo
    And o jogo está publicado e custa R$30
    When compro o jogo
    Then a compra é confirmada e o jogo aparece na minha biblioteca

  Scenario: Tentar comprar jogo já adquirido (negativo)
    Given que já sou proprietário do jogo
    When tento comprar o mesmo jogo novamente
    Then a plataforma retorna erro e nenhuma cobrança é feita

  Scenario: Reembolso válido (menos de 24h e não baixado) (positivo)
    Given que comprei um jogo há menos de 24h
    And ainda não baixei o jogo
    When solicito reembolso
    Then devo receber o valor de volta pela forma original (ou crédito, conforme política) e a compra deve ser registrada como reembolsada

  Scenario: Reembolso negado após download (negativo)
    Given que já baixei o jogo
    When solicito reembolso
    Then o sistema deve impedir o reembolso

  Scenario: Reembolso fora do prazo (>24h) (negativo)
    Given que comprei o jogo há mais de 24h
    When solicito reembolso
    Then o sistema deve impedir o reembolso
