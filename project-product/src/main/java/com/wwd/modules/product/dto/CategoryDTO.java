package com.wwd.modules.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * 商品三级分类
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@ApiModel(value = "商品三级分类")
public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "分类id")
	private Long catId;

	@ApiModelProperty(value = "分类名称")
	private String name;

	@ApiModelProperty(value = "父分类id")
	private Long parentCid;

	@ApiModelProperty(value = "层级")
	private Integer catLevel;

	@ApiModelProperty(value = "是否显示[0-不显示，1显示]")
	private Integer showStatus;

	@ApiModelProperty(value = "排序")
	private Integer sort;

	@ApiModelProperty(value = "图标地址")
	private String icon;

	@ApiModelProperty(value = "计量单位")
	private String productUnit;

	@ApiModelProperty(value = "商品数量")
	private Integer productCount;

	/**
	 * 子分类列表（数据库不存在字段）
	 */
	@JsonInclude(JsonInclude.Include.NON_EMPTY)//值为空不参与序列化
	@ApiModelProperty(value = "子分类列表")
	private List<CategoryDTO> children;

}