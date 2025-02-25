package com.stock.dto;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponse<T> {
    private int code;
    private String msg;
    private List<T> data;
}
