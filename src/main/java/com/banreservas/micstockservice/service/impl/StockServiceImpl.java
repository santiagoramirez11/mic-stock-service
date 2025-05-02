package com.banreservas.micstockservice.service.impl;

import com.banreservas.micstockservice.model.MovementType;
import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.model.StockMovement;
import com.banreservas.micstockservice.repository.StockRepository;
import com.banreservas.micstockservice.service.StockMovementService;
import com.banreservas.micstockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    private final StockMovementService stockMovementService;

    @Override
    @Cacheable(value = "stock", key = "#productId", unless = "#result == null")
    public Mono<Stock> getByProductId(String productId) {
        return stockRepository.findByProductId(productId);
    }

    @Override
    public Mono<Stock> createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    public Mono<Void> deleteStock(Stock stock) {
        return stockRepository.delete(stock);
    }

    @Override
    public Mono<Stock> adjustStock(Stock stock, StockMovement stockMovement) {
        return stockMovementService.createStockMovement(stockMovement)
                .flatMap(adjustFunction(stock));
    }

    private Function<StockMovement, Mono<Stock>> adjustFunction(Stock stock) {
        return stockMovement -> {
            if (MovementType.IN.equals(stockMovement.getType())) {
                stock.setQuantity(stock.getQuantity() + stockMovement.getQuantity());
            } else {
                stock.setQuantity(stock.getQuantity() - stockMovement.getQuantity());
            }
            return stockRepository.save(stock);
        };
    }

}
