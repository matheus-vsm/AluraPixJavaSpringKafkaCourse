# Ambiente local: PostgreSQL e Kafka Connect

Versoes verificadas em 14/09/2026: PostgreSQL 18.6, Confluent Platform 8.3.1,
Debezium PostgreSQL 3.6.2.Final e Confluent JDBC 10.9.7.
Os dois plugins sao baixados durante o build e ficam instalados na imagem do Connect.
O driver JDBC do PostgreSQL ja acompanha o plugin JDBC.

## Iniciar

Na raiz do repositorio:

```powershell
docker compose up -d --build
docker compose ps
Invoke-RestMethod http://localhost:8083/connector-plugins
```

A lista deve incluir `io.debezium.connector.postgresql.PostgresConnector`,
`io.confluent.connect.jdbc.JdbcSourceConnector` e
`io.confluent.connect.jdbc.JdbcSinkConnector`.

O banco fica em `localhost:5432/postgres`. Usuario e senha padrao: `postgres`.
Para as aplicacoes Spring, configure `DB_USER_POSTGRES=postgres` e
`DB_PASSWORD_POSTGRES=postgres` no ambiente de execucao da IDE ou terminal.
O Compose tambem aceita essas variaveis para personalizar as credenciais na
primeira inicializacao do volume. Altera-las depois nao muda a senha ja gravada.
Se outro Postgres estiver usando a porta 5432, pare essa instancia antes de subir.
Este servico cria um banco novo; dados de outro Postgres nao sao migrados.

## Ajustes em relacao ao curso

- O Kafka usa KRaft, sem ZooKeeper.
- O Postgres oficial usa `pgoutput`, sem instalar ou carregar `decoderbufs`.
- `wal_level=logical`, `max_wal_senders=10` e `max_replication_slots=10` sao
  aplicados pelo Compose; nao e necessario editar `postgresql.conf`.
- Ao criar o conector Debezium nas proximas aulas, use `plugin.name=pgoutput`,
  `database.hostname=postgres`, `database.port=5432`, `database.dbname=postgres`
  e as credenciais configuradas. Use `topic.prefix` no lugar da antiga propriedade
  `database.server.name`, caso ela apareca no material.
- Para JDBC dentro do Connect, use `jdbc:postgresql://postgres:5432/postgres`.
- O Schema Registry fica em `http://localhost:8085` no Windows e
  `http://schema-registry:8081` dentro dos containers.

Os plugins estao instalados, mas as instancias dos conectores e as tabelas a
capturar ainda devem ser configuradas nas proximas aulas. O usuario inicial do
Postgres e superusuario, suficiente para replicacao neste ambiente local de estudo.

## Verificar replicacao

```powershell
docker compose exec postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SHOW wal_level;" -c "SHOW max_wal_senders;" -c "SHOW max_replication_slots;"'
```

Os resultados esperados sao `logical`, `10` e `10`.
Os dados persistem em `postgres-data`; `docker compose down` preserva o volume.

## Fontes

- [PostgreSQL: versoes atuais](https://www.postgresql.org/support/versioning/)
- [Debezium 3.6: releases e compatibilidade](https://debezium.io/releases/3.6/)
- [Debezium PostgreSQL: pgoutput e configuracao](https://debezium.io/documentation/reference/3.6/connectors/postgresql.html)
- [Confluent JDBC: changelog](https://docs.confluent.io/kafka-connectors/jdbc/current/changelog.html)
