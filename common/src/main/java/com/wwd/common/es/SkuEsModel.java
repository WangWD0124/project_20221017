package com.wwd.common.es;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SkuEsModel {

    @ApiModelProperty(value = "skuId")
    private Long skuId;

    @ApiModelProperty(value = "标题")
    private String skuTitle;

    @ApiModelProperty(value = "默认图片")
    private String skuDefaultImg;

    @ApiModelProperty(value = "价格")
    private BigDecimal price;

    @ApiModelProperty(value = "库存")
    private Boolean hasStock;

    @ApiModelProperty(value = "销量")
    private Long saleCount;

    @ApiModelProperty(value = "热度评分")
    private Long hotScore;

    @ApiModelProperty(value = "所属分类id")
    private Long catalogId;

    @ApiModelProperty(value = "分类名称")
    private String catalogName;

    @ApiModelProperty(value = "品牌id")
    private Long brandId;

    @ApiModelProperty(value = "品牌名")
    private String brandName;

    @ApiModelProperty(value = "品牌logo地址")
    private String logo;

    @ApiModelProperty(value = "spuId")
    private Long spuId;

    @ApiModelProperty(value = "spu信息集合")
    private List<Attr> attrs;

    @Data
    public static class Attr {
        private Long attrId;
        private String attrName;
        private String attrValue;
    }

}
