package com.banreservas.micstockservice.controller;

import com.banreservas.micstockservice.exception.ProductNotFoundException;
import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.model.StockMovement;
import com.banreservas.micstockservice.service.StockMovementService;
import com.banreservas.micstockservice.service.StockService;
import com.banreservas.openapi.models.AdjustStockRequestDto;
import com.banreservas.openapi.models.MovementHistoryItemDto;
import com.banreservas.openapi.models.StockResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Objects;

import static com.banreservas.micstockservice.mapper.StockDtoMapper.STOCK_DTO_MAPPER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockControllerTest {

    @Mock
    private StockService stockService;

    @Mock
    private StockMovementService stockMovementService;

    @InjectMocks
    private StockController stockController;

    @Mock
    private ServerWebExchange serverWebExchange;

    @Test
    void StockController_whenGetStock_thenSuccess() {
        String productId = "123-asf23-23";
        Stock stock = Stock.builder().productId(productId).quantity(10).build();
        StockResponseDto stockResponseDto = STOCK_DTO_MAPPER.toDto(stock);

        when(stockService.getByProductId(productId)).thenReturn(Mono.just(stock));

        StepVerifier.create(stockController.getStock(productId, serverWebExchange))
                .expectNext(ResponseEntity.ok(stockResponseDto))
                .verifyComplete();

        verify(stockService, times(1)).getByProductId(productId);
    }

    @Test
    void StockController_whenGetStock_thenProductNotFound() {
        String productId = "123-asf23-23";

        when(stockService.getByProductId(productId)).thenReturn(Mono.empty());

        Mono<ResponseEntity<StockResponseDto>> result = stockController.getStock(productId, serverWebExchange);

        StepVerifier.create(result)
                .expectError(ProductNotFoundException.class)
                .verify();

        verify(stockService, times(1)).getByProductId(productId);
    }

    @Test
    void StockController_whenStockAdjust_thenSuccess() {
        var productId = "123-asf23-23";
        AdjustStockRequestDto adjustStockRequestDto = new AdjustStockRequestDto();
        adjustStockRequestDto.setProductId(productId);
        adjustStockRequestDto.setQuantity(10);
        adjustStockRequestDto.setMovementType(AdjustStockRequestDto.MovementTypeEnum.IN);

        Stock stock = Stock.builder()
                .id("123-asf23-23")
                .productId(productId)
                .quantity(5)
                .build();

        Stock adjustedStock = Stock.builder()
                .id("123-asf23-23")
                .productId(productId)
                .quantity(15)
                .build();
        StockResponseDto stockResponseDto = STOCK_DTO_MAPPER.toDto(adjustedStock);

        when(stockService.getByProductId(productId)).thenReturn(Mono.just(stock));
        BDDMockito.given(stockService.adjustStock(any(Stock.class), any(StockMovement.class))).willReturn(Mono.just(adjustedStock));

        StepVerifier.create(stockController.stockAdjust(adjustStockRequestDto, serverWebExchange))
                .expectNext(ResponseEntity.ok(stockResponseDto))
                .verifyComplete();

        verify(stockService, times(1)).getByProductId(productId);
    }

    @Test
    void StockController_whenStockHistory_thenSuccess() {
        String productId = "123-asf23-23";
        StockMovement stockMovement1 = StockMovement.builder()
                .productId(productId)
                .quantity(5)
                .build();
        StockMovement stockMovement2 = StockMovement.builder()
                .productId(productId)
                .quantity(10)
                .build();
        MovementHistoryItemDto dto1 = STOCK_DTO_MAPPER.toMovementHistoryItemDto(stockMovement1);
        MovementHistoryItemDto dto2 = STOCK_DTO_MAPPER.toMovementHistoryItemDto(stockMovement2);

        when(stockMovementService.getAllStockMovementsByProductId(productId)).thenReturn(Flux.just(stockMovement1, stockMovement2));

        StepVerifier.create(stockController.stockHistory(productId, serverWebExchange))
                .assertNext(response -> {
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                    StepVerifier.create(Objects.requireNonNull(response.getBody()))
                            .expectNext(dto1, dto2)
                            .verifyComplete();
                })
                .verifyComplete();

        verify(stockMovementService, times(1)).getAllStockMovementsByProductId(productId);
    }
}