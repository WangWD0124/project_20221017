package com.wwd.modules.menber.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 积分变化历史记录
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@TableName("ums_integration_change_history")
public class IntegrationChangeHistoryEntity {

    /**
     * id
     */
	private Long id;
    /**
     * member_id
     */
	private Long memberId;
    /**
     * create_time
     */
	private Date createTime;
    /**
     * 变化的值
     */
	private Integer changeCount;
    /**
     * 备注
     */
	private String note;
    /**
     * 来源[0->购物；1->管理员修改;2->活动]
     */
	private Integer sourceTyoe;
}