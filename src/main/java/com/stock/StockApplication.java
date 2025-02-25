package com.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StockApplication {
    public static void main(String[] args) {
        System.out.println(System.getProperty("AK"));
        System.out.println(System.getProperty("FLAG"));
        SpringApplication.run(StockApplication.class, args);
    }
}
