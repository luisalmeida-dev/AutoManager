# AutoManager — Arquitetura

Documento de referência técnica para desenvolvedores e agentes de IA.

## Visão geral

AutoManager gerencia o ciclo de vida de uma oficina automotiva:

1. Cadastrar **marcas** e **modelos** de veículos
2. Cadastrar **clientes** e seus **carros**
3. Registrar **funcionários** (usuários internos)
4. Criar e acompanhar **ordens de serviço** (tasks) vinculadas a carros e mecânicos

## Diagrama de camadas

```
┌─────────────────────────────────────────────────────────┐
│                    Cliente HTTP                          │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│  Controller Layer                                        │
│  BrandController, ModelController, CustomerController,   │
│  UserController, CarController                           │
│  • Mapeia HTTP ↔ DTO                                     │
│  • @Valid na entrada                                     │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│  Service Layer                                           │
│  BrandService, ModelService, CustomerService,            │
│  UserService, CarService                                 │
│  • Regras de negócio                                     │
│  • Orquestra repositories e services auxiliares          │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│  Repository Layer                                        │
│  BrandRepository, ModelRepository, etc.                  │
│  • Spring Data JPA                                       │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│  Database (H2 — dev)                                     │
└─────────────────────────────────────────────────────────┘

     Transversal:
     ├── Mapper (MapStruct)     Entity ↔ DTO
     ├── Exception Handler      Erros → HTTP status
     └── Enums                  StatusEnum, RoleEnum
```

## Modelo de domínio

```
Brand (marca)
  └── Model (modelo) ──┐
                       ├── Car (carro) ── Task (ordem de serviço)
Customer (cliente) ────┘         │
                                 └── User (funcionário responsável)
```

### Entidades

| Entidade | Tabela | Descrição |
|----------|--------|-----------|
| `BrandEntity` | `brand_tb` | Marca automotiva (Toyota, Ford) |
| `ModelEntity` | `model_tb` | Modelo vinculado a uma marca |
| `CustomerEntity` | `customer_tb` | Cliente da oficina (CPF único) |
| `CarEntity` | `car_tb` | Veículo do cliente (placa única) |
| `UserEntity` | `user_tb` | Funcionário interno (login único) |
| `TaskEntity` | `task_tb` | Ordem de serviço |

### Enumerações

**RoleEnum** — papéis no sistema:
- `MANAGER` (Gestor), `PAINTER` (Pintor), `TINSMITH` (Funileiro), `CUSTOMER` (Cliente)

**StatusEnum** — ciclo de vida da ordem de serviço:
- `WAITING_FOR_APPROVAL`, `AWAITING_PARTS`, `IN_REPAIR`, `COMPLETED`, `ON_HOLD`, `BLOCKED`

## API REST

### Marcas — `/brands`

| Método | Path | Descrição | Status |
|--------|------|-----------|--------|
| POST | `/brands` | Criar marca | ✅ |
| GET | `/brands/{id}` | Buscar por ID | ✅ |
| GET | `/brands` | Listar todas | ✅ |
| PUT | `/brands/{id}` | Atualizar | ✅ |
| DELETE | `/brands/{id}` | Remover | ✅ |

### Modelos — `/models`

| Método | Path | Descrição | Status |
|--------|------|-----------|--------|
| POST | `/models` | Criar modelo | ✅ |
| GET | `/models/{id}` | Buscar por ID | ✅ |
| GET | `/models` | Listar todos | ✅ |
| PUT | `/models/{id}` | Atualizar | ✅ |
| DELETE | `/models/{id}` | Remover | ✅ |

### Clientes — `/customers`

| Método | Path | Descrição | Status |
|--------|------|-----------|--------|
| POST | `/customers` | Criar cliente | ✅ |
| GET | `/customers/{id}` | Buscar por ID | ✅ |
| GET | `/customers/cpf/{cpf}` | Buscar por CPF | ✅ |
| GET | `/customers` | Listar todos | ✅ |
| PUT | `/customers` | Atualizar por CPF | ✅ |
| DELETE | `/customers/{cpf}` | Remover por CPF | ✅ |

### Usuários — `/users`

| Método | Path | Descrição | Status |
|--------|------|-----------|--------|
| POST | `/users` | Criar usuário | ✅ |
| GET | `/users/{id}` | Buscar por ID | ✅ |
| GET | `/users` | Listar todos | ✅ |
| PUT | `/users/{id}` | Atualizar | ✅ |
| PUT | `/users/update-password` | Alterar senha | ✅ |
| DELETE | `/users/{id}` | Remover | ✅ |

### Carros — `/cars`

| Método | Path | Descrição | Status |
|--------|------|-----------|--------|
| POST | `/cars` | Criar carro | ⚠️ WIP |
| GET | `/cars/{id}` | Buscar por ID | ❌ |
| GET | `/cars` | Listar todos | ❌ |
| PUT | `/cars/{id}` | Atualizar | ❌ |
| DELETE | `/cars/{id}` | Remover | ❌ |

### Ordens de serviço — `/tasks`

| Método | Path | Descrição | Status |
|--------|------|-----------|--------|
| Todos | — | — | ❌ Não implementado |

## Tratamento de erros

Resposta padrão (`GlobalExceptionResponseDTO`):

```json
{
  "code": 404,
  "message": "Marca não encontrada.",
  "timestamp": "2026-07-09T20:00:00"
}
```

| Exceção | HTTP | Quando |
|---------|------|--------|
| `NotFoundException` | 404 | Recurso inexistente |
| `AlreadyExistsException` | 409 | Duplicidade (nome, CPF, placa, login) |
| `InvalidArgumentException` | 400 | ID inválido, validação de negócio |

## Auditoria de qualidade

Mapa entre o código atual e os princípios definidos em `AGENTS.md`. Use para priorizar refatorações.

### Violações por princípio

#### Single Responsibility (S)

| Local | Problema | Severidade |
|-------|----------|------------|
| `UserService.validatePassword` | Método longo com múltiplas regras | Baixa — candidato a `PasswordValidator` |
| `GlobalExceptionHandler` | Repetição na montagem de response | Baixa — extrair factory method |

#### Open/Closed (O)

| Local | Problema | Severidade |
|-------|----------|------------|
| Validação de ID em cada método | Repetição impede extensão limpa | Média — extrair validator |

#### Dependency Inversion (D)

| Local | Problema | Severidade |
|-------|----------|------------|
| Todos os Controllers/Services | `@Autowired` field injection | Média — migrar para constructor injection |

#### Clean Code

| Local | Problema | Severidade |
|-------|----------|------------|
| `BrandresponseDTO` | Typo no nome da classe | Alta — quebra convenção |
| `CustomerService.creteCustomer` | Typo no nome do método | Alta |
| `UserService.updateUserById` | Check `if (userEntity == null)` após `orElseThrow` | Média — dead code |
| `CustomerService` | Mensagem "O clienta já está registrado" | Baixa — typo |
| `UserController` | Sem `@Valid` nos request bodies | Alta — validação na borda ausente |
| `UserService.updatePassword` | Compara senha em texto plano | Crítica — segurança |

#### DRY

| Local | Problema | Severidade |
|-------|----------|------------|
| Todos os Services | Validação `id == null \|\| id <= 0` repetida | Média |
| `GlobalExceptionHandler` | 3 handlers com estrutura idêntica | Baixa |

#### Segurança

| Local | Problema | Severidade |
|-------|----------|------------|
| `pom.xml` | Spring Security comentado | Crítica |
| `UserEntity.password` | Texto plano | Crítica |
| API | Sem autenticação | Crítica |

#### Consistência arquitetural

| Local | Problema | Severidade |
|-------|----------|------------|
| Customer | CRUD por CPF; demais por ID | Info — decisão de design, documentar |
| `data.sql` | DDL manual + JPA auto-ddl | Média — unificar estratégia |
| `pom.xml` | WebSocket sem implementação | Baixa — YAGNI |

### Pontos positivos (manter)

- Separação clara Controller → Service → Repository
- DTOs separam API de persistência
- MapStruct para mapeamento (sem boilerplate manual)
- Exceções de domínio tipadas com handler centralizado
- Testes unitários com Mockito nos services principais
- Validação Bean Validation nos DTOs (Brand, Model, Customer)
- `existsBy*` antes de criar (prevenção de duplicidade)
- Métodos internos `get*EntityById` para composição entre services

## Roadmap sugerido

### Fase 1 — Base sólida (atual)

- [x] Documentação para IA (AGENTS.md, rules, architecture)
- [x] Branch `develop` como base de trabalho
- [ ] Configurar JDK 21 e validar `mvn test` passando
- [ ] Corrigir typos e dead code (quick wins)
- [ ] Adicionar `@Valid` onde falta

### Fase 2 — Completar domínio

- [ ] Finalizar CRUD de Car (seguir template Brand)
- [ ] Implementar CRUD de Task (core do negócio)
- [ ] Testes unitários para UserService e CarService

### Fase 3 — Segurança

- [ ] Habilitar Spring Security
- [ ] BCrypt para senhas
- [ ] Autenticação JWT ou session-based
- [ ] Autorização por RoleEnum

### Fase 4 — Evolução

- [ ] WebSocket para status de ordens em tempo real
- [ ] OpenAPI/Swagger para documentação da API
- [ ] Migrar H2 → PostgreSQL para produção
- [ ] Testes de integração (`@SpringBootTest`)
- [ ] Frontend (se aplicável)

## Decisões de design pendentes

Registre aqui quando tomar decisões — a IA usará como referência:

| Decisão | Opções | Status |
|---------|--------|--------|
| Estratégia de schema DB | JPA auto-ddl vs Flyway/Liquibase | Pendente |
| Autenticação | JWT vs Session | Pendente |
| Customer por CPF vs ID | Manter CPF ou padronizar ID | Pendente |
| WebSocket | Implementar ou remover dependência | Pendente |

## Estratégia de branches

Ver [`AGENTS.md`](../AGENTS.md#estratégia-de-branches) para regras completas.

Resumo: `develop` (trabalho diário) → `main` (estável). `feature/*` opcional para tarefas grandes.

## Referências internas

- [`AGENTS.md`](../AGENTS.md) — guia completo para agentes
- [`.cursor/rules/`](../.cursor/rules/) — regras por contexto
- [`README.md`](../README.md) — início rápido
