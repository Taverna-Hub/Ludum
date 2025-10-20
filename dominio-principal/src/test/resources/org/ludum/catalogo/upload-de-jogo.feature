Feature: Upload e Validação de Jogos
  Para disponibilizar meus jogos de forma segura e padronizada na plataforma,
  Como um desenvolvedor verificado,
  Eu quero enviar arquivos de jogo seguindo padrões de nome e formato, que sejam verificados automaticamente e armazenados somente após aprovação.

  Scenario: Desenvolvedor faz upload de um jogo com formato e nome de arquivo corretos
    Given que eu sou um usuário logado com o perfil de "DESENVOLVEDORA"
    And o padrão de nome de arquivo definido é "jogo-jogo-jogo_1.0.0.zip"
    When eu tento enviar o arquivo "jogo-jogo-jogo_1.0.0.zip"
    Then o sistema deve aceitar o arquivo para a próxima etapa de validação

  Scenario: Usuário sem permissão de desenvolvedor tenta fazer upload
    Given que eu sou um usuário logado com o perfil de "JOGADOR"
    When eu tento enviar o arquivo "jogo-jogo-jogo_1.0.0.zip"
    Then o sistema deve bloquear o meu acesso
    And deve exibir uma mensagem informando o erro

    Scenario: Desenvolvedor tenta fazer o upload para um jogo que não é dele
      Given que eu sou um usuário logado com o perfil de "DESENVOLVEDORA"
      When eu tento enviar o arquivo "jogo-jogo-jogo_1.0.0.zip" para um jogo que não é meu
      Then o sistema deve bloquear o meu acesso
      And deve exibir uma mensagem informando o erro

  Scenario: Desenvolvedor tenta fazer upload de um arquivo com formato incorreto
    Given que eu sou um usuário logado com o perfil de "DESENVOLVEDORA"
    When eu tento enviar o arquivo "jogo-jogo-jogo_1.0.0.rar"
    Then o sistema deve rejeitar o upload
    And deve exibir uma mensagem informando o erro

  Scenario: Desenvolvedor tenta fazer upload de um arquivo com nome fora do padrão
    Given que eu sou um usuário logado com o perfil de "DESENVOLVEDORA"
    And o padrão de nome de arquivo definido é "jogo-jogo-jogo.zip"
    When eu tento enviar o arquivo "jogo-jogo-jogo.zip"
    Then o sistema deve rejeitar o upload
    And deve exibir uma mensagem informando o erro

  Scenario: Arquivo de jogo enviado está livre de malware
    Given que um desenvolvedor enviou um arquivo .zip para a plataforma
    When o sistema de verificação de segurança analisa o arquivo "SEGURO"
    And nenhum malware é detectado
    Then o sistema deve aceitar o arquivo para a próxima etapa de validação

  Scenario: Arquivo de jogo enviado contém malware
    Given que um desenvolvedor enviou um arquivo .zip para a plataforma
    When o sistema de verificação de segurança analisa o arquivo "NAO SEGURO"
    And detecta uma assinatura de malware
    Then o arquivo enviado deve ser descartado
    And deve exibir uma mensagem informando o erro

  Scenario: Jogo passa em todas as validações e é salvo com sucesso
    Given que eu sou um usuário logado com o perfil de "DESENVOLVEDORA"
    When eu tento enviar o arquivo "jogo-jogo-jogo_1.0.0.zip"
    And o arquivo passou na validação de nome e formato
    And o arquivo passou na verificação de malware
    Then o arquivo "jogo-jogo-jogo_1.0.0.zip" deve ser salvo no banco de dados
    And metadados como ID da versao e data do upload devem ser armazenados