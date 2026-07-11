# AutoManager

Sistema de gestão para oficinas automotivas — controle de marcas, modelos, clientes, veículos, funcionários e ordens de serviço.

## Status do projeto

Projeto em fase inicial. Backend REST parcialmente implementado.

| Módulo | Status |
|--------|--------|
| Marcas (Brand) | CRUD completo + testes |
| Modelos (Model) | CRUD completo + testes |
| Clientes (Customer) | CRUD completo + testes |
| Usuários (User) | CRUD completo |
| Carros (Car) | Em andamento (apenas criação) |
| Ordens de serviço (Task) | Apenas entidade/modelo |

## Tecnologias

- Java 21
- Spring Boot 3.4.1
- Spring Data JPA
- PostgreSQL + Flyway
- SpringDoc OpenAPI (Swagger UI)
- MapStruct
- JUnit 5 + Mockito

## Pré-requisitos

- JDK 21 (`JAVA_HOME` configurado)
- Git
- PostgreSQL (Docker ou local) — ver `application-local.properties.example`

## Como rodar

```bash
# Clonar e entrar na branch de desenvolvimento
git checkout develop

# Rodar testes
.\mvnw.cmd test

# Iniciar aplicação
.\mvnw.cmd spring-boot:run
```

A API sobe em `http://localhost:8080`.

**Documentação interativa (Swagger UI):** `http://localhost:8080/swagger-ui.html`

**Spec OpenAPI (importar no Postman):** `http://localhost:8080/v3/api-docs`

Copie `application-local.properties.example` para `application-local.properties` e configure o PostgreSQL antes de subir a aplicação.

## Estrutura

```
src/main/java/org/workshop/automanager/
├── controller/     # Endpoints REST
├── service/        # Regras de negócio
├── repository/     # Acesso a dados
├── model/          # Entidades JPA
├── dto/            # Contratos de entrada/saída
├── mapper/         # Conversão Entity ↔ DTO
├── exception/      # Exceções e handler global
└── enums/          # Enumerações de domínio
```

## Endpoints disponíveis

| Recurso | Base path |
|---------|-----------|
| Marcas | `/brands` |
| Modelos | `/models` |
| Clientes | `/customers` |
| Usuários | `/users` |
| Carros | `/cars` (parcial) |

Detalhes completos em [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md).

## Desenvolvimento com IA

Este projeto está configurado para uso com agentes de IA no Cursor:

- **`AGENTS.md`** — contexto principal, princípios (SOLID, Clean Code) e convenções
- **`.cursor/rules/`** — regras persistentes por tipo de arquivo
- **`docs/ARCHITECTURE.md`** — arquitetura, domínio e roadmap

## Estratégia de branches

Fluxo simplificado para projeto solo:

| Branch | Papel |
|--------|-------|
| `main` | Versão estável — merge de `develop` quando um marco estiver pronto |
| `develop` | Trabalho diário (branch padrão) |
| `feature/*` | Opcional — tarefas grandes ou experimentais |

```
feature/xxx  →  develop  →  main
```

Detalhes e regras para agentes em [`AGENTS.md`](AGENTS.md#estratégia-de-branches).

## Licença

Projeto pessoal — uso interno.
