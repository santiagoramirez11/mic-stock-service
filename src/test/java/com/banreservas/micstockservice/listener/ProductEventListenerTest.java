package com.banreservas.micstockservice.listener;

import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.service.StockService;
import com.banreservas.product.avro.v1.ProductCreatedEventV1;
import com.banreservas.product.avro.v1.ProductDeletedEventV1;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductEventListenerTest {

    @Mock
    private StockService stockService;

    @InjectMocks
    private ProductEventListener productEventListener;

    @Test
    void ProductEventListener_whenConsumerCreate_thenSuccess() {
        var productId = "1as234-232345-as";
        var productCreatedEvent = ProductCreatedEventV1.newBuilder()
                .setId(productId)
                .setName("Laptop")
                .setCategory("Tech")
                .setDescription("Laptop de 15 pulgadas")
                .setPrice(345.87d)
                .setSku("234234234")
                .build();
        ConsumerRecord<String, ProductCreatedEventV1> consumerRecord = new ConsumerRecord<>("topic", 0, 0, null, productCreatedEvent);

        when(stockService.getByProductId(productId)).thenReturn(Mono.empty());
        when(stockService.createStock(any(Stock.class))).thenReturn(Mono.just(Stock.builder().build()));

        productEventListener.consumerCreate(consumerRecord);

        verify(stockService, times(1)).getByProductId(productId);
        verify(stockService, times(1)).createStock(any(Stock.class));
    }

    @Test
    void ProductEventListener_whenConsumerDelete_thenSuccess() {
        var productId = "123";
        var productDeletedEvent = ProductDeletedEventV1.newBuilder()
                .setId(productId)
                .build();
        ConsumerRecord<String, ProductDeletedEventV1> consumerRecord = new ConsumerRecord<>("topic", 0, 0, null, productDeletedEvent);

        var stock = Stock.builder()
                .productId(productId)
                .build();
        when(stockService.getByProductId(productId)).thenReturn(Mono.just(stock));
        when(stockService.deleteStock(stock)).thenReturn(Mono.empty());

        productEventListener.consumerDelete(consumerRecord);

        verify(stockService, times(1)).getByProductId(productId);
        verify(stockService, times(1)).deleteStock(stock);
    }
}