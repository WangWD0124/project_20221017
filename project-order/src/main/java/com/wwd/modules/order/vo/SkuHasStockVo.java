package com.wwd.modules.order.vo;

import lombok.Data;

/**
 * 查询sku的库存量
 */
@Data
public class SkuHasStockVo {

    private Long skuId;

    private Long stock;
}
