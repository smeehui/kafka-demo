package org.smee;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.smee.config.KafkaConfiguration;

@Slf4j
public class Producer {
    public static void main(String[] args) throws InterruptedException {
        KafkaConfiguration config = new KafkaConfiguration();
        KafkaProducer<String, String> producer = new KafkaProducer<>(config.getProperties());
        for (int i = 0; i < 100; i++) {
            ProducerRecord<String, String> record = new ProducerRecord<>("demo_java", "Hello, index: " + i);
            producer.send(record, (meta, exception) -> {
                log.info("Send record: {}", record);
                log.info("Meta data: {}", meta);
            });
            Thread.sleep(10);
        }
        producer.flush();
        producer.close();
    }
}