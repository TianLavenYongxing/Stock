CREATE TABLE `stock_config` (
                                `id` bigint(20) NOT NULL,
                                `type` varchar(32) DEFAULT NULL,
                                `record_high_day` int(11) DEFAULT NULL,
                                `increase_day` int(11) DEFAULT NULL,
                                `increase` int(11) DEFAULT NULL,
                                `strategy` varchar(32) DEFAULT NULL,
                                `version` int(11) DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


CREATE TABLE `stock_info` (
                              `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
                              `symbol` varchar(64) DEFAULT NULL COMMENT '股票代码',
                              `name` varchar(64) DEFAULT NULL COMMENT '股票名称',
                              `exchange` varchar(64) DEFAULT NULL COMMENT '交易所',
                              `product` int(11) DEFAULT NULL COMMENT '种类 ',
                              `is_main` tinyint(1) DEFAULT NULL COMMENT '是否主力合约',
                              `strategy` varchar(64) DEFAULT NULL COMMENT '策略',
                              `create_at` datetime DEFAULT NULL COMMENT '创建时间',
                              `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
                              `update_at` datetime DEFAULT NULL COMMENT '修改时间',
                              `update_by` varchar(64) DEFAULT NULL COMMENT '修改人',
                              `delete_at` datetime DEFAULT NULL COMMENT '删除时间',
                              `delete_by` varchar(64) DEFAULT NULL COMMENT '删除人',
                              `del_flag` tinyint(1) DEFAULT '0' COMMENT '删除标志，0-未删除，1-已删除',
                              `remark` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                              `version` int(11) NOT NULL DEFAULT '0' COMMENT '乐观锁字段',
                              `extend` varchar(255) DEFAULT NULL COMMENT '拓展字段',
                              `history_max` decimal(10,4) DEFAULT NULL COMMENT '历史最高',
                              `consecutive_enrollment` int(11) DEFAULT NULL COMMENT '连续入选',
                              `ma5` float DEFAULT NULL,
                              `ma10` float DEFAULT NULL,
                              `ma20` float DEFAULT NULL,
                              `close` decimal(10,4) DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1893642878627017573 DEFAULT CHARSET=utf8mb4 COMMENT='股票信息表';