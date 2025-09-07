package org.smee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.smee.config.CustomRebalanceListener;
import org.smee.config.KafkaConfiguration;
import org.smee.dto.LogType;
import org.smee.dto.Logs;
import org.smee.dto.Smee;
import org.smee.mapper.SmeeMapper;
import org.smee.repo.impl.PostgresLogRepo;
import org.smee.services.LogService;
import postgres.public$.smee.Envelope;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class KafkaDemoApplication {
    public static void main(String[] args) {
        KafkaConfiguration con = new KafkaConfiguration();
        SmeeMapper instance = SmeeMapper.INSTANCE;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        ObjectMapper mapper = new ObjectMapper();

        LogService logService = new LogService(new PostgresLogRepo());
//        LogService logService = new LogService(new MongoLogRepo());

        executor.submit(() -> startConsumer(
                "postgres.public.smee",
                () -> new KafkaConsumer<String, Smee>(con.getProperties()),
                (record) -> {
                    Envelope env = (Envelope) record.value();
                    try {
                        if (env == null) {
                            log.info("Null envelope");
                            return;
                        }
                        logService.writeLogAsync(Logs
                                .builder()
                                .log(String.valueOf(env.getBefore() != null ? env.getBefore().getId() : env.getAfter().getId()))
                                .before(mapper.writeValueAsString(instance.toEntity(env.getBefore())))
                                .after(mapper.writeValueAsString(instance.toEntity(env.getAfter())))
                                .logType(LogType.fromString(env.getOp()))
                                .time(new Date())
                                .build()
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }));

        executor.submit(() -> startConsumer(
                "postgres.public.smee2",
                () -> new KafkaConsumer<String, Smee>(con.getProperties()),
                (record) -> {
                    postgres.public$.smee2.Envelope env = (postgres.public$.smee2.Envelope) record.value();
                    try {
                        if (env == null) {
                            log.info("Null envelope");
                            return;
                        }
                        logService.writeLogAsync(Logs
                                .builder()
                                .log(String.valueOf(env.getBefore()!= null ? env.getBefore().getId() : env.getAfter().getId()))
                                .before(mapper.writeValueAsString(instance.toEntity(env.getBefore())))
                                .after(mapper.writeValueAsString(instance.toEntity(env.getAfter())))
                                .logType(LogType.fromString(env.getOp()))
                                .time(new Date())
                                .build()
                        );
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }));
    }

    private static void startConsumer(
            String topic,
            Supplier<KafkaConsumer<?, ?>> consumerSupplier,
            Consumer<ConsumerRecord<?, ?>> consumerFunc
    ) {
        try (KafkaConsumer<?, ?> consumer = consumerSupplier.get()) {
            consumer.subscribe(Collections.singletonList(topic), new CustomRebalanceListener());
            try {
            Set<TopicPartition> assignment = consumer.assignment();
            consumer.seekToEnd(assignment);
                while (true) {
                    var records = consumer.poll(Duration.ofMillis(1000));
                    for (var record : records) {
                        consumerFunc.accept(record);
                    }
                }
            } catch (Exception e) {
                log.error("Error ", e);
            }
        }
    }
}