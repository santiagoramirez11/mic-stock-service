package com.banreservas.micstockservice.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Stock {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotNull(message = "Product_name can not be null")
    @NotEmpty(message = "Product_name can not be empty")
    private String productId;

    @Min(value = 0, message = "Quantity can not be less than 0")
    private int quantity;

}
