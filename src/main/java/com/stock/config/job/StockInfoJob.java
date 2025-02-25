package com.stock.config.job;

import com.stock.dao.StockConfigDao;
import com.stock.entity.StockConfigEntity;
import com.stock.service.StockInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockInfoJob {

    private final StockInfoService stockInfoService;
    private final StockConfigDao stockConfigDao;

    @Scheduled(cron = "0 0 17 * * ?")
    public void stockInfo() {
        StockConfigEntity stockConfigEntity = stockConfigDao.selectById(1);
        stockInfoService.saveStockDataAsync(stockConfigEntity.getType(), stockConfigEntity.getStrategy(), stockConfigEntity.getRecordHighDay(), stockConfigEntity.getIncreaseDay(), stockConfigEntity.getIncrease());
    }

}


