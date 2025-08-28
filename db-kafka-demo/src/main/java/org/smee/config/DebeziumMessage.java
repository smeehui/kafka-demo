package org.smee.config;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class DebeziumMessage<T> {
    public Schema schema;
    public Payload<T> payload;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Schema {
        // nếu cần parse schema
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload<T> {
        public T before;
        public T after;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class Source {
        public String version;
        public String connector;
        public String db;
        public String schema;
        public Object table;
    }

    // thêm các trường khác nếu muốn
}
