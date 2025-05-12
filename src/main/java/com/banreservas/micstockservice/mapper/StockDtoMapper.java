package com.banreservas.micstockservice.mapper;

import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.model.StockMovement;
import com.banreservas.openapi.models.AdjustStockRequestDto;
import com.banreservas.openapi.models.MovementHistoryItemDto;
import com.banreservas.openapi.models.StockResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StockDtoMapper {

    StockDtoMapper STOCK_DTO_MAPPER = Mappers.getMapper(StockDtoMapper.class);

    @Mapping(target = "stock", source = "quantity")
    StockResponseDto toDto(Stock stock);

    @Mapping(target = "type", source = "movementType")
    @Mapping(target = "timestamp", expression = "java(java.time.Instant.now())")
    @Mapping(target = "id", ignore = true)
    StockMovement toStockMovement(AdjustStockRequestDto adjustStockRequestDto);

    MovementHistoryItemDto toMovementHistoryItemDto(StockMovement stockMovement);

}
