# AutoManager — Guia para Agentes de IA

Sistema de gestão de oficina automotiva. Backend REST em Java/Spring Boot.

## Regras de Git — OBRIGATÓRIO

**Nunca execute operações de Git sem aprovação explícita do usuário.**

Isso inclui — mas não se limita a:

- `git commit` (incluindo `--amend`)
- `git push` / `git pull`
- `git merge` / `git rebase`
- Criar ou deletar branches (`git checkout -b`, `git branch -d`)
- `git stash`, `git reset`, `git cherry-pick`
- Abrir PR (`gh pr create`)

### Comportamento esperado

1. **Antes** de qualquer operação Git, **informe o que pretende fazer** e **aguarde aprovação**
2. Apresente: comando(s), branch afetada, mensagem de commit proposta (se aplicável)
3. Só execute após o usuário confirmar explicitamente ("pode commitar", "faz o push", etc.)
4. Se o usuário não pediu Git, **não sugira commit ao final** — apenas informe o que foi alterado

```
❌ ERRADO: implementar feature e commitar automaticamente
❌ ERRADO: "Fiz as alterações e já commitei para você"
✅ CORRETO: "Alterações prontas. Posso commitar em develop com a mensagem 'feat(Car): ...'?"
```

## Stack

| Item | Versão / Detalhe |
|------|------------------|
| Java | 21 |
| Spring Boot | 3.4.1 |
| Banco (dev) | H2 em memória |
| ORM | Spring Data JPA |
| Mapeamento | MapStruct 1.6.3 |
| Testes | JUnit 5 + Mockito |
| Build | Maven (`mvnw.cmd` no Windows) |

## Comandos

```bash
# Compilar e rodar testes
.\mvnw.cmd test

# Subir aplicação
.\mvnw.cmd spring-boot:run

# Console H2 (com app rodando)
# http://localhost:8080/h2-console
# JDBC: jdbc:h2:mem:dcbapp | user: sa | password: password
```

**Pré-requisito:** `JAVA_HOME` apontando para JDK 21.

## Estratégia de branches

Projeto solo — fluxo simplificado (sem release/hotfix branches).

| Branch | Papel |
|--------|-------|
| **`main`** | Versão estável. Só recebe merge de `develop` quando um marco está pronto e testado |
| **`develop`** | Branch principal do dia a dia — commits diretos aqui |
| **`feature/*`** | Opcional. Usar apenas para tarefas grandes, arriscadas ou experimentais |

### Fluxo

```
feature/xxx  →  develop  →  main
     ↑              ↑
  (opcional)    (trabalho diário)
```

### Regras para o agente

1. **Trabalho normal** (quick fixes, CRUDs pequenos, docs) → commit em `develop`
2. **Tarefa grande** (ex.: Spring Security, CRUD de Task, refatoração ampla) → criar `feature/nome-da-tarefa` a partir de `develop`; merge em `develop` ao concluir
3. **Nunca commitar direto em `main`** — `main` só via merge de `develop`
4. **Merge `develop` → `main`** apenas quando o marco estiver estável (testes passando, funcionalidade completa)
5. **Não criar** release branches, hotfix branches nem PRs obrigatórios (projeto solo)
6. **Nunca** commit, push, merge ou qualquer operação Git sem aprovação explícita do usuário (ver seção [Regras de Git](#regras-de-git--obrigatório))

### Convenção de nomes

```
feature/car-crud
feature/task-crud
feature/spring-security
fix/user-validation
docs/api-swagger
```

### Commits

Quando o usuário **pedir** um commit, usar formato convencional em português: `feat:`, `fix:`, `test:`, `refactor:`, `docs:`

Exemplo: `feat(Car): adiciona endpoint de listagem`

## Arquitetura

Camadas em fluxo unidirecional — **nunca pule camadas**:

```
HTTP Request
    → Controller   (entrada/saída HTTP, validação de entrada)
    → Service      (regras de negócio, orquestração)
    → Repository   (acesso a dados)
    → Entity       (modelo de persistência JPA)
```

DTOs separam contrato da API do modelo interno:

- `dto/request/*RequestDTO` — entrada
- `dto/response/*ResponseDTO` — saída
- `mapper/*Mapper` (MapStruct) — conversão Entity ↔ DTO

Exceções de domínio em `exception/`; respostas HTTP padronizadas em `GlobalExceptionHandler`.

## Princípios obrigatórios

Todo código novo **e refatorações** devem respeitar:

### SOLID

| Princípio | Aplicação neste projeto |
|-----------|-------------------------|
| **S** — Single Responsibility | Controller só HTTP; Service só negócio; Repository só persistência |
| **O** — Open/Closed | Estenda via novos services/handlers; evite modificar lógica existente para casos novos |
| **L** — Liskov Substitution | Contratos de interface (Repository, Mapper) devem ser substituíveis sem quebrar callers |
| **I** — Interface Segregation | Repositories enxutos; não crie interfaces "god object" |
| **D** — Dependency Inversion | Dependa de abstrações (`BrandRepository`), não de implementações concretas |

### Clean Code

- Nomes expressivos em inglês para código; mensagens de erro ao usuário em português
- Métodos curtos (< 20 linhas ideal); uma responsabilidade por método
- Evite comentários óbvios; comente apenas regras de negócio não triviais
- Trate `Optional` corretamente — prefira `.orElseThrow()` a `if (entity == null)` após `findById`
- Remova código morto, checks redundantes e typos em nomes públicos

### DRY (Don't Repeat Yourself)

- Validação de ID repetida → extrair para método privado ou validator reutilizável
- Padrão CRUD repetido → seguir template existente (Brand/Model) sem copiar/colar cegamente
- MapStruct para conversões; nunca monte DTO manualmente no service se já existe mapper

### Padrões de projeto aplicáveis

| Padrão | Onde |
|--------|------|
| **MVC** (adaptado) | Controller = View/Controller; Service = lógica; Entity = Model |
| **Repository** | `*Repository extends JpaRepository` |
| **DTO** | Separação API ↔ domínio |
| **Mapper** | MapStruct para transformações |
| **Exception Handler** | `@RestControllerAdvice` centralizado |

## Convenções do projeto

### Pacotes

```
org.workshop.automanager
├── controller/
├── service/
├── repository/
├── model/          # Entities JPA
├── dto/request/
├── dto/response/
├── mapper/
├── exception/
└── enums/
```

### Nomenclatura

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| Entity | `*Entity` | `BrandEntity` |
| Repository | `*Repository` | `BrandRepository` |
| Service | `*Service` | `BrandService` |
| Controller | `*Controller` | `BrandController` |
| Request DTO | `*RequestDTO` | `BrandRequestDTO` |
| Response DTO | `*ResponseDTO` | `BrandResponseDTO` |
| Mapper | `*Mapper` | `BrandMapper` |
| Teste | `*Test` | `BrandServiceTest` |

### Controller

- `@RestController` + `@RequestMapping("/recurso-no-plural")`
- Injetar dependência via **construtor** (preferido) ou `@Autowired` (legado existente)
- `@Valid` em todo `@RequestBody` de entrada
- Retornos: `201 Created` (POST), `200 OK` (GET), `204 No Content` (PUT/DELETE sem body)
- Controller **não** contém regra de negócio

### Service

- `@Service`; transações quando necessário (`@Transactional`)
- Validar IDs: `null` ou `<= 0` → `InvalidArgumentException`
- Recurso inexistente → `NotFoundException`
- Duplicidade → `AlreadyExistsException`
- Métodos internos `get*EntityById()` para uso entre services — **sem endpoint**
- Senhas: devem ser hasheadas (BCrypt) — ver débito técnico

### Repository

- `extends JpaRepository<Entity, Integer>`
- Queries derivadas do nome do método (`existsByName`, `findByCpf`)
- Sem lógica de negócio

### DTOs

- Bean Validation (`@NotNull`, `@NotBlank`, `@Size`, `@Email`) nos Request DTOs
- Response DTOs expõem apenas campos necessários ao cliente
- Nunca exponha Entity diretamente na API

### Testes

- Testes unitários de Service com `@ExtendWith(MockitoExtension.class)`
- `@Mock` para dependências; `@InjectMocks` para classe testada
- Cobrir: caminho feliz, not found, invalid argument, already exists
- Ver ` .cursor/rules/testing.mdc` para detalhes

## Template para novo CRUD

Ao implementar um recurso (ex.: Task), crie nesta ordem:

1. Entity (se não existir)
2. Repository
3. RequestDTO + ResponseDTO (com validações)
4. Mapper (MapStruct)
5. Service (regras de negócio)
6. Controller (endpoints REST)
7. Testes unitários do Service

Endpoints padrão:

```
POST   /recurso          → criar
GET    /recurso/{id}     → buscar por ID
GET    /recurso          → listar todos
PUT    /recurso/{id}     → atualizar
DELETE /recurso/{id}     → remover
```

## Débitos técnicos conhecidos (prioridade de correção)

Use esta lista para validar/refatorar código existente:

| # | Problema | Princípio violado | Ação esperada |
|---|----------|-------------------|---------------|
| 1 | Spring Security desabilitado | Segurança | Habilitar + BCrypt para senhas |
| 2 | Senha em texto plano | Segurança | `PasswordEncoder` |
| 3 | `@Autowired` em field injection | DI / testabilidade | Migrar para constructor injection |
| 4 | Typo `BrandresponseDTO` | Clean Code | Renomear para `BrandResponseDTO` |
| 5 | Typo `creteCustomer` | Clean Code | Renomear para `createCustomer` |
| 6 | `UserController` sem `@Valid` | Validação na borda | Adicionar `@Valid` |
| 7 | Check redundante após `orElseThrow` | Clean Code | Remover dead code |
| 8 | WebSocket no pom sem uso | YAGNI | Implementar ou remover dependência |
| 9 | `data.sql` DDL manual vs JPA | Consistência | Unificar estratégia de schema |
| 10 | Car CRUD incompleto | — | Finalizar seguindo template |
| 11 | Task sem implementação | — | Implementar (core do negócio) |

## Documentação de referência

- `docs/ARCHITECTURE.md` — visão técnica, domínio, endpoints, roadmap
- `.cursor/rules/java-backend.mdc` — regras Java/Spring detalhadas
- `.cursor/rules/testing.mdc` — padrões de teste

## Idioma

- **Código** (classes, métodos, variáveis): inglês
- **Mensagens de erro ao usuário**: português
- **Commits**: português, formato convencional (`feat:`, `fix:`, `test:`, `refactor:`)
