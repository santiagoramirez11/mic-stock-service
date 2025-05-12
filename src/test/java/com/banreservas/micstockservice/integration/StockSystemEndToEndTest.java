package com.banreservas.micstockservice.integration;

import com.banreservas.micstockservice.MicStockServiceApplicationTests;
import com.banreservas.micstockservice.mapper.StockDtoMapper;
import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.repository.StockRepository;
import com.banreservas.openapi.models.AdjustStockRequestDto;
import com.banreservas.openapi.models.AuthenticationRequestDto;
import com.banreservas.openapi.models.AuthenticationResponseDto;
import com.banreservas.openapi.models.StockResponseDto;
import jakarta.annotation.PostConstruct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Objects;

@Testcontainers(disabledWithoutDocker = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StockSystemEndToEndTest extends MicStockServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private StockRepository stockRepository;

    private String tokenHeader;

    @PostConstruct
    public void postConstruct(){
        AuthenticationRequestDto authRequest = new AuthenticationRequestDto();
        authRequest.setUsername("admin");
        authRequest.setPassword("admin123");

        String accessToken = Objects.requireNonNull(client.post().uri("/api/v1/auth/login")
                        .bodyValue(authRequest)
                        .exchange()
                        .expectStatus().isOk()
                        .expectBody(AuthenticationResponseDto.class)
                        .returnResult().getResponseBody())
                .getAccessToken();
        tokenHeader = "Bearer " + accessToken;
    }


    @Test
    @Order(1)
    void StockService_whenGetStock_thenAllCycleSuccess() {

        final var productId = "123-abc";
        final var stock = Stock.builder()
                .productId(productId)
                .quantity(20)
                .build();
        stockRepository.save(stock)
                .block();

        final var expected = StockDtoMapper.STOCK_DTO_MAPPER.toDto(stock);

        StockResponseDto stockResponseDto = client.get()
                .uri("/api/v1/stock/{productId}", productId)
                .header("Authorization", tokenHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(StockResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(stockResponseDto).usingDefaultComparator().isEqualTo(expected);
    }

    @Test
    @Order(2)
    void StockService_whenStockAdjust_thenAllAdjustCycleSuccess() {
        AdjustStockRequestDto adjustStockRequestDto = new AdjustStockRequestDto();
        adjustStockRequestDto.setProductId("123-abc");
        adjustStockRequestDto.setQuantity(5);
        adjustStockRequestDto.setMovementType(AdjustStockRequestDto.MovementTypeEnum.IN);

        StockResponseDto expected = new StockResponseDto();
        expected.setProductId("123-abc");
        expected.setStock(25);


        final var stockResponseDto = client.post()
                .uri("/api/v1/stock/adjust")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .bodyValue(adjustStockRequestDto)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(StockResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(stockResponseDto).usingDefaultComparator().isEqualTo(expected);

    }
}