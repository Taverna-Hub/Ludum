Feature: Sistema de Postagem
  Como um usuário da plataforma
  Quero criar, editar e interagir com posts sobre jogos
  Para compartilhar minhas experiências e opiniões

  Scenario: Criar post com título válido
    Given que sou um usuário autenticado
    And quero criar um post sobre um jogo
    When preencho o título com "Minha análise do jogo"
    And preencho o conteúdo com "Este jogo é incrível! Adorei a jogabilidade."
    And adiciono as tags "Aventura" e "Indie"
    And publico o post
    Then o post deve ser criado com sucesso
    And o post deve ter status "PUBLICADO"

  Scenario: Falha ao criar post sem título
    Given que sou um usuário autenticado
    And quero criar um post sobre um jogo
    When preencho o título com ""
    And preencho o conteúdo com "Conteúdo do post"
    And adiciono a tag "Ação"
    And publico o post
    Then devo receber um erro informando "Título não pode ser vazio"

  Scenario: Falha ao editar post com título vazio
    Given que tenho um post publicado
    And que sou o autor do post
    When edito o título para ""
    And edito o conteúdo para "Conteúdo válido"
    And salvo as alterações
    Then a operação deve falhar
    And devo receber um erro informando "Título não pode ser vazio"

  Scenario: Criar post com conteúdo válido
    Given que sou um usuário autenticado
    And quero criar um post sobre um jogo
    When preencho o título com "Post com imagem"
    And preencho o conteúdo com "Confira essa imagem legal!"
    And adiciono a tag "Screenshot"
    And adiciono a imagem "screenshot.png" de tamanho 2MB
    And publico o post
    Then o post deve ser criado com sucesso

  Scenario: Falha ao criar post com conteúdo muito longo
    Given que sou um usuário autenticado
    And quero criar um post sobre um jogo
    When preencho o título com "Título válido"
    And preencho o conteúdo com 501 caracteres
    And adiciono a tag "Análise"
    And publico o post
    Then a operação deve falhar
    And devo receber um erro informando "excede o limite de 500 caracteres"

  Scenario: Criar post com quantidade válida de tags
    Given que sou um usuário autenticado
    And quero criar um post sobre um jogo
    When preencho o título com "Análise completa"
    And preencho o conteúdo com "Conteúdo da análise"
    And adiciono as tags "Aventura" e "Indie"
    And publico o post
    Then o post deve ser criado com sucesso

  Scenario: Falha ao criar post sem tags
    Given que sou um usuário autenticado
    And quero criar um post sobre um jogo
    When preencho o título com "Título válido"
    And preencho o conteúdo com "Conteúdo válido"
    And não adiciono nenhuma tag
    And publico o post
    Then devo receber um erro informando "Post deve ter pelo menos 1 tag"

  Scenario: Falha ao criar post com muitas tags
    Given que sou um usuário autenticado
    And quero criar um post sobre um jogo
    When preencho o título com "Título válido"
    And preencho o conteúdo com "Conteúdo válido"
    And adiciono as tags "RPG", "Aventura", "Indie", "Estratégia", "Ação" e "Simulação"
    And publico o post
    Then devo receber um erro informando "Post não pode ter mais de 5 tags"


  Scenario: Curtir um post
    Given que existe um post publicado
    When um usuário curte o post
    Then o número de curtidas deve ser 1

  Scenario: Não permitir curtir o mesmo post duas vezes
    Given que um usuário já curtiu o post
    When o mesmo usuário tenta curtir novamente
    Then a operação deve falhar
    And devo receber um erro informando "já curtiu"

  Scenario: Descurtir um post
    Given que já curtí um post
    When descurto o post
    Then a operação deve ser bem-sucedida
    And o número de curtidas deve ser 0

  Scenario: Falha ao descurtir post não curtido
    Given que existe um post publicado
    And que não curtí o post
    When tento descurtir o post
    Then a operação deve falhar
    And devo receber um erro informando "não curtiu este post"

  Scenario: Adicionar comentário a um post
    Given que existe um post publicado
    When um usuário adiciona um comentário "Ótimo post!"
    Then o número de comentários deve ser 1
    And o comentário deve conter o texto "Ótimo post!"

  Scenario: Falha ao adicionar comentário vazio
    Given que existe um post publicado
    When um usuário adiciona um comentário ""
    Then a operação deve falhar
    And devo receber um erro informando "não pode ser vazio"

  Scenario: Remover comentário como autor do post
    Given que existe um post com comentário de outro usuário
    And que sou o autor do post
    When removo o comentário como dono do post
    Then o comentário deve ser removido com sucesso
    And o número de comentários deve ser 0

  Scenario: Falha ao remover comentário sem autorização
    Given que existe um comentário em post de outro autor
    And que não sou o autor do comentário nem do post
    When tento remover o comentário
    Then a operação deve falhar
    And devo receber um erro informando "autor do comentário ou do post"

  Scenario: Editar post com sucesso
    Given que tenho um post publicado
    And que sou o autor do post
    When edito o título para "Título Atualizado"
    And edito o conteúdo para "Conteúdo Atualizado"
    And salvo as alterações
    Then o post deve ser atualizado com sucesso
    And o título deve ser "Título Atualizado"
    And o conteúdo deve ser "Conteúdo Atualizado"

  Scenario: Falha ao editar post de outro autor
    Given que existe um post de outro autor
    When tento editar o título para "Título Hackeado"
    And tento editar o conteúdo para "Conteúdo Hackeado"
    And tento salvar as alterações
    Then a operação deve falhar
    And devo receber um erro informando "Apenas o autor pode editar"


  Scenario: Remover post como autor
    Given que tenho um post publicado
    And que sou o autor do post
    When removo o post
    Then o post deve ser removido com sucesso

  Scenario: Falha ao remover post de outro autor
    Given que tenho um post publicado
    And que não sou o autor do post
    When removo o post
    Then a operação deve falhar
    And devo receber um erro de autorização


  Scenario: Criar rascunho de post
    Given que sou um usuário autenticado
    And quero criar um post sobre um jogo
    When crio um rascunho com título "Rascunho" e conteúdo "Conteúdo em desenvolvimento"
    And adiciono a tag "Desenvolvimento"
    Then o rascunho deve ser salvo
    And o post deve ter status "EM_RASCUNHO"

  Scenario: Publicar rascunho com sucesso
    Given que tenho um rascunho
    And que sou o autor do post
    When publico o rascunho
    Then o rascunho deve ser publicado com sucesso
    And o post deve ter status "PUBLICADO"


  Scenario: Agendar post dentro do prazo permitido
    Given que tenho um rascunho
    And que sou o autor do post
    When agendo o post para publicar em 5 horas
    Then o post deve ser agendado com sucesso
    And o post deve ter status "AGENDADO"

  Scenario: Falha ao agendar post fora do prazo permitido
    Given que tenho um rascunho
    And que sou o autor do post
    When agendo o post para publicar em 48 horas
    Then a operação deve falhar
    And devo receber um erro informando "não pode ultrapassar 24 horas"


  Scenario: Buscar posts por tag
    Given que existem posts com a tag "RPG"
    When busco posts pela tag "RPG"
    Then devo encontrar 2 post(s)

  Scenario: Buscar por tag sem resultados
    Given que não existem posts com a tag "TagInexistente"
    When busco posts pela tag "TagInexistente"
    Then devo encontrar 0 post(s)


  Scenario: Criar post com imagem válida
    Given que sou um usuário autenticado
    And quero criar um post sobre um jogo
    When preencho o título com "Post com imagem"
    And preencho o conteúdo com "Confira essa imagem legal!"
    And adiciono a tag "Screenshot"
    And adiciono a imagem "screenshot.png" de tamanho 2MB
    And publico o post
    Then o post deve ser criado com sucesso

  Scenario: Falha ao criar post com imagem infectada por malware
    Given que sou um usuário autenticado
    And quero criar um post sobre um jogo sem tag +18
    When preencho o título com "Post com imagem"
    And preencho o conteúdo com "Tentando adicionar malware"
    And adiciono a tag "Gameplay"
    And adiciono a imagem "virus.exe.png" infectada com malware
    And publico o post
    Then a operação deve falhar
    And devo receber um erro informando "malware detectado"
