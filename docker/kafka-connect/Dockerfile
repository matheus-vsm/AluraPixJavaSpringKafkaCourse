# Etapa de download: as ferramentas de extracao nao ficam na imagem final.
FROM postgres:18.6 AS plugins

ARG DEBEZIUM_VERSION=3.6.2.Final
ARG JDBC_VERSION=10.9.7

RUN apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl unzip \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /plugins/debezium-postgresql \
    && curl --fail --location --retry 3 \
       "https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/${DEBEZIUM_VERSION}/debezium-connector-postgres-${DEBEZIUM_VERSION}-plugin.tar.gz" \
       -o /tmp/debezium.tar.gz \
    && tar -xzf /tmp/debezium.tar.gz -C /plugins/debezium-postgresql --strip-components=1 \
    && curl --fail --location --retry 3 \
       "https://hub-downloads.confluent.io/api/plugins/confluentinc/kafka-connect-jdbc/versions/${JDBC_VERSION}/confluentinc-kafka-connect-jdbc-${JDBC_VERSION}.zip" \
       -o /tmp/jdbc.zip \
    && unzip -q /tmp/jdbc.zip -d /plugins \
    && rm /tmp/debezium.tar.gz /tmp/jdbc.zip

FROM confluentinc/cp-kafka-connect:8.3.1

COPY --from=plugins --chown=appuser:appuser /plugins/ /usr/share/confluent-hub-components/
