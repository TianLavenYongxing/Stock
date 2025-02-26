package com.stock.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stock.dao.StockInfoDao;
import com.stock.dto.StockData;
import jakarta.annotation.Resource;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MyWebSocketClient extends WebSocketClient {

    private static final String url = "ws://api.vvtr.com/v1/connect?apiKey=SX86nYgL8Ly28jhc57e463b4bc869cd";

    private StockInfoDao stockInfoDao;
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;

    public MyWebSocketClient(StockInfoDao stockInfoDao) {
        super(URI.create(url));
        this.stockInfoDao = stockInfoDao;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("WebSocket 连接已打开");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("收到消息: " + message);
        if (message.contains("latest_price")) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<StockData>>() {
            }.getType();
            List<StockData> stockInfoDTOList = gson.fromJson(message, type);
            for (StockData data : stockInfoDTOList) {
                Instant instant = Instant.ofEpochSecond(data.getUpdate_time());
                LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = dateTime.format(formatter);
                data.setTime(formattedDateTime);
                stockInfoDao.updateBySymbol(data);
            }
            send("ping");
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("WebSocket 连接已关闭: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket 错误: " + ex.getMessage());
    }


}
