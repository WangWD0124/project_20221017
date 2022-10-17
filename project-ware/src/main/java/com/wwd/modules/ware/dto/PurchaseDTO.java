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

	@ApiModelProperty(value = "")
	private Long id;

	@ApiModelProperty(value = "")
	private Long assigneeId;

	@ApiModelProperty(value = "")
	private String assigneeName;

	@ApiModelProperty(value = "")
	private String phone;

	@ApiModelProperty(value = "")
	private Integer priority;

	@ApiModelProperty(value = "")
	private Integer status;

	@ApiModelProperty(value = "")
	private Long wareId;

	@ApiModelProperty(value = "")
	private BigDecimal amount;

	@ApiModelProperty(value = "")
	private Date createTime;

	@ApiModelProperty(value = "")
	private Date updateTime;


}