package com.stock.config;

import lombok.Data;

@Data
public class StockException extends RuntimeException{
    private Object object;

    public StockException(Object object) {
        this.object = object;
    }
}
