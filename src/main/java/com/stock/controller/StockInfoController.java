package com.stock.controller;

import com.stock.config.job.StockInfoWSJob;
import com.stock.entity.StockConfigEntity;
import com.stock.mybatis.utils.R;
import com.stock.service.StockInfoService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
@Api(tags = "竞赛内容")
@RequiredArgsConstructor
public class StockInfoController {

    private final StockInfoService stockInfoService;
    private final StockInfoWSJob stockInfoWSJob;

    @PostMapping("/stockSelectNow")
    public R<Object> stockSelectNow() {
        return stockInfoService.stockSelectNow();
    }

    @PostMapping("/getStockPoolAll")
    public R<Object> getStockPoolAll(@RequestParam(value = "product", required = false) Integer product, @RequestParam(value = "order", required = false) String order, @RequestParam(value = "targetId", required = false) Long targetId) {
        return stockInfoService.getStockPoolAll(product, order, targetId);
    }

    @PostMapping("/getConfig")
    public R<Object> getConfig() {
        return stockInfoService.getConfig();
    }

    @PostMapping("/updateStockConfig")
    public R<Object> updateStockConfig(@RequestBody StockConfigEntity stockConfig) {
        return stockInfoService.updateStockConfig(stockConfig);
    }

    @PostMapping("/startTask")
    public R<Object> startTask() {
        stockInfoWSJob.startTask();
        return R.ok();
    }

    @PostMapping("/startTask1")
    public R<Object> startTask1() {
        stockInfoWSJob.startTask1();
        return R.ok();
    }

    @PostMapping("/stopTask")
    public R<Object> stopTask() {
        stockInfoWSJob.stopTask();
        return R.ok();
    }
}
