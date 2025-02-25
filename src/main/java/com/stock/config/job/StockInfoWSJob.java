package com.stock.config.job;

import com.stock.config.MyWebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StockInfoWSJob {

    private final MyWebSocketClient webSocketClient;
    private volatile boolean isRunning = false;

    @Autowired
    public StockInfoWSJob(MyWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @Scheduled(cron = "0 25 09 * * ?")
    public void startTask() throws InterruptedException {
        if (!isRunning) {
            isRunning = true;
            System.out.println("任务启动...");
            webSocketClient.connect();
            while (isRunning) {
                Thread.sleep(30000);
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
}