package org.smee.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoDatabase extends AbstractSimpleConfiguration{
    public MongoDatabase() {
        this.loadConfigProperties("mongo");
    }

    public MongoClient getMongoClient() {
        return MongoClients.create(properties.getProperty("uri"));
    }
}
