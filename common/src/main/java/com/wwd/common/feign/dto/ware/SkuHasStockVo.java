package com.wwd.common.feign.dto.ware;

import lombok.Data;

/**
 * 查询sku的库存量
 */
@Data
public class SkuHasStockVo {

    private Long skuId;

    private Long stock;
}
