package com.wwd.modules.product.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;
import java.util.List;

/**
 * sku信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@ApiModel(value = "sku信息")
public class SkuInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "skuId")
	private Long skuId;

	@ApiModelProperty(value = "spuId")
	private Long spuId;

	@ApiModelProperty(value = "sku名称")
	private String skuName;

	@ApiModelProperty(value = "sku介绍描述")
	private String skuDesc;

	@ApiModelProperty(value = "所属分类id")
	private Long catalogId;

	@ApiModelProperty(value = "品牌id")
	private Long brandId;

	@ApiModelProperty(value = "默认图片")
	private String skuDefaultImg;

	@ApiModelProperty(value = "标题")
	private String skuTitle;

	@ApiModelProperty(value = "副标题")
	private String skuSubtitle;

	@ApiModelProperty(value = "价格")
	private BigDecimal price;

	@ApiModelProperty(value = "销量")
	private Long saleCount;

	/**
	 * 图片集
	 */
	@ApiModelProperty(value = "图片集")
	private List<SkuImagesDTO> images;

	/**
	 * sku属性及其值
	 */
	@ApiModelProperty(value = "sku属性及其值")
	private List<SkuSaleAttrValueDTO> attr;

	/**
	 * sku属性及其值
	 */

}