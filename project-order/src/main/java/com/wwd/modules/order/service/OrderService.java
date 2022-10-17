package com.wwd.modules.order.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.order.dto.OrderDTO;
import com.wwd.modules.order.entity.OrderEntity;

/**
 * 订单
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface OrderService extends CrudService<OrderEntity, OrderDTO> {

}