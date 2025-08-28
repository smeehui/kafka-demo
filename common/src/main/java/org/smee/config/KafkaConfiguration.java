package org.smee.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class KafkaConfiguration extends AbstractSimpleConfiguration {
    public KafkaConfiguration() {
        this.loadConfigProperties("kafka");
    }
}
