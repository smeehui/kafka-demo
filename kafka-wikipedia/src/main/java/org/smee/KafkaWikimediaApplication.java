package org.smee;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
public class KafkaWikimediaApplication {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        KafkaWikimediaProducer producer = new KafkaWikimediaProducer();
        OpenSearchConsumer consumer = new OpenSearchConsumer();
        executor.execute(producer::produce);
        executor.execute(consumer::consume);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down...");
            producer.close();
            consumer.close();
            executor.shutdown();
            try {
                if (executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }
}
