# AutoManager — Arquitetura

Documento de referência técnica para desenvolvedores e agentes de IA.

> **Última revisão de arquitetura:** 2026-07-10 — MVP multi-tenant com gestão de ordens de serviço.

## Visão geral

AutoManager é um **SaaS multi-tenant** para oficinas mecânicas e funilarias gerenciarem o fluxo interno de trabalho.

**MVP:** gestão de ordens de serviço (OS) com avaliação pelo gestor, execução pelos funcionários, histórico completo e atualização de telas em tempo quase real.

Fluxo resumido:

1. Carro chega na oficina
2. **Gestor** avalia e define os itens de trabalho (catálogo + texto livre)
3. **Funcionários** executam e marcam itens como concluídos
4. Funcionário pode **propor** novos itens → gestor **aprova ou rejeita**
5. Sistema registra **histórico** (quem fez o quê e quando)
6. Telas atualizam via **SSE** quando algo muda

---

## Decisões de arquitetura

Registro consolidado das decisões tomadas. A IA deve tratar isto como fonte de verdade até nova revisão.

| # | Tema | Decisão | Notas |
|---|------|---------|-------|
| 1 | Escopo | **Multi-tenant (SaaS)** | Várias oficinas; dados isolados por `workshop_id` |
| 2 | Fluxo de trabalho | **OS + itens** | Gestor define na avaliação; funcionário marca como feito; histórico de eventos |
| 3 | Catálogo de serviços | **Catálogo + texto livre** | Gestor escolhe do catálogo da oficina ou descreve livremente |
| 4 | Edição após avaliação | **Gestor edita livremente** | Funcionário pode propor itens → exige aprovação do gestor |
| 5 | Carros e clientes | **Cadastro simplificado + snapshot** | `customers` + `vehicles`; **sem** `brands`/`models`; snapshot na OS |
| 6 | Autenticação | **JWT + refresh token** | Token carrega `userId`, `workshopId`, `role` |
| 7 | Tempo real | **SSE** | Push servidor → cliente; mutações via REST |
| 8 | Frontend | **Web responsivo (PWA)** | Um frontend; layouts por papel (gestor vs funcionário) |
| 9 | Modularidade | **Adiado** | `workshop_modules` quando existir 2º módulo comercial |

### Papéis no fluxo

| Papel | Responsabilidades |
|-------|-------------------|
| **Gestor** | Criar OS, avaliar carro, definir/editar itens, atribuir funcionários, aprovar/rejeitar propostas |
| **Funcionário** | Ver itens atribuídos, iniciar/concluir trabalho, propor novos itens |
| **Sistema** | Persistir mudanças, gravar histórico, publicar eventos SSE |

### Tipos de oficina suportados

| Tipo | Como o sistema atende |
|------|----------------------|
| **Funilaria** | 1 OS com vários itens em sequência (funileiro → preparador → pintor) |
| **Mecânica geral** | 1 OS com um ou poucos itens para um mecânico |
| **Misto** | Itens de especialidades diferentes na mesma OS |

---

## Stack técnica (alvo MVP)

| Camada | Tecnologia |
|--------|------------|
| Backend | Java 21, Spring Boot 3.4, Spring Data JPA |
| Banco | PostgreSQL + Flyway |
| API | REST + OpenAPI (SpringDoc) |
| Auth | Spring Security, JWT + refresh token, BCrypt |
| Tempo real | SSE (`text/event-stream`) |
| Frontend | PWA responsivo (futuro) |
| Mapeamento | MapStruct |

### Fluxo técnico de atualização em tempo real

```
Cliente A: REST (ex. PATCH /items/5/complete)
      ↓
Service persiste + grava evento no histórico
      ↓
SseEmitter notifica clientes conectados da oficina
      ↓
Clientes B, C atualizam a tela
```

---

## Fluxo de negócio detalhado

```
┌──────────────┐
│ Carro chega  │
└──────┬───────┘
       ▼
┌──────────────────────────────────────┐
│ OS criada (status: EVALUATION)       │
│ Gestor preenche snapshot cliente/    │
│ veículo (do cadastro ou na hora)     │
└──────┬───────────────────────────────┘
       ▼
┌──────────────────────────────────────┐
│ Gestor define itens de trabalho      │
│ (catálogo + texto, atribui pessoas)  │
└──────┬───────────────────────────────┘
       ▼
┌──────────────────────────────────────┐
│ OS → IN_PROGRESS                     │
│ Funcionários executam itens          │
└──────┬───────────────────────────────┘
       │
       ├── Funcionário propõe item → PENDING_APPROVAL
       │         ↓
       │   Gestor aprova → PENDING  /  rejeita → REJECTED
       │
       ▼
┌──────────────────────────────────────┐
│ Todos os itens COMPLETED             │
│ OS → COMPLETED                       │
└──────────────────────────────────────┘

Cada transição gera registro em service_order_item_events
```

---

## Diagrama de camadas

```
┌─────────────────────────────────────────────────────────┐
│              Cliente (PWA — futuro) / Postman            │
└─────────────────────────┬───────────────────────────────┘
                          │ REST + SSE
┌─────────────────────────▼───────────────────────────────┐
│  Controller + Security Filter (JWT, workshop_id)         │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│  Service Layer (regras de negócio, eventos SSE)          │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│  Repository Layer (Spring Data JPA)                      │
└─────────────────────────┬───────────────────────────────┘
                          │
┌─────────────────────────▼───────────────────────────────┐
│  PostgreSQL (Flyway migrations)                          │
└─────────────────────────────────────────────────────────┘

     Transversal:
     ├── Mapper (MapStruct)
     ├── Exception Handler
     ├── OpenAPI (documentação)
     └── SSE (notificações por oficina)
```

---

## Modelo de dados — diagrama ER (alvo MVP)

```mermaid
erDiagram
    workshops ||--o{ workshop_specialties : has
    workshops ||--o{ users : employs
    workshops ||--o{ customers : has
    workshops ||--o{ vehicles : has
    workshops ||--o{ service_catalog : has
    workshops ||--o{ service_orders : has

    users ||--o{ user_specialties : has
    workshop_specialties ||--o{ user_specialties : assigned

    customers ||--o{ vehicles : owns
    vehicles ||--o{ service_orders : referenced

    users ||--o{ service_orders : evaluates
    service_orders ||--o{ service_order_items : contains
    service_orders ||--o{ service_order_item_events : logs

    service_catalog ||--o{ service_order_items : optional
    workshop_specialties ||--o{ service_order_items : optional
    users ||--o{ service_order_items : assigned
    users ||--o{ service_order_items : proposed
    service_order_items ||--o{ service_order_item_events : optional

    workshops {
        int id PK
        varchar name
        varchar document UK
        boolean active
        timestamptz created_at
        timestamptz updated_at
    }

    workshop_specialties {
        int id PK
        int workshop_id FK
        varchar name UK
        boolean active
    }

    users {
        int id PK
        int workshop_id FK
        varchar name
        varchar login UK
        varchar password
        varchar role
        boolean active
        timestamptz created_at
        timestamptz updated_at
    }

    user_specialties {
        int user_id PK_FK
        int specialty_id PK_FK
    }

    service_catalog {
        int id PK
        int workshop_id FK
        varchar name UK
        text description
        decimal default_price
        boolean active
    }

    customers {
        int id PK
        int workshop_id FK
        varchar name
        varchar document UK
        varchar email
        varchar phone
        varchar address
        timestamptz created_at
        timestamptz updated_at
    }

    vehicles {
        int id PK
        int workshop_id FK
        int customer_id FK
        varchar plate UK
        varchar make_model
        int manufacture_year
        varchar color
        timestamptz created_at
        timestamptz updated_at
    }

    service_orders {
        int id PK
        int workshop_id FK
        int vehicle_id FK
        int customer_id FK
        int evaluated_by FK
        varchar status
        text evaluation_notes
        varchar customer_name
        varchar customer_phone
        varchar customer_email
        varchar customer_document
        varchar vehicle_plate
        varchar vehicle_description
        int vehicle_year
        varchar vehicle_color
        decimal estimate
        decimal final_value
        timestamptz created_at
        timestamptz updated_at
        timestamptz closed_at
    }

    service_order_items {
        int id PK
        int service_order_id FK
        int service_catalog_id FK
        text description
        int specialty_id FK
        int assigned_user_id FK
        int proposed_by_id FK
        int sequence_order
        varchar status
        timestamptz created_at
        timestamptz updated_at
        timestamptz started_at
        timestamptz completed_at
    }

    service_order_item_events {
        int id PK
        int service_order_id FK
        int service_order_item_id FK
        varchar event_type
        int performed_by FK
        timestamptz occurred_at
        text notes
        varchar previous_status
        varchar new_status
    }
```

> **UK** = unique por oficina (`UNIQUE (workshop_id, coluna)`), exceto `workshops.document` que é global por tenant.

---

## Modelo de dados — colunas por tabela

### `workshops`

Oficina (tenant). Raiz do isolamento de dados.

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| `id` | `INTEGER` | PK, sequence | Identificador |
| `name` | `VARCHAR(100)` | NOT NULL | Nome da oficina |
| `document` | `VARCHAR(14)` | UNIQUE, nullable | CNPJ ou identificador fiscal |
| `active` | `BOOLEAN` | NOT NULL, default true | Oficina ativa |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | Criação |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL | Última atualização |

---

### `workshop_specialties`

Especialidades configuráveis por oficina (ex.: Funileiro, Preparador, Pintor, Mecânico).

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| `id` | `INTEGER` | PK | |
| `workshop_id` | `INTEGER` | FK → `workshops`, NOT NULL | |
| `name` | `VARCHAR(50)` | NOT NULL | Nome da especialidade |
| `active` | `BOOLEAN` | NOT NULL, default true | |

**Índices:** `UNIQUE (workshop_id, name)`

---

### `users`

Funcionários e gestores de cada oficina.

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| `id` | `INTEGER` | PK | |
| `workshop_id` | `INTEGER` | FK → `workshops`, NOT NULL | |
| `name` | `VARCHAR(100)` | NOT NULL | Nome completo |
| `login` | `VARCHAR(50)` | NOT NULL | Login de acesso |
| `password` | `VARCHAR(255)` | NOT NULL | Hash BCrypt |
| `role` | `VARCHAR(30)` | NOT NULL | `MANAGER` ou `EMPLOYEE` (evoluir conforme necessário) |
| `active` | `BOOLEAN` | NOT NULL, default true | |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL | |

**Índices:** `UNIQUE (workshop_id, login)`

---

### `user_specialties`

Vínculo N:N entre usuário e especialidades da oficina.

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| `user_id` | `INTEGER` | PK, FK → `users` | |
| `specialty_id` | `INTEGER` | PK, FK → `workshop_specialties` | |

---

### `service_catalog`

Catálogo de serviços da oficina (usado na avaliação pelo gestor).

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| `id` | `INTEGER` | PK | |
| `workshop_id` | `INTEGER` | FK → `workshops`, NOT NULL | |
| `name` | `VARCHAR(100)` | NOT NULL | Nome do serviço (ex.: Pintura, Troca de óleo) |
| `description` | `TEXT` | nullable | Detalhes padrão |
| `default_price` | `DECIMAL(10,2)` | nullable | Preço sugerido |
| `active` | `BOOLEAN` | NOT NULL, default true | |

**Índices:** `UNIQUE (workshop_id, name)`

---

### `customers`

Clientes da oficina (cadastro simplificado).

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| `id` | `INTEGER` | PK | |
| `workshop_id` | `INTEGER` | FK → `workshops`, NOT NULL | |
| `name` | `VARCHAR(100)` | NOT NULL | |
| `document` | `VARCHAR(11)` | nullable | CPF |
| `email` | `VARCHAR(100)` | nullable | Para NF futura |
| `phone` | `VARCHAR(20)` | nullable | Contato |
| `address` | `VARCHAR(200)` | nullable | |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL | |

**Índices:** `UNIQUE (workshop_id, document)` onde `document` não nulo

---

### `vehicles`

Veículos (substitui `cars` + `brands` + `models`).

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| `id` | `INTEGER` | PK | |
| `workshop_id` | `INTEGER` | FK → `workshops`, NOT NULL | |
| `customer_id` | `INTEGER` | FK → `customers`, NOT NULL | Proprietário |
| `plate` | `VARCHAR(10)` | NOT NULL | Placa |
| `make_model` | `VARCHAR(100)` | NOT NULL | Ex.: "Toyota Corolla" |
| `manufacture_year` | `INTEGER` | nullable | Ano |
| `color` | `VARCHAR(30)` | nullable | Cor |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL | |

**Índices:** `UNIQUE (workshop_id, plate)`

---

### `service_orders`

Ordem de serviço (cabeçalho) com **snapshot** de cliente e veículo no momento da avaliação.

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| `id` | `INTEGER` | PK | |
| `workshop_id` | `INTEGER` | FK → `workshops`, NOT NULL | |
| `vehicle_id` | `INTEGER` | FK → `vehicles`, nullable | Link ao cadastro (se existir) |
| `customer_id` | `INTEGER` | FK → `customers`, nullable | Link ao cadastro (se existir) |
| `evaluated_by` | `INTEGER` | FK → `users`, nullable | Gestor que avaliou |
| `status` | `VARCHAR(30)` | NOT NULL | Ver enum abaixo |
| `evaluation_notes` | `TEXT` | nullable | Notas da avaliação inicial |
| **Snapshot cliente** | | | |
| `customer_name` | `VARCHAR(100)` | NOT NULL | |
| `customer_phone` | `VARCHAR(20)` | nullable | |
| `customer_email` | `VARCHAR(100)` | nullable | |
| `customer_document` | `VARCHAR(11)` | nullable | |
| **Snapshot veículo** | | | |
| `vehicle_plate` | `VARCHAR(10)` | NOT NULL | |
| `vehicle_description` | `VARCHAR(100)` | NOT NULL | make/model em texto |
| `vehicle_year` | `INTEGER` | nullable | |
| `vehicle_color` | `VARCHAR(30)` | nullable | |
| **Financeiro** | | | |
| `estimate` | `DECIMAL(10,2)` | nullable | Orçamento |
| `final_value` | `DECIMAL(10,2)` | nullable | Valor final |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL | |
| `closed_at` | `TIMESTAMPTZ` | nullable | Conclusão da OS |

**Status da OS (`service_orders.status`):**

| Valor | Descrição |
|-------|-----------|
| `EVALUATION` | Aguardando/definindo avaliação |
| `IN_PROGRESS` | Itens em execução |
| `ON_HOLD` | Pausada (peças, cliente, etc.) |
| `COMPLETED` | Concluída |
| `CANCELLED` | Cancelada |

**Índices:** `idx_service_orders_workshop_id`, `idx_service_orders_vehicle_plate (workshop_id, vehicle_plate)`

---

### `service_order_items`

Cada serviço/etapa dentro da OS.

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| `id` | `INTEGER` | PK | |
| `service_order_id` | `INTEGER` | FK → `service_orders`, NOT NULL | |
| `service_catalog_id` | `INTEGER` | FK → `service_catalog`, nullable | Se veio do catálogo |
| `description` | `TEXT` | NOT NULL | Descrição (catálogo + complemento ou texto livre) |
| `specialty_id` | `INTEGER` | FK → `workshop_specialties`, nullable | Especialidade necessária |
| `assigned_user_id` | `INTEGER` | FK → `users`, nullable | Responsável |
| `proposed_by_id` | `INTEGER` | FK → `users`, nullable | Quem propôs (se funcionário) |
| `sequence_order` | `INTEGER` | nullable | Ordem na pipeline (funilaria) |
| `status` | `VARCHAR(30)` | NOT NULL | Ver enum abaixo |
| `created_at` | `TIMESTAMPTZ` | NOT NULL | |
| `updated_at` | `TIMESTAMPTZ` | NOT NULL | |
| `started_at` | `TIMESTAMPTZ` | nullable | Início da execução |
| `completed_at` | `TIMESTAMPTZ` | nullable | Conclusão |

**Status do item (`service_order_items.status`):**

| Valor | Descrição |
|-------|-----------|
| `PENDING_APPROVAL` | Proposto por funcionário; aguarda gestor |
| `REJECTED` | Gestor rejeitou a proposta |
| `PENDING` | Aprovado/definido; não iniciado |
| `IN_PROGRESS` | Em execução |
| `COMPLETED` | Concluído |
| `CANCELLED` | Cancelado pelo gestor |

**Índices:** `idx_service_order_items_order_id`, `idx_service_order_items_assigned_user`

---

### `service_order_item_events`

Histórico imutável de eventos (auditoria e timeline do carro/OS).

| Coluna | Tipo | Restrições | Descrição |
|--------|------|------------|-----------|
| `id` | `INTEGER` | PK | |
| `service_order_id` | `INTEGER` | FK → `service_orders`, NOT NULL | |
| `service_order_item_id` | `INTEGER` | FK → `service_order_items`, nullable | Null para eventos da OS |
| `event_type` | `VARCHAR(30)` | NOT NULL | Ver enum abaixo |
| `performed_by` | `INTEGER` | FK → `users`, NOT NULL | Quem executou a ação |
| `occurred_at` | `TIMESTAMPTZ` | NOT NULL | Quando ocorreu |
| `notes` | `TEXT` | nullable | Observação livre |
| `previous_status` | `VARCHAR(30)` | nullable | Status anterior |
| `new_status` | `VARCHAR(30)` | nullable | Novo status |

**Tipos de evento (`event_type`):**

| Valor | Quando |
|-------|--------|
| `ORDER_CREATED` | OS criada |
| `ORDER_STATUS_CHANGED` | Status da OS mudou |
| `ITEM_CREATED` | Gestor criou item na avaliação |
| `ITEM_PROPOSED` | Funcionário propôs item |
| `ITEM_APPROVED` | Gestor aprovou proposta |
| `ITEM_REJECTED` | Gestor rejeitou proposta |
| `ITEM_ASSIGNED` | Funcionário atribuído |
| `ITEM_STARTED` | Trabalho iniciado |
| `ITEM_COMPLETED` | Trabalho concluído |
| `ITEM_CANCELLED` | Item cancelado |
| `ITEM_UPDATED` | Gestor editou item |

**Índices:** `idx_events_order_id`, `idx_events_occurred_at`

---

### Tabelas legadas (código atual — a remover na migração)

| Tabela atual | Destino |
|--------------|---------|
| `brands` | **Remover** — informação vai para `vehicles.make_model` |
| `models` | **Remover** |
| `cars` | **Substituir** por `vehicles` |
| `service_orders` (estrutura atual) | **Evoluir** conforme modelo acima |

---

### Tabelas futuras (adiadas)

| Tabela | Quando |
|--------|--------|
| `workshop_modules` | Ao lançar 2º módulo comercial (feature flags) |
| `refresh_tokens` | Implementação JWT + refresh |
| Tabelas de NF/faturamento | Módulo financeiro |

---

## Enumerações

### Papéis de usuário (`users.role`)

| Valor | Descrição |
|-------|-----------|
| `MANAGER` | Gestor — avalia, edita, aprova |
| `EMPLOYEE` | Funcionário — executa e propõe |

> `RoleEnum` legado (`PAINTER`, `TINSMITH`, etc.) migra para `workshop_specialties` + `user_specialties`.

---

## Código atual vs alvo

| Área | Estado atual | Alvo MVP |
|------|--------------|----------|
| Multi-tenant | ❌ Não existe | `workshops` + `workshop_id` |
| Marcas/modelos | ✅ CRUD `brands`, `models` | Remover; usar `vehicles.make_model` |
| Carros | ⚠️ POST apenas | `vehicles` completo |
| OS / tarefas | ⚠️ Entidade sem API | `service_orders` + `items` + `events` |
| Auth | ❌ Sem segurança | JWT + refresh |
| Tempo real | ❌ | SSE |
| OpenAPI | ✅ | Manter como fonte de verdade |
| PostgreSQL + Flyway | ✅ | Manter |

---

## API REST

Documentação interativa: `http://localhost:8080/swagger-ui.html`

### Endpoints legados (código atual)

| Recurso | Base path | Status |
|---------|-----------|--------|
| Marcas | `/brands` | ✅ — **será removido** |
| Modelos | `/models` | ✅ — **será removido** |
| Clientes | `/customers` | ✅ — evoluir com `workshop_id` |
| Usuários | `/users` | ✅ — evoluir com auth |
| Carros | `/cars` | ⚠️ — substituir por `/vehicles` |
| Ordens de serviço | — | ❌ — implementar |

### Endpoints alvo (MVP)

| Recurso | Base path | Descrição |
|---------|-----------|-----------|
| Auth | `/auth` | login, refresh, logout |
| Oficina | `/workshops` | dados do tenant (admin) |
| Especialidades | `/specialties` | CRUD por oficina |
| Catálogo | `/service-catalog` | CRUD por oficina |
| Clientes | `/customers` | CRUD filtrado por oficina |
| Veículos | `/vehicles` | CRUD filtrado por oficina |
| Ordens de serviço | `/service-orders` | CRUD + avaliação + itens |
| Itens | `/service-orders/{id}/items` | CRUD, concluir, propor |
| Histórico | `/service-orders/{id}/events` | Timeline |
| SSE | `/workshops/{id}/events/stream` | Notificações em tempo real |

---

## Tratamento de erros

Resposta padrão (`GlobalExceptionResponseDTO`):

```json
{
  "code": 404,
  "message": "Recurso não encontrado.",
  "timestamp": "2026-07-10T20:00:00"
}
```

| Exceção | HTTP | Quando |
|---------|------|--------|
| `NotFoundException` | 404 | Recurso inexistente |
| `AlreadyExistsException` | 409 | Duplicidade |
| `InvalidArgumentException` | 400 | Validação de negócio |
| `ForbiddenException` (futuro) | 403 | Sem permissão ou outro tenant |

---

## Ordem de implementação sugerida

```
1. Migration: workshops + workshop_id + novas tabelas
2. Remover brands/models; migrar cars → vehicles
3. Spring Security (JWT + refresh + BCrypt)
4. CRUD service_orders + items + events
5. Fluxo avaliação (gestor) + conclusão (funcionário) + aprovação
6. SSE por oficina
7. Frontend PWA
8. workshop_modules (quando houver 2º módulo)
```

---

## Roadmap

### Fase 1 — Base (concluída parcialmente)

- [x] Documentação para IA
- [x] PostgreSQL + Flyway
- [x] OpenAPI/Swagger
- [x] Decisões de arquitetura MVP documentadas
- [ ] Diagrama ER implementado via migrations

### Fase 2 — Modelo multi-tenant

- [ ] Tabela `workshops` e `workshop_id`
- [ ] `vehicles` substituindo `brands`/`models`/`cars`
- [ ] `workshop_specialties`, `service_catalog`
- [ ] Novo modelo `service_orders` + `items` + `events`

### Fase 3 — Segurança

- [ ] Spring Security + JWT + refresh
- [ ] BCrypt; isolamento por `workshop_id` no token
- [ ] Autorização gestor vs funcionário

### Fase 4 — Core do negócio

- [ ] Fluxo completo de OS (avaliação → execução → conclusão)
- [ ] Proposta de item por funcionário + aprovação
- [ ] Histórico e timeline
- [ ] SSE para atualização de telas

### Fase 5 — Frontend

- [ ] PWA responsivo (gestor + funcionário)

### Fase 6 — Evolução (futuro)

- [ ] Módulos por oficina (`workshop_modules`)
- [ ] Nota fiscal / e-mail
- [ ] Testes de integração

---

## Decisões pendentes

| Decisão | Opções | Status |
|---------|--------|--------|
| Framework frontend | React, Vue, etc. | Pendente |
| Módulos por oficina | Feature flags no banco | Adiado |
| NF / e-mail | Integração futura | Fora do MVP |

---

## Auditoria de qualidade (código legado)

Mapa entre o código **atual** e os princípios de `AGENTS.md`. Válido até a migração para o modelo multi-tenant.

### Violações críticas

| Local | Problema |
|-------|----------|
| `pom.xml` | Spring Security comentado |
| `UserEntity.password` | Texto plano (sem BCrypt) |
| API | Sem autenticação |

### Violações médias

| Local | Problema |
|-------|----------|
| Controllers/Services | `@Autowired` field injection |
| Services | Validação de ID repetida |
| `UserController` | Sem `@Valid` nos bodies |
| `BrandresponseDTO` | Typo no nome da classe |
| `CustomerService.creteCustomer` | Typo no método |

### Pontos positivos (manter)

- Separação Controller → Service → Repository
- DTOs + MapStruct
- Exceções tipadas + `GlobalExceptionHandler`
- Testes unitários com Mockito
- OpenAPI como documentação da API
- Flyway + PostgreSQL

---

## Estratégia de branches

Ver [`AGENTS.md`](../AGENTS.md#estratégia-de-branches).

Resumo: `develop` (trabalho diário) → `main` (estável).

---

## Referências internas

- [`AGENTS.md`](../AGENTS.md) — guia completo para agentes
- [`.cursor/rules/`](../.cursor/rules/) — regras por contexto
- [`README.md`](../README.md) — início rápido
