package com.wwd.projectcart.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * sku销售属性&值
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@ApiModel(value = "sku销售属性&值")
public class SkuSaleAttrValueDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "sku_id")
	private Long skuId;

	@ApiModelProperty(value = "attr_id")
	private Long attrId;

	@ApiModelProperty(value = "销售属性名")
	private String attrName;

	@ApiModelProperty(value = "销售属性值")
	private String attrValue;

	@ApiModelProperty(value = "顺序")
	private Integer attrSort;


}