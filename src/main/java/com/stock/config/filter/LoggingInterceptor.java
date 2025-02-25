package com.stock.config.filter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.stock.config.StockException;
import com.stock.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        // 在发送请求之前记录信息
        logger.debug("Sending request to URL: {}", request.getURI());
        logger.debug("HTTP Method: {}", request.getMethod());
        logger.debug("Request Headers: {}", request.getHeaders());

        // 调用执行器，获取响应
        ClientHttpResponse response = execution.execute(request, body);

        // 在接收到响应之后记录信息
        logger.debug("Response Status Code: {}", response.getStatusCode());
        logger.debug("Response Headers: {}", response.getHeaders());
        // 缓存响应体
        byte[] responseBody = StreamUtils.copyToByteArray(response.getBody());
        String responseBodyAsString = new String(responseBody, StandardCharsets.UTF_8);
        if(!responseBodyAsString.contains("{\"code\":200")){
            throw new StockException(responseBodyAsString);
        }
        logger.debug("Response Body: {}", responseBodyAsString);
        // 构造一个新的 ClientHttpResponse，使用缓存的响应体
        return new BufferingClientHttpResponse(response, responseBody);
    }

    private static class BufferingClientHttpResponse implements ClientHttpResponse {

        private final ClientHttpResponse originalResponse;
        private final byte[] responseBody;

        public BufferingClientHttpResponse(ClientHttpResponse originalResponse, byte[] responseBody) {
            this.originalResponse = originalResponse;
            this.responseBody = responseBody;
        }

        @Override
        public String getStatusText() throws IOException {
            return originalResponse.getStatusText();
        }

        @Override
        public InputStream getBody() {
            // 返回缓存的响应体流
            return new ByteArrayInputStream(responseBody);
        }

        @Override
        public HttpHeaders getHeaders() {
            return originalResponse.getHeaders();
        }

        @Override
        public void close() {
            originalResponse.close();
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return originalResponse.getStatusCode();
        }
    }
}
