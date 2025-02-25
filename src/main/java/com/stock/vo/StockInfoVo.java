package com.stock.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockInfoVo {

    private Long id;
    /**
     * 股票代码
     */
    private String symbol;
    /**
     * 股票名称
     */
    private String name;
    /**
     * 交易所
     */
    private String exchange;
    /**
     * 种类
     */
    private Integer product;
    /**
     * 是否主力合约
     */
    private Integer isMain;
    /**
     * 策略
     */
    private String strategy;
    /**
     * 历史最高
     */
    private BigDecimal historyMax;
    /**
     * 收盘价
     */
    private BigDecimal close;
    /**
     * 最新价格
     */
    private BigDecimal latestPrice	;
    /**
     * 最新更新时间
     */
    private String time;
    /**
     * 历史最高天数
     */
    private Integer recordHighDay;
    /**
     *
     */
    private Double ma5;
    /**
     *
     */
    private Double ma10;
    /**
     *
     */
    private Double ma20;
    private Date updateAt;
    private Date createAt;
    /**
     * 出现次数
     */
    private Integer consecutiveEnrollment;

}
