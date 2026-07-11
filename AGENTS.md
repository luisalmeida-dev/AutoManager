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
✅ CORRETO: "Alterações prontas. Posso commitar em develop com a mensagem abaixo?"
```
feat(db): migra para PostgreSQL com Flyway

- Substitui H2 por PostgreSQL em dev
- Adiciona V1__INITIAL_SCHEMA.sql
- Documenta convenções de migrations na base de IA
```
```

## Stack

| Item | Versão / Detalhe |
|------|------------------|
| Java | 21 |
| Spring Boot | 3.4.1 |
| Banco (dev) | PostgreSQL (Docker local) |
| Migrations | Flyway (`src/main/resources/db/migration/`) |
| ORM | Spring Data JPA |
| Mapeamento | MapStruct 1.6.3 |
| Testes | JUnit 5 + Mockito |
| Build | Maven (`mvnw.cmd` no Windows) |

## Comandos

```bash
# Compilar e rodar testes
.\mvnw.cmd test

# Subir aplicação (requer PostgreSQL rodando + application-local.properties)
.\mvnw.cmd spring-boot:run
```

**Pré-requisitos:** `JAVA_HOME` apontando para JDK 21; PostgreSQL local; `application-local.properties` configurado (copiar de `application-local.properties.example`).

## Configuração da aplicação — OBRIGATÓRIO

Valores sensíveis ou específicos de ambiente **nunca** vão direto no `application.properties` (commitado no Git).

### Regra

1. **`application.properties`** (Git) — estrutura fixa com **placeholders** `${NOME_VARIAVEL}`
2. **`application-local.properties`** (gitignored) — valores reais do ambiente local
3. **`application-local.properties.example`** (Git) — template sem credenciais reais; atualizar ao adicionar novos placeholders

```properties
# ✅ application.properties (commitado)
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}

# ✅ application-local.properties (NÃO commitar)
DB_URL=jdbc:postgresql://localhost:5432/automanager
DB_USER=postgres
DB_PASSWORD=sua_senha
```

### Ao adicionar nova configuração

1. Defina placeholder em `application.properties`: `minha.config=${MINHA_CONFIG}`
2. Adicione o valor em `application-local.properties`
3. Adicione entrada no `application-local.properties.example` (sem valor real)
4. Em produção, use variáveis de ambiente do servidor com o **mesmo nome** do placeholder

```
❌ ERRADO: spring.datasource.password=357753 no application.properties
❌ ERRADO: host/IP de produção hardcoded no application.properties
✅ CORRETO: spring.datasource.url=${DB_URL} + valor em application-local.properties ou env var
```

## Banco de dados e Flyway — OBRIGATÓRIO

Schema gerenciado exclusivamente pelo **Flyway**. Hibernate usa `spring.jpa.hibernate.ddl-auto=validate` — não cria nem altera tabelas.

### Migrations

- Local: `src/main/resources/db/migration/`
- Formato: `V{versão}__{DESCRIÇÃO}.sql`
- **Descrição sempre em UPPERCASE** (snake_case após o `__`)

```
✅ V1__INITIAL_SCHEMA.sql
✅ V3__ADD_PLATE_UNIQUE_CONSTRAINT.sql
❌ V2__fix_manufacture_year_type.sql
❌ V4__add-column.sql
```

### Regras

1. **Nunca editar** migration já aplicada — criar nova versão (`V{N+1}__...`)
2. Uma alteração de schema por migration (quando possível)
3. **Toda migration deve ser idempotente** — executar o script várias vezes não pode falhar nem corromper o schema
4. Histórico em `flyway_schema_history` — não manipular manualmente

### Idempotência — OBRIGATÓRIO

Mesmo que o Flyway rode cada migration uma vez, o SQL deve ser seguro para reexecução (dev, recovery, debug manual).

| Operação | Padrão idempotente |
|----------|-------------------|
| Criar tabela | `CREATE TABLE IF NOT EXISTS` |
| Criar índice | `CREATE INDEX IF NOT EXISTS` |
| Criar sequence | `CREATE SEQUENCE IF NOT EXISTS` |
| Adicionar coluna | `ADD COLUMN IF NOT EXISTS` (PostgreSQL 9.1+) |
| Inserir dado seed | `INSERT ... ON CONFLICT DO NOTHING` |
| Constraint / FK | Bloco `DO $$ ... IF NOT EXISTS` consultando `information_schema` ou `pg_constraint` |

```sql
-- ✅ Adicionar coluna de forma idempotente
ALTER TABLE customers ADD COLUMN IF NOT EXISTS notes TEXT;

-- ✅ Criar índice idempotente
CREATE INDEX IF NOT EXISTS idx_customers_email ON customers (email);

-- ✅ Constraint condicional (PostgreSQL)
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uk_customers_email'
    ) THEN
        ALTER TABLE customers ADD CONSTRAINT uk_customers_email UNIQUE (email);
    END IF;
END $$;
```

```
❌ ERRADO: CREATE TABLE brands (...) — falha se tabela já existir
❌ ERRADO: ALTER TABLE customers ADD COLUMN notes TEXT — falha se coluna já existir
✅ CORRETO: CREATE TABLE IF NOT EXISTS / ADD COLUMN IF NOT EXISTS / bloco DO $$
```

### Ao alterar estrutura do banco

1. Criar `V{N}__DESCRICAO_EM_UPPERCASE.sql`
2. Subir a aplicação — Flyway aplica automaticamente
3. Garantir que entidades JPA continuam alinhadas com o schema (validate)

### Nomenclatura de tabelas — OBRIGATÓRIO

Tabelas e sequences seguem **plural snake_case**, sem sufixos (`_tb`, `_table`):

| Tipo | Padrão | Exemplo |
|------|--------|---------|
| Tabela | plural, snake_case | `brands`, `customers`, `service_orders` |
| Coluna FK | `{entidade_singular}_id` | `brand_id`, `customer_id` |
| Sequence | `{tabela}_id_seq` | `brands_id_seq`, `service_orders_id_seq` |
| Constraint UNIQUE | `uk_{tabela}_{coluna}` | `uk_brands_name` |
| Constraint FK | `fk_{tabela}_{referencia}` | `fk_cars_model` |
| Índice | `idx_{tabela}_{coluna}` | `idx_cars_customer_id` |

```
✅ brands, models, customers, cars, users, service_orders
❌ brand_tb, customer_table, User
❌ task_tb (usar service_orders para ordens de serviço)
```

Ao criar nova entidade:
1. Escolher nome de tabela em **plural snake_case**
2. Mapear com `@Table(name = "nome_plural")` na Entity
3. Criar sequence `{tabela}_id_seq` na migration Flyway
4. Evitar palavras reservadas do PostgreSQL (`user` → `users`)

### Relacionamentos N:N — OBRIGATÓRIO

Bancos relacionais **não** suportam N:N diretamente. Toda relação muitos-para-muitos deve ser quebrada em duas relações **1:N** através de uma **tabela de junção** (pivô/associativa) que cruza os IDs das duas entidades.

- Nome no plural unindo as entidades: `employee_specialties`, `catalog_service_specialties`
- FK para cada lado + `UNIQUE` no par para evitar duplicidade
- Em multi-tenant: incluir `workshop_id` e garantir que as duas pontas são da mesma oficina
- Preferir surrogate `id` a PK composta (facilita histórico/eventos)
- Colunas extras podem qualificar a relação (`assigned_by_employee_id`, `role`, `started_at`)

```
❌ ERRADO: specialty_1_id, specialty_2_id (colunas repetidas)
❌ ERRADO: JSON/array de IDs sem FK (perde integridade referencial)
✅ CORRETO: tabela `{a}_{b}` com FKs + UNIQUE (a_id, b_id)
```

Detalhes completos em `.cursor/rules/database-migrations.mdc`.

```
❌ ERRADO: alterar V1 já aplicada
❌ ERRADO: usar ddl-auto=update em produção
✅ CORRETO: V3__ADD_EMAIL_UNIQUE_TO_CUSTOMER.sql
```

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

### Commits — OBRIGATÓRIO

**Sempre** seguir Conventional Commits. O padrão completo está **neste arquivo** — não é necessário (nem deve) consultar URLs externas.

> **Para humanos:** inspiração original em [qoomon cheatsheet](https://gist.github.com/qoomon/5dfcdf8eec66a051ecd85625518cfd13) (apenas leitura complementar; a IA segue o que está documentado abaixo).

#### Estrutura da mensagem

```
<tipo>(<escopo opcional>): <descrição>

<corpo opcional — bullet points com mudanças importantes>

<rodapé opcional — breaking changes, referências de issue>
```

Equivalente no Git:

```bash
git commit -m "<tipo>(<escopo>): <descrição>" \
  -m "<corpo com bullet points>" \
  -m "<rodapé se necessário>"
```

Ou via heredoc (preferido para mensagem completa).

#### Tipos (`type`)

| Tipo | Quando usar |
|------|-------------|
| `feat` | Adiciona, ajusta ou remove funcionalidade na API/UI |
| `fix` | Corrige bug na API/UI (geralmente após um `feat`) |
| `refactor` | Reestrutura código sem alterar comportamento da API/UI |
| `perf` | Tipo especial de `refactor` que melhora performance |
| `style` | Formatação, espaços, ponto-e-vírgula — sem mudança de comportamento |
| `test` | Adiciona ou corrige testes |
| `docs` | Apenas documentação |
| `build` | Build, dependências, versão do projeto (Maven, Docker) |
| `ops` | Infraestrutura, CI/CD, deploy, monitoramento |
| `chore` | Tarefas diversas (`.gitignore`, commit inicial, etc.) |

#### Escopo (`scope`)

- **Opcional**, entre parênteses: `feat(Car):`, `fix(db):`
- Definido pelo projeto — usar módulo/domínio (`Car`, `Customer`, `db`, `Task`)
- **Não** usar ID de issue como escopo (`JIRA-123` ❌)

#### Descrição (`description`) — obrigatória

- Imperativo, presente: **"adiciona"** — não "adicionou" nem "adicionando"
- Pensar: *"Este commit vai..."* / *"Este commit deve..."*
- **Não** capitalizar a primeira letra
- **Não** terminar com ponto (`.`)
- Em português

#### Corpo (`body`) — obrigatório neste projeto

No cheatsheet o corpo é opcional; **no AutoManager é obrigatório** com **bullet points** resumindo as mudanças importantes.

- Imperativo, presente
- Separar do título com **linha em branco**
- Motivação ou resumo do que mudou (não listar cada arquivo)

#### Rodapé (`footer`) — quando necessário

- Referência de issues: `Closes #123`, `Fixes JIRA-456`
- **Breaking changes** devem começar com `BREAKING CHANGE:` (obrigatório no rodapé se a descrição não for suficiente)

#### Breaking changes

- Indicar com `!` antes dos `:` no título: `feat(api)!: remove endpoint de listagem`
- Descrever no rodapé:

```
feat(api)!: remove endpoint de listagem de marcas

- Remove GET /brands legacy usado apenas pelo frontend antigo

BREAKING CHANGE: endpoint GET /brands/legacy não existe mais. Usar GET /brands.
```

#### Commits especiais

| Situação | Formato |
|----------|---------|
| Commit inicial | `chore: init` |
| Merge | `Merge branch '<nome>'` (padrão Git) |
| Revert | `Revert "<assunto do commit revertido>"` (padrão Git) |

#### Regras para o agente

1. **Sempre** Conventional Commits — sem exceção
2. Título + corpo com bullets ao propor commit ao usuário
3. Aguardar aprovação explícita antes de `git commit`
4. Mensagens em **português**
5. **Nunca** incluir menção a Cursor, IA, agente, Copilot ou ferramenta na mensagem de commit — o commit deve parecer escrito por um desenvolvedor humano

#### Exemplos (padrão AutoManager)

```
feat(Car): adiciona endpoint de listagem

- Implementa GET /cars no CarController
- Adiciona findAll no CarService e CarRepository
- Inclui testes unitários do service
```

```
feat(db): migra de H2 para PostgreSQL com Flyway

- Substitui data.sql por migrations versionadas
- Renomeia tabelas para plural snake_case
- Configura placeholders em application.properties
```

```
fix(Customer): corrige validação de CPF nulo

- Retorna InvalidArgumentException antes de consultar o banco
- Adiciona teste parametrizado para CPF inválido
```

```
docs: atualiza convenções de commit na base de IA

- Documenta padrão Conventional Commits completo no AGENTS.md
- Cria regra git-commits.mdc no Cursor
```

```
❌ ERRADO: "adiciona flyway e postgres"
❌ ERRADO: Adiciona flyway (maiúscula na descrição)
❌ ERRADO: feat: migra banco. (ponto final)
❌ ERRADO: feat(db): migra banco (sem corpo com bullets)
❌ ERRADO: feat(api): adiciona endpoint (feito pelo Cursor)
❌ ERRADO: Co-authored-by: Cursor / menção a IA no corpo ou rodapé
✅ CORRETO: feat(db): migra para PostgreSQL com Flyway + bullets no corpo
```

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
├── config/         # Configuração (OpenAPI, etc.)
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
| Tabela (SQL) | plural snake_case | `brands`, `service_orders` |
| Sequence | `{tabela}_id_seq` | `brands_id_seq` |
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

### Documentação da API (OpenAPI) — OBRIGATÓRIO

**OpenAPI/Swagger é a fonte de verdade** da documentação e contrato da API. Não manter coleção Postman versionada no Git.

| Recurso | URL (app rodando) |
|---------|-------------------|
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| Spec OpenAPI (JSON) | `http://localhost:8080/v3/api-docs` |

**Ao criar ou alterar endpoints:**

1. Adicionar `@Tag(name = "...", description = "...")` no controller
2. Adicionar `@Schema` com `description` e `example` nos campos dos **Request DTOs**
3. Novos controllers devem aparecer agrupados no Swagger UI automaticamente
4. **Não** duplicar documentação em arquivos externos — o código gera a spec

**Importar no Postman:** Postman → Import → Link → `http://localhost:8080/v3/api-docs`

Configuração central em `config/OpenApiConfig.java` e `application.properties` (`springdoc.*`).

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
| 9 | ~~`data.sql` DDL manual vs JPA~~ | — | ✅ Resolvido — Flyway + `ddl-auto=validate` |
| 10 | Car CRUD incompleto | — | Finalizar seguindo template |
| 11 | Task sem implementação | — | Implementar (core do negócio) |

## Documentação de referência

- `docs/ARCHITECTURE.md` — visão técnica, domínio, endpoints, roadmap
- `.cursor/rules/java-backend.mdc` — regras Java/Spring detalhadas
- `.cursor/rules/application-config.mdc` — placeholders e `application-local.properties`
- `.cursor/rules/database-migrations.mdc` — Flyway e nomenclatura UPPERCASE
- `.cursor/rules/git-commits.mdc` — Conventional Commits e formato da mensagem
- `.cursor/rules/testing.mdc` — padrões de teste

## Idioma

- **Código** (classes, métodos, variáveis): inglês
- **Mensagens de erro ao usuário**: português
- **Commits**: português, Conventional Commits — título + corpo com bullets (ver seção Commits neste arquivo)
