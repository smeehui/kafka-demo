package org.smee.repo.impl;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.smee.config.MongoDatabase;
import org.smee.dto.Logs;
import org.smee.mapper.LogMapper;
import org.smee.repo.LogRepository;

@Slf4j
public class MongoLogRepo implements LogRepository {
    private final MongoDatabase database;
    private final LogMapper logMapper;

    public MongoLogRepo() {
        this.database = new MongoDatabase();
        this.logMapper = LogMapper.INSTANCE;
    }

    public MongoCollection<Document> getCollection(MongoClient client) {
        var database = client.getDatabase("smee");
        return database.getCollection("logs");
    }

    @Override
    public boolean create(Logs logEntity) {
        try (var client = database.getMongoClient()) {
            MongoCollection<Document> collection = this.getCollection(client);
            log.info("Writing log: {}", logEntity);
            collection.insertOne(logMapper.toMongoDocument(logEntity));
            log.info("Successfully wrote log: {}", logEntity);
            return true;
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
