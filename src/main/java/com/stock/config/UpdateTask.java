package com.stock.config;

import com.stock.dao.StockInfoDao;
import com.stock.dto.StockData;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
public class UpdateTask implements Runnable {
    private final StockData data;
    private final StockInfoDao stockInfoDao;

    public UpdateTask(StockData data, StockInfoDao stockInfoDao) {
        this.data = data;
        this.stockInfoDao = stockInfoDao;
    }

    @Override
    public void run() {
        try {
            Instant instant = Instant.ofEpochSecond(data.getUpdate_time());
            LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = dateTime.format(formatter);
            data.setTime(formattedDateTime);
            stockInfoDao.updateBySymbol(data);
        } catch (Exception e) {
            UpdateTask.log.error("更新数据失败: ", e.getMessage());
        }
    }
}
