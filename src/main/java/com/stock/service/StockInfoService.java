package com.stock.service;

import com.stock.entity.StockConfigEntity;
import com.stock.mybatis.utils.R;

public interface StockInfoService {

    R<Object> stockSelectNow();

    R<Object> getStockPoolAll(Integer product,String order, Long targetId);

    R<Object> getConfig();

    R<Object> updateStockConfig(StockConfigEntity stockConfig);

    void saveStockDataAsync(String type, String strategy, int recordHighDay, int increaseDay, int increase);

}
