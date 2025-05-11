package com.banreservas.micstockservice.service.impl;

import com.banreservas.micstockservice.model.StockMovement;
import com.banreservas.micstockservice.repository.StockMovementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockMovementServiceImplTest {

    @Mock
    private StockMovementRepository repository;

    @InjectMocks
    private StockMovementServiceImpl stockMovementService;

    @Test
    void StockMovementService_whenCreateStockMovement_thenSuccess() {
        var stockMovement = StockMovement.builder()
                .id("1a342-2341ab")
                .productId("456-ab2345-235")
                .quantity(5)
                .build();

        when(repository.save(stockMovement)).thenReturn(Mono.just(stockMovement));

        StepVerifier.create(stockMovementService.createStockMovement(stockMovement))
                .expectNext(stockMovement)
                .verifyComplete();

        verify(repository, times(1)).save(stockMovement);
    }

    @Test
    void StockMovementService_whenGetAllStockMovementsByProductId_thenSuccess() {
        String productId = "456-ab2345-235";
        var stockMovement1 = StockMovement.builder().id("1a342-2341ab").productId(productId).quantity(5).build();
        var stockMovement2 = StockMovement.builder().id("245234-as").productId(productId).quantity(10).build();

        when(repository.findByProductId(productId)).thenReturn(Flux.just(stockMovement1, stockMovement2));

        StepVerifier.create(stockMovementService.getAllStockMovementsByProductId(productId))
                .expectNext(stockMovement1, stockMovement2)
                .verifyComplete();

        verify(repository, times(1)).findByProductId(productId);
    }
}