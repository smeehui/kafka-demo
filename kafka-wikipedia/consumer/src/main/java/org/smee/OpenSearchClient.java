package org.smee;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.indices.CreateIndexRequest;
import org.opensearch.client.indices.CreateIndexResponse;
import org.opensearch.client.indices.GetIndexRequest;

import java.io.IOException;
import java.net.URI;

@Slf4j
public class OpenSearchClient {
    public static RestHighLevelClient getClient() {
        var connectionString = "http://localhost:9200";
        var uri = URI.create(connectionString);

        String userInfo = uri.getUserInfo();
        var client = (RestHighLevelClient) null;
        if (userInfo == null) {
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme())));
        } else {
            String[] auth = userInfo.split(":");

            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(auth[0], auth[1]));
            client = new RestHighLevelClient(
                    RestClient.builder(new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()))
                            .setHttpClientConfigCallback(
                                    builder -> builder.setDefaultCredentialsProvider(credentialsProvider)
                            )
            );
        }
        return client;
    }

    public static void checkAndCreateIndex(RestHighLevelClient client) throws IOException {
        String wikimedia = "wikimedia";
        boolean existed = client.indices().exists(new GetIndexRequest(wikimedia), RequestOptions.DEFAULT);
        if (existed) {
            log.info("Index exists, skipping index creation");
            return;
        }
        CreateIndexRequest request = new CreateIndexRequest(wikimedia);
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);
        if (response.isAcknowledged()) {
            log.info("Index created {}", response.index());
        }
    }
}
