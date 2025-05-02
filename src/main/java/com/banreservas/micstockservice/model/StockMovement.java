package com.banreservas.micstockservice.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@Document(collection = "stock_history")
public class StockMovement {

    @Id
    private String id;

    private String productId;

    private int quantity;

    private MovementType type;

    private Instant timestamp;
}
