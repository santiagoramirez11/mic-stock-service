package com.banreservas.micstockservice.security;

import com.banreservas.micstockservice.service.JwtProviderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SuppressWarnings("ReactiveStreamsUnusedPublisher")
class JwtAuthenticationFilterTest {

    private final JwtProviderService tokenProvider = Mockito.mock(JwtProviderService.class);

    private final ServerWebExchange exchange = mock(ServerWebExchange.class, RETURNS_DEEP_STUBS);

    private final WebFilterChain chain = mock(WebFilterChain.class, RETURNS_DEEP_STUBS);

    @BeforeEach
    void setup() {
        reset(this.tokenProvider);
        reset(this.exchange);
        reset(this.chain);
    }

    @Test
    void JwtAuthenticationFilter_whenFilter_thenSuccess() {
        var filter = new JwtAuthenticationFilter(this.tokenProvider);

        var usernamePasswordToken = new UsernamePasswordAuthenticationToken("test", "password",
                AuthorityUtils.createAuthorityList("ROLE_ADMIN"));

        given(this.exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .willReturn("Bearer tokentokentest");
        given(this.tokenProvider.validateToken(anyString())).willReturn(true);
        given(this.tokenProvider.getAuthentication(anyString())).willReturn(usernamePasswordToken);
        given(this.chain.filter(this.exchange)
                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(usernamePasswordToken))
        ).willReturn(Mono.empty());

        filter.filter(this.exchange, this.chain);

        then(this.chain)
                .should(times(2))
                .filter(this.exchange);
    }

    @Test
    void JwtAuthenticationFilter_whenFilterWithNoToken_thenSuccess() {
        var filter = new JwtAuthenticationFilter(this.tokenProvider);
        given(this.exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).willReturn(null);
        given(this.chain.filter(this.exchange)).willReturn(Mono.empty());

        filter.filter(this.exchange, this.chain);

        then(this.chain)
                .should(times(1))
                .filter(this.exchange);
    }

    @Test
    void JwtAuthenticationFilter_whenFilterWithInvalidToken_thenSuccess() {
        var filter = new JwtAuthenticationFilter(this.tokenProvider);
        given(this.exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .willReturn("Bearer tokentokentest");
        given(this.tokenProvider.validateToken(anyString())).willReturn(false);
        given(this.chain.filter(this.exchange)).willReturn(Mono.empty());

        filter.filter(this.exchange, this.chain);

        then(this.chain)
                .should(times(1))
                .filter(this.exchange);
    }
}