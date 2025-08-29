package org.smee;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.smee.config.KafkaConfiguration;
import org.smee.dto.Smee;
import org.smee.mapper.SmeeMapper;
import postgres.public$.smee.Envelope;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class Main {
    public static void main(String[] args) {
        KafkaConfiguration con = new KafkaConfiguration();
        SmeeMapper instance = SmeeMapper.INSTANCE;
        ExecutorService executor = Executors.newFixedThreadPool(2);

        executor.submit(() -> {
            startConsumer(
                    con,
                    "postgres.public.smee",
                    () -> new KafkaConsumer<String, Smee>(con.getProperties()),
                    (record) -> {
                        Envelope env = (Envelope) record.value();
                        log.info("before {}", instance.toEntity(env.getBefore()));
                        log.info("after {}", instance.toEntity(env.getAfter()));
                    });
        });

        executor.submit(() -> {
            startConsumer(
                    con,
                    "postgres.public.smee2",
                    () -> new KafkaConsumer<String, Smee>(con.getProperties()),
                    (record) -> {
                        postgres.public$.smee2.Envelope env = (postgres.public$.smee2.Envelope) record.value();
                        log.info("before {}", instance.toEntity(env.getBefore()));
                        log.info("after {}", instance.toEntity(env.getAfter()));
                    });
        });
    }

    private static <T extends org.apache.avro.specific.SpecificRecordBase> void startConsumer(
            KafkaConfiguration con,
            String topic,
            Supplier<KafkaConsumer<?, ?>> consumerSupplier,
            Consumer<ConsumerRecord<?, ?>> consumerFunc
    ) {
        try (KafkaConsumer<?, ?> consumer = consumerSupplier.get()) {
            consumer.subscribe(Collections.singletonList(topic));
            try {
                while (true) {
                    var records = consumer.poll(Duration.ofMillis(1000));
                    for (var record : records) {
                        consumerFunc.accept(record);
                    }
                }
            } catch (Exception e) {
                consumer.unsubscribe();
                consumer.close();
                log.error("Error ", e);
            }
        }
    }
}