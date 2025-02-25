package com.stock.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockInfoRealTimeDTO {
    private String symbol;
    private BigDecimal latest_price;
    private String time;
}
