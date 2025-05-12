package com.banreservas.micstockservice.config.properties;

import java.util.Set;

public record Rule(String path, String method, Set<String> roles) {
}
