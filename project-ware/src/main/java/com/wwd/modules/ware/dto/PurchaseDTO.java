package com.wwd.modules.ware.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.math.BigDecimal;

/**
 * 采购信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@ApiModel(value = "采购信息")
public class PurchaseDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "采购单id")
	private Long id;

	@ApiModelProperty(value = "采购人id")
	private Long assigneeId;

	@ApiModelProperty(value = "采购人姓名")
	private String assigneeName;

	@ApiModelProperty(value = "联系电话")
	private String phone;

	@ApiModelProperty(value = "优先级")
	private Integer priority;

	@ApiModelProperty(value = "状态")
	private Integer status;

	@ApiModelProperty(value = "仓库id")
	private Long wareId;

	@ApiModelProperty(value = "金额")
	private BigDecimal amount;

	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	@ApiModelProperty(value = "修改时间")
	private Date updateTime;


}