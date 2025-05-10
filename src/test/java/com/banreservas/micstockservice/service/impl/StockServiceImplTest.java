package com.banreservas.micstockservice.service.impl;

import com.banreservas.micstockservice.model.MovementType;
import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.model.StockMovement;
import com.banreservas.micstockservice.repository.StockRepository;
import com.banreservas.micstockservice.service.StockMovementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockMovementService stockMovementService;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void StockService_whenGetByProductId_thenSuccess() {
        String productId = "123-325";
        var stock = Stock.builder()
                .id("asdf-ase234-435353")
                .productId(productId)
                .quantity(10)
                .build();

        when(stockRepository.findByProductId(productId)).thenReturn(Mono.just(stock));

        StepVerifier.create(stockService.getByProductId(productId))
                .expectNext(stock)
                .verifyComplete();

        verify(stockRepository, times(1)).findByProductId(productId);
    }

    @Test
    void StockService_whenCreateStock_thenSuccess() {
        var stock = Stock.builder()
                .productId("asdf-ase234-435353")
                .quantity(10)
                .build();

        when(stockRepository.save(stock)).thenReturn(Mono.just(stock));

        StepVerifier.create(stockService.createStock(stock))
                .expectNext(stock)
                .verifyComplete();
        verify(stockRepository, times(1)).save(stock);
    }

    @Test
    void StockService_whenDeleteStock_thenSuccess() {
        var stock = Stock.builder()
                .id("asdf-ase234-435353")
                .build();

        when(stockRepository.delete(stock)).thenReturn(Mono.empty());

        StepVerifier.create(stockService.deleteStock(stock))
                .verifyComplete();
        verify(stockRepository, times(1)).delete(stock);
    }

    @Test
    void StockService_whenAdjustStock_thenSuccess() {
        var stock = Stock.builder()
                .id("asdf-ase234-435353")
                .productId("123-325")
                .quantity(10)
                .build();

        StockMovement stockMovement = StockMovement.builder()
                .id("435235")
                .productId("asdf-324-1234")
                .quantity(12)
                .type(MovementType.IN)
                .build();

        when(stockMovementService.createStockMovement(stockMovement)).thenReturn(Mono.just(stockMovement));
        when(stockRepository.save(any(Stock.class))).thenReturn(Mono.just(stock));

        StepVerifier.create(stockService.adjustStock(stock, stockMovement))
                .expectNextMatches(adjustedStock -> adjustedStock.getQuantity() == 22)
                .verifyComplete();

        verify(stockMovementService, times(1)).createStockMovement(stockMovement);
        verify(stockRepository, times(1)).save(any(Stock.class));
    }
}