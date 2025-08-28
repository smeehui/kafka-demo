package org.smee.config;

import io.debezium.config.Configuration;
import lombok.Getter;

@Getter
public class DebeziumConfig extends AbstractSimpleConfiguration{
   private final Configuration config;

   public DebeziumConfig() {
       this.loadConfigProperties("debezium");
       config = Configuration.from(properties);
   }
}
