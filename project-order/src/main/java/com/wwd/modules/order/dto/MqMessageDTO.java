package com.wwd.modules.order.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@ApiModel(value = "")
public class MqMessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "")
	private String messageId;

	@ApiModelProperty(value = "JSON")
	private String content;

	@ApiModelProperty(value = "")
	private String toExchange;

	@ApiModelProperty(value = "")
	private String classType;

	@ApiModelProperty(value = "0-新建 1-已发送 2-错误抵达 3-已抵达")
	private Integer messageStatus;

	@ApiModelProperty(value = "")
	private Date createTime;

	@ApiModelProperty(value = "")
	private Date updateTime;


}