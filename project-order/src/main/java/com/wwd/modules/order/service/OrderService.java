package com.wwd.modules.order.service;

import com.wwd.common.service.CrudService;
import com.wwd.modules.order.dto.OrderDTO;
import com.wwd.modules.order.entity.OrderEntity;
import com.wwd.modules.order.vo.OrderConfirmVo;
import com.wwd.modules.order.vo.OrderSubmitResponseVo;
import com.wwd.modules.order.vo.OrderSubmitVo;

import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
public interface OrderService extends CrudService<OrderEntity, OrderDTO> {

    /**
     * 确认订单
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    OrderConfirmVo orderConfirm() throws ExecutionException, InterruptedException;

    /**
     * 提交订单
     * @param orderSubmitVo
     * @return
     */
    OrderSubmitResponseVo orderSubmit(OrderSubmitVo orderSubmitVo);

    /**
     * 查询订单状态
     * @param orderSn
     * @return
     */
    Integer getStatusByOrderSn(String orderSn);

    Long updateStatusByOrderSn(String orderSn, Integer status);
}