package org.smee;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.opensearch.action.bulk.BulkRequest;
import org.opensearch.action.bulk.BulkResponse;
import org.opensearch.action.index.IndexRequest;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.common.xcontent.XContentType;
import org.smee.config.KafkaConfiguration;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OpenSearchConsumer {
    private final KafkaConfiguration config;
    private final Consumer<String, String> consumer;

    public OpenSearchConsumer() {
        config = new KafkaConfiguration();
        this.consumer = getConsumer();
    }

    public void consume() {
        RestHighLevelClient client = OpenSearchClient.getClient();
        try (client; consumer) {
            OpenSearchClient.checkAndCreateIndex(client);
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                log.info("Consumed {} records", records.count());
                BulkRequest bulkRequest = new BulkRequest();
                for (ConsumerRecord<String, String> record : records) {
                    String id = "%s-%s-%s".formatted(record.topic(), record.partition(), record.offset());
                    if (record.value() == null) {
                        log.warn("Null value for record: {}", record);
                        continue;
                    }
                    IndexRequest request = new IndexRequest(config.getProperties().getProperty("wikipedia.topic"));
                    request.source(record.value(), XContentType.JSON).id(id);
                    bulkRequest.add(request);
                }
                syncToOpenSearch(bulkRequest, client);
                consumer.commitAsync();
                TimeUnit.MICROSECONDS.sleep(1000);
            }
        } catch (WakeupException e) {
            log.error("Wakeup exception", e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            consumer.close();
            try {
                client.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            log.info("Consumer gracefully shut down");
        }
    }

    public void close() {
        consumer.wakeup();
    }

    private void syncToOpenSearch(BulkRequest request, RestHighLevelClient client) throws IOException {
        BulkResponse response = null;
        try {
            if (request.numberOfActions() > 0) {
                response = client.bulk(request, RequestOptions.DEFAULT);
                log.info("indexed {}", response.getItems().length);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (response != null && response.hasFailures()) {
                log.error("Record {}", response.buildFailureMessage());
            }
        }
    }

    public KafkaConsumer<String, String> getConsumer() {
        KafkaConsumer<String, String> cons = new KafkaConsumer<>(config.getProperties());
        cons.subscribe(Collections.singleton((String) config.getProperties().get("wikipedia.topic")));
        return cons;
    }

}