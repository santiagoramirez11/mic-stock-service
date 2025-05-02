package com.banreservas.micstockservice.listener;

import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.service.StockService;
import com.banreservas.product.avro.v1.ProductCreatedEventV1;
import com.banreservas.product.avro.v1.ProductDeletedEventV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final StockService stockService;

    @KafkaListener(topics = "${app.kafka-listener.product-create-event}")
    public void consumerCreate(ConsumerRecord<String, ProductCreatedEventV1> consumerRecord) {
        log.debug("ProductCreatedEventV1 receive [{}]", consumerRecord.value());
        var productId = consumerRecord.value().getId();
        stockService.getByProductId(productId)
                .switchIfEmpty(Mono.defer(() ->
                        stockService.createStock(Stock.builder()
                        .productId(productId)
                        .build())))
                .subscribe();
    }

    @KafkaListener(topics = "${app.kafka-listener.product-deleted-event}")
    public void consumerDelete(ConsumerRecord<String, ProductDeletedEventV1> consumerRecord) {
        log.debug("ProductDeletedEventV1 receive [{}]", consumerRecord.value());
        var productId = consumerRecord.value().getId();
        stockService.getByProductId(productId)
                .switchIfEmpty(Mono.fromRunnable(() -> log.error("Trying to eliminate Stock, but product not exist [{}]",
                        consumerRecord.value())))
                .flatMap(stockService::deleteStock)
                .subscribe();
    }
}
