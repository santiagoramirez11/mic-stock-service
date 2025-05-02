package com.banreservas.micstockservice.controller;

import com.banreservas.micstockservice.exception.ProductNotFoundException;
import com.banreservas.micstockservice.model.StockMovement;
import com.banreservas.micstockservice.service.StockMovementService;
import com.banreservas.micstockservice.service.StockService;
import com.banreservas.openapi.controllers.StockApi;
import com.banreservas.openapi.models.AdjustStockRequestDto;
import com.banreservas.openapi.models.MovementHistoryItemDto;
import com.banreservas.openapi.models.StockResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.banreservas.micstockservice.mapper.StockDtoMapper.STOCK_DTO_MAPPER;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("${app.api.base-url}")
public class StockController implements StockApi {

    private final StockService stockService;

    private final StockMovementService stockMovementService;

    @Override
    public Mono<ResponseEntity<StockResponseDto>> getStock(String productId, ServerWebExchange exchange) {

        return stockService.getByProductId(productId)
                .switchIfEmpty(Mono.error(new ProductNotFoundException(productId)))
                .doOnSuccess(response -> log.debug("Success getting Stock: [{},{}]", productId, response))
                .map(STOCK_DTO_MAPPER::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<StockResponseDto>> stockAdjust(AdjustStockRequestDto adjustStockRequestDto, ServerWebExchange exchange) {
        StockMovement stockMovement = STOCK_DTO_MAPPER.toStockMovement(adjustStockRequestDto);
        return stockService.getByProductId(adjustStockRequestDto.getProductId())
                .switchIfEmpty(Mono.error(new ProductNotFoundException(adjustStockRequestDto.getProductId())))
                .doOnSuccess(stock -> log.trace("Adjusting Stock: [{}]", stock))
                .flatMap(stock -> stockService.adjustStock(stock, stockMovement))
                .map(STOCK_DTO_MAPPER::toDto)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<MovementHistoryItemDto>>> stockHistory(String productId, ServerWebExchange exchange) {
        Flux<MovementHistoryItemDto> result = stockMovementService.getAllStockMovementsByProductId(productId)
                .map(STOCK_DTO_MAPPER::toMovementHistoryItemDto);
        return Mono.just(ResponseEntity.ok(result));
    }
}
