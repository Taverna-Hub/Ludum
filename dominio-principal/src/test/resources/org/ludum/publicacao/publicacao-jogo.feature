Feature: Publicação de Jogo
  Como desenvolvedor, quero publicar meus jogos na plataforma
  Para que jogadores possam encontrá-los e jogá-los

  Background:
    Given que sou um desenvolvedor com conta ativa

  Scenario: Publicar jogo com sucesso
    Given que criei um jogo com título "Aventura Espacial"
    And adicionei a descrição "Um jogo emocionante no espaço sideral"
    And adicionei a capa oficial
    And adicionei 1 screenshot
    And adicionei a tag "Aventura"
    When publico o jogo
    Then o jogo deve ser publicado com sucesso
    And o jogo deve ter status "PUBLICADO"

  Scenario: Publicar jogo com vídeo ao invés de screenshot
    Given que criei um jogo com título "Corrida Espacial"
    And adicionei a descrição "Corra pelo espaço em alta velocidade"
    And adicionei a capa oficial
    And adicionei 1 vídeo
    And adicionei a tag "Corrida"
    When publico o jogo
    Then o jogo deve ser publicado com sucesso
    And o jogo deve ter status "PUBLICADO"

  Scenario: Publicar jogo adulto com tag +18
    Given que criei um jogo com título "Jogo Adulto"
    And adicionei a descrição "Um jogo para maiores de 18 anos"
    And adicionei a capa oficial
    And adicionei 1 screenshot
    And marquei o jogo como NSFW
    And adicionei a tag "+18"
    And adicionei a tag "Ação"
    When publico o jogo
    Then o jogo deve ser publicado com sucesso
    And o jogo deve ter status "PUBLICADO"

  Scenario: Publicar jogo com múltiplas tags
    Given que criei um jogo com título "RPG Medieval"
    And adicionei a descrição "Um RPG de fantasia medieval épico"
    And adicionei a capa oficial
    And adicionei 1 screenshot
    And adicionei as tags "RPG", "Fantasia", "Medieval", "Singleplayer"
    When publico o jogo
    Then o jogo deve ser publicado com sucesso

  Scenario: Publicar jogo com screenshots e vídeos
    Given que criei um jogo com título "Mundo Aberto"
    And adicionei a descrição "Explore um vasto mundo aberto"
    And adicionei a capa oficial
    And adicionei 3 screenshots
    And adicionei 2 vídeos
    And adicionei a tag "Aventura"
    When publico o jogo
    Then o jogo deve ser publicado com sucesso

  Scenario: Falha ao publicar com conta não desenvolvedora
    Given que sou um jogador com conta ativa
    And que criei um jogo válido
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "Apenas contas de desenvolvedores podem publicar jogos"

  Scenario: Falha ao publicar com conta inativa
    Given que sou um desenvolvedor com conta inativa
    And que criei um jogo válido
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "Apenas contas ativas podem publicar jogos"


  Scenario: Falha ao publicar jogo sem título
    Given que criei um jogo sem título
    And adicionei a descrição "Uma descrição válida"
    And adicionei a capa oficial
    And adicionei 1 screenshot
    And adicionei a tag "Aventura"
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "deve ter um título"

  Scenario: Falha ao publicar jogo sem descrição
    Given que criei um jogo com título "Jogo Sem Descrição"
    And não adicionei descrição
    And adicionei a capa oficial
    And adicionei 1 screenshot
    And adicionei a tag "Aventura"
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "deve ter uma descrição"

  Scenario: Falha ao publicar jogo sem capa oficial
    Given que criei um jogo com título "Jogo Sem Capa"
    And adicionei a descrição "Uma descrição válida"
    And não adicionei capa oficial
    And adicionei 1 screenshot
    And adicionei a tag "Aventura"
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "deve ter uma capa oficial"

  Scenario: Falha ao publicar jogo sem screenshot ou vídeo
    Given que criei um jogo com título "Jogo Sem Mídia"
    And adicionei a descrição "Uma descrição válida"
    And adicionei a capa oficial
    And não adicionei screenshots nem vídeos
    And adicionei a tag "Aventura"
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "deve ter pelo menos 1 screenshot ou vídeo"

  Scenario: Falha ao publicar jogo sem tags
    Given que criei um jogo com título "Jogo Sem Tags"
    And adicionei a descrição "Uma descrição válida"
    And adicionei a capa oficial
    And adicionei 1 screenshot
    And não adicionei nenhuma tag
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "deve ter pelo menos 1 tag"


  Scenario: Falha ao publicar jogo adulto sem tag +18
    Given que criei um jogo com título "Jogo Adulto Sem Tag"
    And adicionei a descrição "Um jogo para maiores de 18 anos"
    And adicionei a capa oficial
    And adicionei 1 screenshot
    And marquei o jogo como NSFW
    And adicionei a tag "Ação"
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "deve ter a tag +18"

  Scenario: Falha ao publicar jogo com título duplicado do mesmo desenvolvedor
    Given que já publiquei um jogo com título "Meu Jogo"
    And que criei outro jogo com título "Meu Jogo"
    And adicionei a descrição "Uma descrição válida"
    And adicionei a capa oficial
    And adicionei 1 screenshot
    And adicionei a tag "Aventura"
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "já possui um jogo com este título"

  Scenario: Falha ao publicar jogo com slug duplicada globalmente
    Given que outro desenvolvedor já publicou um jogo com título "Aventura Mundial"
    And que criei um jogo com título "Aventura Mundial"
    And adicionei a descrição "Uma descrição válida"
    And adicionei a capa oficial
    And adicionei 1 screenshot
    And adicionei a tag "Aventura"
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "Já existe outro jogo com esta slug"

  Scenario: Falha ao publicar jogo que já está publicado
    Given que já publiquei um jogo com título "Jogo Publicado"
    When tento publicar o jogo novamente
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "Status atual: PUBLICADO"

  Scenario: Falha ao publicar jogo rejeitado
    Given que criei um jogo com título "Jogo Rejeitado"
    And o jogo foi rejeitado por um moderador
    When tento publicar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "Status atual: REJEITADO"


  Scenario: Falha ao publicar jogo de outro desenvolvedor
    Given que outro desenvolvedor criou um jogo
    When tento publicar o jogo deste desenvolvedor
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "Apenas o desenvolvedor dono pode publicar o jogo"


  Scenario: Falha ao adicionar mais de 10 tags
    Given que criei um jogo com título "Jogo Com Muitas Tags"
    And adicionei a descrição "Uma descrição válida"
    And adicionei a capa oficial
    And adicionei 1 screenshot
    When adiciono 11 tags ao jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "não pode ter mais de 10 tags"


  Scenario: Rejeitar jogo aguardando validação
    Given que criei um jogo aguardando validação
    When um moderador rejeita o jogo com motivo "Conteúdo inadequado"
    Then o jogo deve ser rejeitado
    And o jogo deve ter status "REJEITADO"

  Scenario: Arquivar jogo publicado
    Given que já publiquei um jogo com título "Jogo Para Arquivar"
    When arquivo o jogo
    Then o jogo deve ser arquivado com sucesso
    And o jogo deve ter status "ARQUIVADO"

  Scenario: Falha ao arquivar jogo não publicado
    Given que criei um jogo aguardando validação
    When tento arquivar o jogo
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "Apenas jogos publicados podem ser arquivados"


  Scenario: Falha ao criar jogo com título muito curto
    When tento criar um jogo com título "Ab"
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "pelo menos 3 caracteres"

  Scenario: Falha ao criar jogo com descrição muito curta
    Given que tento criar um jogo com título "Jogo Válido"
    When adiciono a descrição "Curta"
    Then a publicação deve falhar
    And devo receber um erro de publicação informando "pelo menos 10 caracteres"
