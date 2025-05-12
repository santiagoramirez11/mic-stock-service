package com.banreservas.micstockservice.service;

import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.model.StockMovement;
import reactor.core.publisher.Mono;

public interface StockEventPublisherService {

    Mono<Stock> publishAdjustStockEvent(Stock stock, StockMovement stockMovement);
}
