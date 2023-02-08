package com.wwd.modules.order.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


/**
 * spu属性值
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@ApiModel(value = "spu属性值")
public class ProductAttrValueDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "商品id")
	private Long spuId;

	@ApiModelProperty(value = "属性id")
	private Long attrId;

	@ApiModelProperty(value = "属性名")
	private String attrName;

	@ApiModelProperty(value = "属性值")
	private String attrValue;

	@ApiModelProperty(value = "顺序")
	private Integer attrSort;

	@ApiModelProperty(value = "快速展示【是否展示在介绍上；0-否 1-是】")
	private Integer quickShow;


}