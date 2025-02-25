package com.stock.dao;

import com.stock.entity.StockConfigEntity;
import com.stock.mybatis.dao.BaseDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 *
 * @author Laven tianlavenyongxing@gmail.com
 * @since  2025-02-23
 */
@Mapper
public interface StockConfigDao extends BaseDao<StockConfigEntity> {

    /**
    * 物理删除记录
    * @param ids 要删除的 ID 列表
    * @return 删除的记录数
    */
    int deleteBatchIds(@Param("list") List<String> ids);

}