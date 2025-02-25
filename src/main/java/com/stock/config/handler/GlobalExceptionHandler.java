package com.stock.config.handler;

import com.stock.config.StockException;
import com.stock.mybatis.utils.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    // 捕获自定义异常
    @ExceptionHandler(StockException.class)
    public ResponseEntity<Object> handleCustomException(StockException ex) {
        return new ResponseEntity<>(R.error("三方接口异常",ex.getObject()), HttpStatus.BAD_REQUEST);
    }
}
