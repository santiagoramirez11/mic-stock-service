package com.banreservas.micstockservice.integration;

import com.banreservas.micstockservice.MicStockServiceApplicationTests;
import com.banreservas.micstockservice.mapper.StockDtoMapper;
import com.banreservas.micstockservice.model.Stock;
import com.banreservas.micstockservice.repository.StockMovementRepository;
import com.banreservas.micstockservice.repository.StockRepository;
import com.banreservas.micstockservice.repository.UserRepository;
import com.banreservas.openapi.models.*;
import jakarta.annotation.PostConstruct;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static com.banreservas.micstockservice.mapper.StockDtoMapper.STOCK_DTO_MAPPER;

@Testcontainers(disabledWithoutDocker = true)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StockSystemEndToEndTest extends MicStockServiceApplicationTests {

    @Autowired
    private WebTestClient client;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private StockMovementRepository stockMovementRepository;

    @Autowired
    private UserRepository userRepository;

    private String tokenHeader;

    @PostConstruct
    public void postConstruct() {
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

    @Test
    @Order(3)
    void StockService_whenCallStockHistory_thenAllStockHistoryCycleSuccess() {

        final var productId = "123-abc";
        List<MovementHistoryItemDto> movementHistoryList = stockMovementRepository.findByProductId(productId)
                .map(STOCK_DTO_MAPPER::toMovementHistoryItemDto)
                .collectList()
                .block();


        client.get()
                .uri("/api/v1/stock/{productId}/history", productId)
                .header("Authorization", tokenHeader)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(MovementHistoryItemDto.class)
                .isEqualTo(Objects.requireNonNull(movementHistoryList));
    }

    @Order(4)
    @ParameterizedTest
    @ValueSource(strings = {"/api/v1/stock/{productId}"})
    void StockService_whenGetProduct_thenProductNotFound(String endpoint) {

        final var productId = "123-abcd-12";

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), "product-not-found", String.format("Product with id: '%s' not exist", productId));

        client.get()
                .uri(endpoint, productId)
                .header("Authorization", tokenHeader)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(errorResponseDto);
    }

    @Test
    @Order(5)
    void StockService_whenStockAdjust_thenProductNotFound() {
        AdjustStockRequestDto adjustStockRequestDto = new AdjustStockRequestDto();
        adjustStockRequestDto.setProductId("123-abc232");
        adjustStockRequestDto.setQuantity(5);
        adjustStockRequestDto.setMovementType(AdjustStockRequestDto.MovementTypeEnum.IN);

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(HttpStatus.NOT_FOUND.value(), "product-not-found", String.format("Product with id: '%s' not exist", "123-abc232"));

        client.post()
                .uri("/api/v1/stock/adjust")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .bodyValue(adjustStockRequestDto)
                .exchange()
                .expectStatus()
                .isNotFound()
                .expectBody(ErrorResponseDto.class)
                .isEqualTo(errorResponseDto);
    }

    @Test
    @Order(6)
    void AuthController_whenRegisterUser_thenSuccess() {
        var username = "user1";
        var roles = List.of("USER");
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setUsername(username);
        userRegistrationRequestDto.setPassword("password123");
        userRegistrationRequestDto.setRoles(roles);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setUsername(username);
        userResponseDto.setRoles(roles);

        client.post()
                .uri("/api/v1/auth/register")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", tokenHeader)
                .bodyValue(userRegistrationRequestDto)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(UserResponseDto.class)
                .isEqualTo(userResponseDto);

        StepVerifier.create(userRepository.findByUsername(username))
                .expectNextMatches(user -> user.getUsername().equals(username) && user.getRoles().contains("USER"))
                .verifyComplete();
    }
}