package com.banreservas.micstockservice.repository;

import com.banreservas.micstockservice.model.StockMovement;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface StockMovementRepository extends ReactiveMongoRepository<StockMovement, String> {
    Flux<StockMovement> findByProductId(String productId);
}
