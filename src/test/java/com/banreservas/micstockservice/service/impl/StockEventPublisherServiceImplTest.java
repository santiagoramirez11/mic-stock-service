package com.banreservas.micstockservice.service.impl;

import com.banreservas.micstockservice.avro.v1.StockAdjustedEventV1;
import com.banreservas.micstockservice.messaging.StockEventPublisher;
import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.model.StockMovement;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.SendResult;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.banreservas.micstockservice.mapper.StockEventMapper.STOCK_EVENT_MAPPER;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockEventPublisherServiceImplTest {

    @Mock
    private StockEventPublisher<StockAdjustedEventV1> stockAdjustedEventPublisher;

    @InjectMocks
    private StockEventPublisherServiceImpl stockEventPublisherService;

    @Test
    void StockEventPublisherService_whenPublishAdjustStockEvent_thenSuccess() {
        var productId = "1231234-adf-234";
        var stock = Stock.builder().productId(productId).quantity(10).build();
        var stockMovement = StockMovement.builder().productId(productId).quantity(5).build();
        var adjustedEvent = STOCK_EVENT_MAPPER.toAdjustedEvent(stock, stockMovement);
        var producerRecord = new ProducerRecord<>("adjustTopic", productId, adjustedEvent);
        final var sendResult = new SendResult<>(producerRecord, null);

        when(stockAdjustedEventPublisher.send(productId, adjustedEvent)).thenReturn(Mono.just(sendResult));

        Mono<Stock> result = stockEventPublisherService.publishAdjustStockEvent(stock, stockMovement);

        StepVerifier.create(result)
                .expectNext(stock)
                .verifyComplete();

        verify(stockAdjustedEventPublisher, times(1)).send(productId, adjustedEvent);
    }
}