-- ============================================================
-- Fase 6 — Derruba tabelas legadas do modelo mono-tenant:
--          brands, models, cars, users.
--
-- Pré-condição: nenhuma entidade JPA referencia essas tabelas.
--
-- CASCADE derruba junto quaisquer FKs remanescentes que apontem
-- para elas (nesta fase, nenhuma — service_orders foi recriado
-- em V5 sem referências a cars/users).
-- ============================================================

DROP TABLE IF EXISTS cars CASCADE;
DROP TABLE IF EXISTS models CASCADE;
DROP TABLE IF EXISTS brands CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Sequences órfãs — remover para não ficar lixo no schema
DROP SEQUENCE IF EXISTS cars_id_seq;
DROP SEQUENCE IF EXISTS models_id_seq;
DROP SEQUENCE IF EXISTS brands_id_seq;
DROP SEQUENCE IF EXISTS users_id_seq;
