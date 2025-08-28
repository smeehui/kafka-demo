FROM confluentinc/cp-kafka-connect:7.7.0

# Cài plugin Debezium Postgres
RUN curl -sSL https://repo1.maven.org/maven2/io/debezium/debezium-connector-postgres/2.7.0.Final/debezium-connector-postgres-2.7.0.Final-plugin.tar.gz \
    | tar -xvz -C /usr/share/java/