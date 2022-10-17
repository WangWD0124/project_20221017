package com.wwd.modules.menber.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;


/**
 * 会员收藏的商品
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@ApiModel(value = "会员收藏的商品")
public class MemberCollectSpuDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "id")
	private Long id;

	@ApiModelProperty(value = "会员id")
	private Long memberId;

	@ApiModelProperty(value = "spu_id")
	private Long spuId;

	@ApiModelProperty(value = "spu_name")
	private String spuName;

	@ApiModelProperty(value = "spu_img")
	private String spuImg;

	@ApiModelProperty(value = "create_time")
	private Date createTime;


}