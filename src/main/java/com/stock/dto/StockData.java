package com.stock.dto;

import lombok.Data;

@Data
public class StockData {
    private String symbol; // 股票代码
    private double latest_price; // 最新价格
    private long update_time; // 更新时间
    private String time; // 时间
}
