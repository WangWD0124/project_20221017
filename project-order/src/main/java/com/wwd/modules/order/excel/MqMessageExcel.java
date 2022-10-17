package com.wwd.modules.order.excel;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Data
public class MqMessageExcel {
    @Excel(name = "")
    private String messageId;
    @Excel(name = "JSON")
    private String content;
    @Excel(name = "")
    private String toExchange;
    @Excel(name = "")
    private String classType;
    @Excel(name = "0-新建 1-已发送 2-错误抵达 3-已抵达")
    private Integer messageStatus;
    @Excel(name = "")
    private Date createTime;
    @Excel(name = "")
    private Date updateTime;

}