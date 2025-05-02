package com.banreservas.micstockservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MicStockServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicStockServiceApplication.class, args);
    }

}
