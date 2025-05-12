package com.banreservas.micstockservice.config;

import com.banreservas.micstockservice.avro.v1.StockAdjustedEventV1;
import com.banreservas.micstockservice.config.properties.KafkaBindingsProperties;
import com.banreservas.micstockservice.messaging.StockEventPublisher;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConfigurationPropertiesScan
public class KafkaEventPublisherConfig {

    @Bean
    public StockEventPublisher<StockAdjustedEventV1> stockAdjustedEventPublisher(
            KafkaTemplate<String, StockAdjustedEventV1> kafkaTemplate, KafkaBindingsProperties kafkaBindingsProperties) {

        var topic = kafkaBindingsProperties.getTopics().get(StockAdjustedEventV1.class.getCanonicalName());
        return new StockEventPublisher<>(kafkaTemplate, topic);
    }
}
