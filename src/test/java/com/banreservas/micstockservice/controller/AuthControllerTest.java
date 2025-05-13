package com.banreservas.micstockservice.controller;

import com.banreservas.micstockservice.model.User;
import com.banreservas.micstockservice.security.TokenInfo;
import com.banreservas.micstockservice.service.JwtProviderService;
import com.banreservas.micstockservice.service.UserService;
import com.banreservas.openapi.models.AuthenticationRequestDto;
import com.banreservas.openapi.models.UserRegistrationRequestDto;
import com.banreservas.openapi.models.UserResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@WebFluxTest(controllers = AuthController.class,
        excludeAutoConfiguration = {
                ReactiveUserDetailsServiceAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class
        })
class AuthControllerTest {

    @MockitoBean
    private JwtProviderService tokenProvider;

    @MockitoBean
    private ReactiveAuthenticationManager authenticationManager;

    @MockitoBean
    private UserService userService;

    @Autowired
    private WebTestClient client;

    @Test
    void AuthController_whenLogin_thenSuccess() {

        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                "user",
                "pass",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN")
        );

        TokenInfo tokenInfo = new TokenInfo("BearerToken", 1000000L, "Bearer");

        when(this.authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(Mono.just(usernamePasswordAuthenticationToken));
        when(this.tokenProvider.generateToken(any(Authentication.class))).thenReturn(tokenInfo);

        AuthenticationRequestDto req = new AuthenticationRequestDto();
        req.setUsername("user");
        req.setPassword("pass");

        this.client.post()
                .uri("/api/v1/auth/login")
                .bodyValue(req)
                .exchange()
                .expectBody(TokenInfo.class)
                .isEqualTo(tokenInfo);

        verify(this.authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(this.tokenProvider, times(1)).generateToken(any(Authentication.class));
        verifyNoMoreInteractions(this.authenticationManager);
        verifyNoMoreInteractions(this.tokenProvider);
    }

    @Test
    void AuthController_whenRegister_thenSuccess() {
        UserRegistrationRequestDto userRegistrationRequestDto = new UserRegistrationRequestDto();
        userRegistrationRequestDto.setUsername("newuser");
        userRegistrationRequestDto.setPassword("password123");
        userRegistrationRequestDto.setRoles(List.of("USER"));

        var user = new User();
        user.setId("123");
        user.setUsername("newuser");
        user.setRoles(Set.of("USER"));

        var userResponseDto = new UserResponseDto();
        userResponseDto.setUsername("newuser");
        userResponseDto.setRoles(List.of("USER"));

        when(userService.createUser(any(User.class))).thenReturn(Mono.just(user));

        client.post()
                .uri("/api/v1/auth/register")
                .bodyValue(userRegistrationRequestDto)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(UserResponseDto.class)
                .isEqualTo(userResponseDto);

        verify(userService, times(1)).createUser(any(User.class));
        verifyNoMoreInteractions(userService);
    }

}