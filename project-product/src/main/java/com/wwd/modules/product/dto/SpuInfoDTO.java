package com.wwd.modules.product.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;
import java.util.List;

/**
 * spu信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@ApiModel(value = "spu信息")
public class SpuInfoDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "商品id")
	private Long id;

	@ApiModelProperty(value = "商品名称")
	private String spuName;

	@ApiModelProperty(value = "商品描述")
	private String spuDescription;

	@ApiModelProperty(value = "所属分类id")
	private Long catalogId;

	@ApiModelProperty(value = "品牌id")
	private Long brandId;

	@ApiModelProperty(value = "")
	private BigDecimal weight;

	@ApiModelProperty(value = "上架状态[0 - 下架，1 - 上架]")
	private Integer publishStatus;

	@ApiModelProperty(value = "")
	private Date createTime;

	@ApiModelProperty(value = "")
	private Date updateTime;

	/**
	 * 接收spu和sku其他信息
	 */
	@ApiModelProperty(value = "积分、成长值")
	private SpuBoundsDTO bounds;

	@ApiModelProperty(value = "商品详情图集")
	private List<String> decript;

	@ApiModelProperty(value = "商品图集")
	private List<String> images;

	@ApiModelProperty(value = "基本属性")
	private List<ProductAttrValueDTO> baseAttrs;

	@ApiModelProperty(value = "所有sku信息")
	private List<SkuInfoDTO> skus;


}