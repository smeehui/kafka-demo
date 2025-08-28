package org.smee;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.smee.config.KafkaConfiguration;
import org.smee.mapper.SmeeMapper;
import postgres.public$.smee.Envelope;

import java.time.Duration;
import java.util.Collections;

@Slf4j
public class Main {
    public static void main(String[] args) {
        KafkaConfiguration con = new KafkaConfiguration();
        try (KafkaConsumer<String, Envelope> consumer = new KafkaConsumer<>(con.getProperties())) {
            consumer.subscribe(Collections.singletonList("postgres.public.smee"));
            SmeeMapper instance = SmeeMapper.INSTANCE;
            while (true) {
                var records = consumer.poll(Duration.ofMillis(1000));
                for (var record : records) {
                    Envelope env = record.value();
                    log.info( "before {}", instance.toEntity(env.getBefore()));
                    log.info( "after {}", instance.toEntity(env.getAfter()));
                }
            }
        }

    }
}