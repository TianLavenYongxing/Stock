package com.stock.config.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.stock.config.MyWebSocketClient;
import com.stock.dao.StockInfoDao;
import com.stock.entity.StockInfoEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class StockInfoWSJob {

    private static final String url = "http://api.vvtr.com/v1/subscribe";
    private MyWebSocketClient webSocketClient;
    private volatile boolean isRunning = false;
    @Autowired
    private StockInfoDao stockInfoDao;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public StockInfoWSJob(MyWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @Scheduled(cron = "0 25 09 * * ?")
    public void startTask() {
        if (!isRunning) {
            isRunning = true;
            System.out.println("任务启动...");
            webSocketClient = new MyWebSocketClient(stockInfoDao);
            webSocketClient.connect();
        }
    }

    @Scheduled(cron = "0 25 09 * * ?")
    public void startTask1() {
        if (isRunning) {
            List<StockInfoEntity> stockInfoEntities = stockInfoDao.selectList(new LambdaQueryWrapper<StockInfoEntity>().eq(StockInfoEntity::getProduct, 1));
            while (isRunning) {
                List<List<StockInfoEntity>> lists = splitList(stockInfoEntities);
                for (List<StockInfoEntity> list : lists) {
                    String symbols = list.stream().map(StockInfoEntity::getSymbol).collect(Collectors.joining(","));
                    getStockDetailDTOList(symbols, url);
                }
                webSocketClient.send("ping");
                log.info("--------------------已发送消息-------------------");
            }
        }
    }

    @Scheduled(cron = "0 05 15 * * ?")
    public void stopTask() {
        if (isRunning) {
            isRunning = false;
            System.out.println("任务关闭...");
            webSocketClient.close();
        }
    }

    private List<List<StockInfoEntity>> splitList(List<StockInfoEntity> stockBriefDTOS) {
        List<List<StockInfoEntity>> result = new ArrayList<>();
        if (stockBriefDTOS == null || stockBriefDTOS.isEmpty()) {
            return result;
        }
        int totalSize = stockBriefDTOS.size();
        for (int i = 0; i < totalSize; i += 30) {
            int end = Math.min(i + 30, totalSize);
            result.add(stockBriefDTOS.subList(i, end));
        }
        return result;
    }

    private void getStockDetailDTOList(String symbols, String url) {
        String apiKey = Objects.isNull(System.getenv("AK")) ? System.getProperty("AK") : System.getenv("AK");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        URI uri = UriComponentsBuilder.fromHttpUrl(url).queryParam("cn_stocks", symbols).queryParam("apiKey", apiKey).build().toUri();
        restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
    }
}