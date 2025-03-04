package com.stock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
public class StockApplication {
    public static void main(String[] args) {
        log.info("AK --- {},{}",System.getProperty("AK"),System.getenv("AK"));
        log.info("FLAG --- {},{}",System.getProperty("FLAG"),System.getenv("FLAG"));
        SpringApplication.run(StockApplication.class, args);
    }
}
