# Padrões de Projeto Implementados

---

## Padrões escolhidos

| Nome | Padrão Implementado | Funcionalidade |
|------|---------------------|----------------|
| **Ana** | Strategy | Publicação de Jogos |
| **Sophia** | Template Method | - |
| **Gabriel** | Observer | Review de Jogos |
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

## Gabriel | Observer - Review de Jogos

### 📋 Contexto
O sistema Ludum precisa notificar diferentes partes interessadas quando uma nova review é criada para um jogo (desenvolvedores, sistema de estatísticas, etc.). O **Observer Pattern** foi implementado para desacoplar a lógica de criação de reviews da lógica de notificação, permitindo adicionar novos observadores sem modificar o código existente.

### 🎯 Problema Resolvido
Evitar acoplamento forte entre o serviço de reviews e os sistemas que precisam ser notificados quando uma review é criada. Com o Observer, o `ReviewService` não precisa conhecer todos os sistemas que dependem dele - apenas notifica seus observadores registrados.

### 🏗️ Estrutura da Implementação

Foi criada a pasta de observers dentro do módulo de review, contendo a interface e suas implementações.

```
dominio-principal/src/main/java/org/ludum/dominio/comunidade/review/
├── entidades/
│   ├── Review.java                     # Entidade de review
│   └── ReviewId.java                   # Value Object do ID
├── enums/
│   └── StatusReview.java               # Status da review
├── observer/
│   ├── ReviewObserver.java             # (interface) Observer
│   └── NotificacaoDesenvolvedorObserver.java  # Impl - Notifica desenvolvedor
├── repositorios/
│   └── ReviewRepository.java           # Interface do repositório
└── services/
    └── ReviewService.java              # Subject que notifica observadores
```

#### **Controller** (camada de apresentação)
```
backend/src/main/java/org/ludum/backend/apresentacao/controllers/
└── ReviewController.java (endpoints REST)
```

### 🔄 Fluxo de Execução

1. **Registro de Observadores**: Na inicialização do sistema
   - `ReviewService.adicionarObservador(observer)`
   - Observadores são armazenados em uma lista interna

2. **Criação de Review**: `POST /jogos/{jogoId}/reviews`
   - Usuário envia: nota, título, texto, recomendação
   - Validações: jogo publicado, jogo na biblioteca, review única
   - Review é salva no repositório
   - **Notificação**: `notificarObservadores(review)` é chamado

3. **Notificação aos Observadores**:
   - Para cada observador registrado: `observer.quandoNovaReviewCriada(review)`
   - Cada observador executa sua lógica específica

### 📦 Componentes do Padrão

| Componente | Classe | Responsabilidade |
|-----------|--------|------------------|
| **Subject** | `ReviewService` | Mantém lista de observadores e notifica quando review é criada |
| **Observer (Interface)** | `ReviewObserver` | Define contrato `quandoNovaReviewCriada(Review)` |
| **ConcreteObserver** | `NotificacaoDesenvolvedorObserver` | Notifica o desenvolvedor sobre nova review |

### 🔧 Exemplo de Código

**Interface Observer:**
```java
public interface ReviewObserver {
    void quandoNovaReviewCriada(Review review);
}
```

**Subject (ReviewService):**
```java
public class ReviewService {
    private final List<ReviewObserver> observadores = new ArrayList<>();

    public void adicionarObservador(ReviewObserver observer) {
        this.observadores.add(observer);
    }

    private void notificarObservadores(Review review) {
        for (ReviewObserver observer : observadores) {
            observer.quandoNovaReviewCriada(review);
        }
    }

    public void avaliarJogo(...) {
        // ... validações e criação da review
        reviewRepository.salvar(novaReview);
        notificarObservadores(novaReview);  // Notifica todos os observadores
    }
}
```

**ConcreteObserver:**
```java
public class NotificacaoDesenvolvedorObserver implements ReviewObserver {

    private final JogoRepository jogoRepository;

    public NotificacaoDesenvolvedorObserver(JogoRepository jogoRepository) {
        this.jogoRepository = jogoRepository;
    }
    
    @Override
    public void quandoNovaReviewCriada(Review review) {
        Jogo jogo = jogoRepository.obterPorId(review.getJogoId());
        String nomeJogo = jogo != null ? jogo.getTitulo() : review.getJogoId().getValue();
        
        System.out.println("\n========================================");
        System.out.println("📢 NOTIFICAÇÃO PARA DESENVOLVEDOR");
        System.out.println("========================================");
        System.out.println("Seu jogo \"" + nomeJogo + "\" tem uma nova review!");
        System.out.println("Nota: " + review.getNota() + "/5 estrelas");
        System.out.println("Recomenda: " + (review.isRecomendado() ? "Sim ✅" : "Não ❌"));
        System.out.println("========================================\n");
    }
}
```

**Registro do Observer (DominioConfig):**
```java
@Bean
public ReviewService reviewService(
        ReviewRepository reviewRepository,
        JogoRepository jogoRepository,
        BibliotecaRepository bibliotecaRepository) {
    
    ReviewService service = new ReviewService(reviewRepository, jogoRepository, bibliotecaRepository);
    
    // Registrar observer para notificar desenvolvedores sobre novas reviews
    service.adicionarObservador(new NotificacaoDesenvolvedorObserver(jogoRepository));
    
    return service;
}
```

### 📤 Exemplo de Saída no Terminal

Quando uma nova review é criada, o terminal do servidor exibe:
```
========================================
📢 NOTIFICAÇÃO PARA DESENVOLVEDOR
========================================
Seu jogo "Super Adventure" tem uma nova review!
Nota: 4/5 estrelas
Recomenda: Sim ✅
========================================
```

### ✅ Benefícios da Implementação

- **Desacoplamento**: `ReviewService` não conhece os detalhes de quem será notificado
- **Open/Closed Principle**: Adicionar novos observadores não requer modificar o `ReviewService`
- **Single Responsibility**: Cada observador tem uma responsabilidade específica
- **Extensibilidade**: Fácil adicionar novos tipos de notificação (email, push, analytics)
- **Testabilidade**: Observadores podem ser testados isoladamente

### 🚀 Possíveis Extensões

| Observador | Funcionalidade |
|-----------|----------------|
| `EmailNotificacaoObserver` | Envia email ao desenvolvedor |
| `EstatisticasObserver` | Atualiza métricas do jogo (média, total) |
| `ModeracaoObserver` | Envia reviews para fila de moderação |
| `BadgeObserver` | Concede conquistas ao autor da review |

---

## Luan | Decorator


---

## Matheus | Iterator

