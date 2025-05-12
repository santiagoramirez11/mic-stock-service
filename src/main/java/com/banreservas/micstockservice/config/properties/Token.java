package com.banreservas.micstockservice.config.properties;

public record Token(String secret, long expirationTime) {
}