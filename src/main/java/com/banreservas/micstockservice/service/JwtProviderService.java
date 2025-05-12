package com.banreservas.micstockservice.service;

import com.banreservas.micstockservice.security.TokenInfo;
import org.springframework.security.core.Authentication;

public interface JwtProviderService {

    TokenInfo generateToken(Authentication authentication);

    Authentication getAuthentication(String token);

    boolean validateToken(String token);
}
