package com.wwd.modules.order.vo;

import com.wwd.modules.order.dto.OrderDTO;
import lombok.Data;

/**
 * 封装提交订单的响应数据
 */

@Data
public class OrderSubmitResponseVo {

    private OrderDTO order;
    private Integer code;//成功错误状态码

}













