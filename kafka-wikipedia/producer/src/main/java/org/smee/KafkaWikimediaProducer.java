package org.smee;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.smee.config.KafkaConfiguration;

@Slf4j
public class KafkaWikimediaProducer {
    private WikimediaKafkaEventHandler wikimediaKafkaEventHandler;

    public void produce() {
        KafkaConfiguration con = new KafkaConfiguration();
        log.info("Config: {}", con.getProperties());
        KafkaProducer<String, String> producer = new KafkaProducer<>(con.getProperties());
        wikimediaKafkaEventHandler = new WikimediaKafkaEventHandler(producer, con.getProperties().getProperty("wikipedia.topic"));
        wikimediaKafkaEventHandler.subscribe();
    }

    public void close() {
        wikimediaKafkaEventHandler.close();
    }
}