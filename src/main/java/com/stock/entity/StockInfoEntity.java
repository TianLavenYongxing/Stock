package com.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.stock.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 股票信息表
 *
 * @author Laven tianlavenyongxing@gmail.com
 * @since  2025-02-23
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("stock_info")
public class StockInfoEntity extends BaseEntity {

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
     * 出现次数
     */
    private Integer consecutiveEnrollment;
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


    private String historyMaxTime;

    /**
     * 最新价格
     */
    private BigDecimal latestPrice;
    /**
     * 最新更新时间
     */
    private String time;
}