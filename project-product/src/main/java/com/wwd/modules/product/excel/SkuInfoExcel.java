package com.wwd.modules.product.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * sku信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
public class SkuInfoExcel {
    @Excel(name = "skuId")
    private Long skuId;
    @Excel(name = "spuId")
    private Long spuId;
    @Excel(name = "sku名称")
    private String skuName;
    @Excel(name = "sku介绍描述")
    private String skuDesc;
    @Excel(name = "所属分类id")
    private Long catalogId;
    @Excel(name = "品牌id")
    private Long brandId;
    @Excel(name = "默认图片")
    private String skuDefaultImg;
    @Excel(name = "标题")
    private String skuTitle;
    @Excel(name = "副标题")
    private String skuSubtitle;
    @Excel(name = "价格")
    private BigDecimal price;
    @Excel(name = "销量")
    private Long saleCount;

}