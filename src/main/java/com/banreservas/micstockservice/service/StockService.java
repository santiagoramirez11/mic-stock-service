package com.banreservas.micstockservice.service;

import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.model.StockMovement;
import reactor.core.publisher.Mono;

public interface StockService {

    Mono<Stock> getByProductId(String productId);

    Mono<Stock> createStock(Stock stock);

    Mono<Void> deleteStock(Stock stock);

    Mono<Stock> adjustStock(Stock stock, StockMovement stockMovement);

}
