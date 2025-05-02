package com.banreservas.micstockservice.service.impl;

import com.banreservas.micstockservice.model.StockMovement;
import com.banreservas.micstockservice.repository.StockMovementRepository;
import com.banreservas.micstockservice.service.StockMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StockMovementServiceImpl implements StockMovementService {

    private final StockMovementRepository repository;

    @Override
    public Mono<StockMovement> createStockMovement(StockMovement stockMovement) {
        return repository.save(stockMovement);
    }

    @Override
    public Flux<StockMovement> getAllStockMovementsByProductId(String productId) {
        return repository.findByProductId(productId);
    }
}
