package org.smee;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.smee.WikimediaEventConstants.*;

@Slf4j
public class WikimediaKafkaEventHandler {
    private final Producer<String, String> kafkaProducer;
    private final String topic;
    private final HttpClient client;
    private final HttpRequest request;

    public WikimediaKafkaEventHandler(Producer<String, String> kafkaProducer, String topic) {
        this.kafkaProducer = kafkaProducer;
        this.topic = topic;
        client = HttpClient.newHttpClient();
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI("https://stream.wikimedia.org/v2/stream/recentchange"))
                    .header("Accept", "text/event-stream")
                    .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:142.0) Gecko/20100101 Firefox/142.0")
                    .build();
        } catch (URISyntaxException e) {
            log.error("Error creating URI", e);
            throw new RuntimeException(e);
        }
    }

    public void subscribe() {
        HttpResponse<InputStream> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
        } catch (IOException | InterruptedException e) {
            this.onError(e);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.body()))) {
            String line;
            var msBody = new WRCMessage();
            while ((line = reader.readLine()) != null) {
                processResponseData(line, msBody);
            }
        } catch (Exception e) {
            this.onError(e);
        }
    }

    private void processResponseData(String line, WRCMessage msBody) {
        if (line.startsWith(EVENT_LABEL)) {
            msBody.setLabel(line.substring(EVENT_LABEL.length()));
        } else if (line.startsWith(ID_LABEL)) {
            msBody.setId(line.substring(ID_LABEL.length()));
        } else if (line.startsWith(DATA_LABEL)) {
            msBody.setData(line.substring(DATA_LABEL.length()));
        }
        if (msBody.isReady()) {
            this.onMessage(msBody);
            msBody.clear();
        }
    }

    private void onMessage(StreamMessage msBody) {
        log.debug("Msg Id: {}", msBody.getId());
        log.debug("Msg Label: {}", msBody.getLabel());
        kafkaProducer.send(
                new ProducerRecord<>(topic, msBody.getData()),
                (recordMetadata, e) -> {
                    if (e != null) {
                        this.onMessageError(e);
                    }
                    log.info("Message sent to Kafka topic: {}", topic);
                }
        );
    }

    private void onError(Exception e) {
        log.error(e.getMessage(), e);
        this.kafkaProducer.close();
        throw new RuntimeException(e);
    }

    private void onMessageError(Exception e) {
        log.error(e.getMessage(), e);
    }

    public void close() {
        kafkaProducer.close();
    }
}
