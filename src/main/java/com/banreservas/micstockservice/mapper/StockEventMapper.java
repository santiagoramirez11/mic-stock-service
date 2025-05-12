package com.banreservas.micstockservice.mapper;
import com.banreservas.micstockservice.avro.v1.StockAdjustedEventV1;
import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.model.StockMovement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StockEventMapper {

    StockEventMapper STOCK_EVENT_MAPPER = Mappers.getMapper(StockEventMapper.class);

    @Mapping(source = "stock.productId", target = "productId")
    @Mapping(source = "stock.quantity", target = "finalQuantity")
    @Mapping(source = "stockMovement.quantity", target = "adjustedQuantity")
    StockAdjustedEventV1 toAdjustedEvent(Stock stock, StockMovement stockMovement);

    @Mapping(source = "finalQuantity", target = "quantity")
    @Mapping(target = "id", ignore = true)
    Stock toDomain(StockAdjustedEventV1 stockAdjustedEventV1);
}
