package com.stock.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
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

    private Boolean conditionNumberOne;

    private Boolean conditionNumberTwo;

    private Boolean conditionNumberThree;

    private Boolean conditionNumberFour;

    private Integer newHigh;

    @Version
    private Integer version;

}