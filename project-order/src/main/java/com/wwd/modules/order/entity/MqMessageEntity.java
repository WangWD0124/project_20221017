package com.wwd.modules.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
@TableName("mq_message")
public class MqMessageEntity {

    /**
     * 
     */
	private String messageId;
    /**
     * JSON
     */
	private String content;
    /**
     * 
     */
	private String toExchange;
    /**
     * 
     */
	private String classType;
    /**
     * 0-新建 1-已发送 2-错误抵达 3-已抵达
     */
	private Integer messageStatus;
    /**
     * 
     */
	private Date createTime;
    /**
     * 
     */
	private Date updateTime;
}