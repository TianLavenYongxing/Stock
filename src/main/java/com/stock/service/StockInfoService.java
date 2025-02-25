package com.stock.service;

import com.stock.entity.StockConfigEntity;
import com.stock.mybatis.utils.R;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface StockInfoService {

    R<Object> stockSelectNow();

    R<Object> getStockPool(String order, Long targetId);

    R<Object> getStockPoolAll(String order, Long targetId);

    R<Object> getConfig();

    R<Object> updateStockConfig(StockConfigEntity stockConfig);

    void saveStockDataAsync(String type, String strategy, int recordHighDay, int increaseDay, int increase);

}
