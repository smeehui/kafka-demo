package org.smee;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.smee.config.KafkaConfiguration;

import java.time.Duration;
import java.util.Collections;

@Slf4j
public class Consumer {
    public static void main(String[] args) {
        KafkaConfiguration config = new KafkaConfiguration();
        try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(config.getProperties())) {
            consumer.subscribe(Collections.singleton("demo_java"));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    log.info("Header data: {}", record.headers());
                    log.info("Key data: {}", record.key());
                    log.info("Value data: {}", record.value());
                    log.info("Partition data: {}", record.partition());
                    log.info("Offset data: {}", record.offset());
                    log.info("Timestamp data: {}", record.timestamp());
                    log.info("======================================");
                }
            }
        }
    }
}
