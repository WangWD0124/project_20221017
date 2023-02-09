package com.wwd.modules.order.vo;

import lombok.Data;

/**
 * 锁定库存返回结果
 */
@Data
public class LockStockResult {

    private Long skuId;
    private Integer num;
    private Boolean lock;
}
