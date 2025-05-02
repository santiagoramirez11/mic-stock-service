package com.banreservas.micstockservice.service;

import com.banreservas.micstockservice.model.StockMovement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockMovementService {

    Mono<StockMovement> createStockMovement(StockMovement stockMovement);

    Flux<StockMovement> getAllStockMovementsByProductId(String productId);
}
