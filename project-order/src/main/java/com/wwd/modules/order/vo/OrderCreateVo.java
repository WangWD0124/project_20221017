package com.wwd.modules.order.vo;

import com.wwd.modules.order.dto.OrderDTO;
import com.wwd.modules.order.dto.OrderItemDTO;
import com.wwd.modules.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCreateVo {

    private OrderDTO order;

    private List<OrderItemDTO> orderItemDTOS;

    private BigDecimal payPrice;//订单计算的应付价格

    private BigDecimal fare;//运费
}
