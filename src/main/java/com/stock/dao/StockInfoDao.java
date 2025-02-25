package com.stock.dao;

import com.stock.dto.StockData;
import com.stock.entity.StockInfoEntity;
import com.stock.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 股票信息表
 *
 * @author Laven tianlavenyongxing@gmail.com
 * @since  2025-02-23
 */
@Mapper
public interface StockInfoDao extends BaseDao<StockInfoEntity> {

    /**
    * 逻辑删除记录
    * @param ids 要删除的 ID 列表
    * @return 删除的记录数
    */
    int deleteBatchIds(@Param("list") List<Long> ids);


    void updateBySymbol(StockData data);

}