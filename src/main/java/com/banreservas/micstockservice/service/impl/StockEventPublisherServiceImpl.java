package com.banreservas.micstockservice.service.impl;

import com.banreservas.micstockservice.avro.v1.StockAdjustedEventV1;
import com.banreservas.micstockservice.messaging.StockEventPublisher;
import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.model.StockMovement;
import com.banreservas.micstockservice.service.StockEventPublisherService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.banreservas.micstockservice.mapper.StockEventMapper.STOCK_EVENT_MAPPER;

@Service
@RequiredArgsConstructor
public class StockEventPublisherServiceImpl implements StockEventPublisherService {

    private final StockEventPublisher<StockAdjustedEventV1> stockAdjustedEventPublisher;

    @Override
    public Mono<Stock> publishAdjustStockEvent(Stock stock, StockMovement stockMovement) {
        StockAdjustedEventV1 adjustedEvent = STOCK_EVENT_MAPPER.toAdjustedEvent(stock, stockMovement);
        return stockAdjustedEventPublisher.send(stock.getProductId(), adjustedEvent)
                .map(SendResult::getProducerRecord)
                .map(producerRecord -> STOCK_EVENT_MAPPER.toDomain(producerRecord.value()));
    }
}
