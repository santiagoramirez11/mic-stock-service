package com.banreservas.micstockservice.config;

import com.banreservas.micstockservice.config.properties.SecurityProperties;
import com.banreservas.micstockservice.repository.UserRepository;
import com.banreservas.micstockservice.security.JwtAuthenticationFilter;
import com.banreservas.micstockservice.service.JwtProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;

import java.util.Set;

@Configuration
@EnableWebFluxSecurity
@ConfigurationPropertiesScan
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityProperties properties;

    @Value("${app.api.base-url}")
    private String baseUrl;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http,
                                                       JwtProviderService tokenProvider,
                                                       ReactiveAuthenticationManager reactiveAuthenticationManager) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(reactiveAuthenticationManager)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .addFilterAt(new JwtAuthenticationFilter(tokenProvider), SecurityWebFiltersOrder.HTTP_BASIC)
                .authorizeExchange(authorizeExchange -> {
                    properties.getRules().forEach(rule -> {
                        String path = rule.path();
                        String method = rule.method();
                        Set<String> roles = rule.roles();

                        ServerWebExchangeMatcher matcher = exchange -> {
                            boolean matchesPath = path.equals(exchange.getRequest().getPath().value());
                            boolean matchesMethod = method.equals(exchange.getRequest().getMethod().name());
                            return (matchesPath && matchesMethod)
                                    ? ServerWebExchangeMatcher.MatchResult.match()
                                    : ServerWebExchangeMatcher.MatchResult.notMatch();
                        };

                        authorizeExchange
                                .matchers(matcher)
                                .hasAnyRole(roles.toArray(new String[0]));
                    });
                    authorizeExchange.pathMatchers(baseUrl + "/auth/login").permitAll()
                            .pathMatchers("/actuator/health").permitAll()
                            .anyExchange().authenticated();
                })
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                       PasswordEncoder passwordEncoder) {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder);
        return authenticationManager;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(UserRepository users) {

        return username -> users.findByUsername(username)
                .map(u -> User
                        .withUsername(u.getUsername()).password(u.getPassword())
                        .roles(u.getRoles().toArray(new String[0]))
                        .build()
                );
    }

}
