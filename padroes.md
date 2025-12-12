# Padrões de Projeto Implementados

---

## Padrões escolhidos

| Nome | Padrão Implementado | Funcionalidade |
|------|---------------------|----------------|
| **Ana** | Strategy | Publicação de Jogos |
| **Sophia** | Template Method | - |
| **Gabriel** | Observer | - |
| **Luan** | Decorator | - |
| **Matheus** | Iterator | - |

---

## 📑 Índice
- [Ana - Strategy](#ana--strategy---publicação-de-jogos)
- [Sophia - Template Method](#)
- [Gabriel - Observer](#)
- [Luan - Decorator](#)
- [Matheus - Iterator](#)

---

## Ana | Strategy - Publicação de Jogos

### 📋 Contexto
O sistema Ludum precisa gerenciar diferentes estados de publicação de jogos (Em Upload, Aguardando Validação, Publicado, Rejeitado, Arquivado), onde cada estado possui regras de validação e transições específicas. O **Strategy Pattern** foi implementado para encapsular os comportamentos de cada estado e permitir que o sistema execute diferentes estratégias de forma dinâmica.

### 🎯 Problema Resolvido
Evitar condicionais complexas (`if-else` ou `switch-case`) para determinar o comportamento de cada estado de publicação. Cada estado agora possui sua própria classe com lógica isolada, facilitando manutenção e extensão.

### 🏗️ Estrutura da Implementação

Foi criada a pasta de estratégias no catálogo, para criar a interface que cada respectiva estratégia irá utilizar.

```
dominio-principal/src/main/java/org/ludum/dominio/catalogo/jogo/estrategias/
├── EstrategiaPublicacao.java           # (interface)
├── PublicacaoEmUpload.java             # Impl EM_UPLOAD
├── PublicacaoAguardandoValidacao.java  # Impl AGUARDANDO_VALIDACAO
├── PublicacaoPublicado.java            # Impl PUBLICADO
├── PublicacaoRejeitado.java            # Impl REJEITADO
└── PublicacaoArquivado.java            # Impl ARQUIVADO
```

#### **Service Modificado** (contexto que utiliza as estratégias)
```
dominio-principal/src/main/java/org/ludum/dominio/catalogo/jogo/services/
└── PublicacaoService.java (utiliza as estratégias)
```

#### **Controller** (camada de apresentação)
```
backend/src/main/java/org/ludum/backend/apresentacao/controllers/
└── PublicacaoController.java (endpoints REST)
```

### 🔄 Fluxo de Execução

1. **Criação do Jogo**: `POST /jogos/publicar`
   - Estado inicial: `EM_UPLOAD`
   - Validação: `PublicacaoEmUpload`
   - Transição: `EM_UPLOAD → AGUARDANDO_VALIDACAO`

2. **Validação Automática** (após 2 horas)
   - Validação: `PublicacaoAguardandoValidacao`
   - Transição: `AGUARDANDO_VALIDACAO → PUBLICADO`

3. **Rejeição Manual**: `POST /jogos/{id}/rejeitar`
   - Validação: `PublicacaoAguardandoValidacao`
   - Transição: `AGUARDANDO_VALIDACAO → REJEITADO`

4. **Arquivamento**: `POST /jogos/{id}/arquivar`
   - Validação: `PublicacaoPublicado`
   - Transição: `PUBLICADO → ARQUIVADO`

### 📦 Responsabilidades de Cada Estratégia

| Estratégia | Status | Validações | Transição |
|-----------|--------|-----------|-----------|
| `PublicacaoEmUpload` | EM_UPLOAD | Título, descrição, capa, tags, screenshots | → AGUARDANDO_VALIDACAO |
| `PublicacaoAguardandoValidacao` | AGUARDANDO_VALIDACAO | Validação leve | → PUBLICADO |
| `PublicacaoRejeitado` | REJEITADO | Bloqueia republicação | Nenhuma |
| `PublicacaoPublicado` | PUBLICADO | Verifica propriedade do desenvolvedor | → ARQUIVADO |
| `PublicacaoArquivado` | ARQUIVADO | Bloqueia todas operações | Nenhuma |

### ✅ Benefícios da Implementação

- **Open/Closed Principle**: Adicionar novos estados não requer modificar código existente
- **Single Responsibility**: Cada estratégia tem uma responsabilidade clara
- **Testabilidade**: Cada estratégia pode ser testada isoladamente
- **Manutenibilidade**: Regras de cada estado centralizadas em uma classe
- **Eliminação de condicionais**: Sem `if-else` para determinar comportamento por estado

---

## Sophia | Template Method


---

## Gabriel | Observer


---

## Luan | Decorator


---

## Matheus | Iterator

