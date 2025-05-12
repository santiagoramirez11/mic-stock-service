package com.banreservas.micstockservice.security;

public record TokenInfo(String accessToken, long expiresIn, String tokenType) {
}
