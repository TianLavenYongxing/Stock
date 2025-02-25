package com.stock.dto;

import com.stock.mybatis.dto.BashDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 股票信息表
 *
 * @author Laven tianlavenyongxing@gmail.com
 * @since 2025-02-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "股票信息表")
public class StockInfoDTO extends BashDTO {
    @ApiModelProperty(value = "股票代码")
    private String symbol;

    @ApiModelProperty(value = "股票名称")
    private String name;

    @ApiModelProperty(value = "交易所")
    private String exchange;

    @ApiModelProperty(value = "种类 ")
    private Integer product;

    @ApiModelProperty(value = "是否主力合约")
    private Integer isMain;

    @ApiModelProperty(value = "策略")
    private String strategy;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "乐观锁字段")
    private Integer version;

    @ApiModelProperty(value = "拓展字段")
    private String extend;

}