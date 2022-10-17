package com.wwd.modules.order.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.order.dto.OrderItemDTO;
import com.wwd.modules.order.entity.OrderItemEntity;

/**
 * 订单项信息
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface OrderItemService extends CrudService<OrderItemEntity, OrderItemDTO> {

}