Feature: Upload e Validação de Jogos
  Para disponibilizar meus jogos de forma segura e padronizada na plataforma,
  Como um desenvolvedor verificado,
  Eu quero enviar arquivos de jogo seguindo padrões de nome e formato, que sejam verificados automaticamente e armazenados somente após aprovação.

  Scenario: Desenvolvedor faz upload de um jogo com formato e nome de arquivo corretos
    Given que eu sou um usuário logado com o perfil de "desenvolvedor"
    And o padrão de nome de arquivo definido é "NOMEJOGO_VERSAO.zip"
    When eu envio o arquivo "AventuraCosmica_1.0.0.zip"
    Then o sistema deve aceitar o arquivo para a próxima etapa de validação

  Scenario: Usuário sem permissão de desenvolvedor tenta fazer upload
    Given que eu sou um usuário logado com o perfil de "jogador"
    When eu tento acessar a funcionalidade de upload de jogos
    Then o sistema deve bloquear o meu acesso
    And deve exibir uma mensagem informando que a funcionalidade é restrita para desenvolvedores

  Scenario: Desenvolvedor tenta fazer upload de um arquivo com formato incorreto
    Given que eu sou um usuário logado com o perfil de "desenvolvedor"
    When eu tento enviar o arquivo "AventuraCosmica.rar"
    Then o sistema deve rejeitar o upload
    And deve exibir a mensagem de erro: "Formato de arquivo inválido. Por favor, envie um pacote .zip."

  Scenario: Desenvolvedor tenta fazer upload de um arquivo com nome fora do padrão
    Given que eu sou um usuário logado com o perfil de "desenvolvedor"
    And o padrão de nome de arquivo definido é "NOMEJOGO_VERSAO.zip"
    When eu tento enviar o arquivo "meu_jogo_aventura.zip"
    Then o sistema deve rejeitar o upload
    And deve exibir a mensagem de erro: "O nome do arquivo não segue o padrão 'NOMEJOGO_VERSAO.zip'."

  Scenario: Arquivo de jogo enviado está livre de malware
    Given que um desenvolvedor enviou um arquivo .zip para a plataforma
    When o sistema de verificação de segurança analisa o arquivo
    And nenhum malware é detectado
    Then o arquivo é marcado como "verificado e limpo"
    And o processo de upload continua para a próxima etapa

  Scenario: Arquivo de jogo enviado contém malware
    Given que um desenvolvedor enviou um arquivo .zip
    When o sistema de verificação de segurança analisa o arquivo
    And detecta uma assinatura de malware
    Then o processo de upload deve ser imediatamente cancelado
    And o arquivo enviado deve ser descartado ou movido para quarentena
    And o desenvolvedor deve receber uma mensagem de alerta informando: "Upload cancelado: Malware detectado no arquivo."

  Scenario: Jogo passa em todas as validações e é salvo com sucesso
    Given que um desenvolvedor enviou o arquivo "AventuraCosmica_1.0.zip"
    And o arquivo passou na validação de nome e formato
    And o arquivo passou na validação de estrutura interna
    And o arquivo passou na verificação de malware
    When a plataforma processa o salvamento do jogo
    Then o arquivo "AventuraCosmica_1.0.zip" deve ser salvo no banco de dados
    And metadados como ID do desenvolvedor, data do upload e status "verificado" devem ser armazenados
    And o desenvolvedor deve receber uma notificação de "Upload concluído com sucesso!"

  Scenario: Jogo falha em uma das validações e não é salvo
    Given que um desenvolvedor enviou o arquivo "AventuraCosmica_1.0.zip"
    And o arquivo passou na validação de nome e formato
    But o arquivo foi reprovado na verificação de malware
    When a plataforma tenta finalizar o processo de upload
    Then o arquivo não deve ser salvo no banco de dados principal
    And o status do upload deve ser registrado como "falhou"
