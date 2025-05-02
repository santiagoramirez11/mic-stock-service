package com.banreservas.micstockservice.repository;

import com.banreservas.micstockservice.model.Stock;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface StockRepository extends ReactiveMongoRepository<Stock, String> {
    Mono<Stock> findByProductId(String productId);
}
