package com.stock.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author Laven tianlavenyongxing@gmail.com
 * @since 2025-02-23
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("stock_config")
public class StockConfigEntity {

    @TableId
    private Long id;
    private String type;

    private Integer recordHighDay;

    private Integer increaseDay;

    private Integer increase;

    private String strategy;

}