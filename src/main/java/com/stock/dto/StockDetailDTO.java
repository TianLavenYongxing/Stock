package com.stock.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockDetailDTO {
    private String symbol;
    private String exchange;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private Long volume;
    private Long turnover;
    private String time;
}
