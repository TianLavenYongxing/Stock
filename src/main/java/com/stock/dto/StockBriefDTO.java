package com.stock.dto;

import lombok.Data;

@Data
public class StockBriefDTO {
    private String symbol;
    private String exchange;
    private String name;
    private int product;
    private boolean isMain;
}

