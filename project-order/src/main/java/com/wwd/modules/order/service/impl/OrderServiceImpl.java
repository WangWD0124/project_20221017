package com.wwd.modules.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wwd.common.service.impl.CrudServiceImpl;
import com.wwd.common.utils.Result;
import com.wwd.modules.order.dao.OrderDao;
import com.wwd.modules.order.dto.OrderDTO;
import com.wwd.modules.order.entity.OrderEntity;
import com.wwd.modules.order.feign.CartServerFeign;
import com.wwd.modules.order.feign.MemberServerFeign;
import com.wwd.modules.order.interceptor.LoginUserInterceptor;
import com.wwd.modules.order.service.OrderService;
import com.wwd.modules.order.vo.MemberDTO;
import com.wwd.modules.order.vo.MemberReceiveAddressDTO;
import com.wwd.modules.order.vo.OrderConfirmVo;
import com.wwd.modules.order.vo.OrderItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单
 *
 * @author wwd 1245436962@qq.com
 * @since 1.0.0 2022-10-14
 */
@Service
public class OrderServiceImpl extends CrudServiceImpl<OrderDao, OrderEntity, OrderDTO> implements OrderService {

    @Autowired
    private MemberServerFeign memberServerFeign;

    @Autowired
    private CartServerFeign cartServerFeign;

    @Override
    public QueryWrapper<OrderEntity> getWrapper(Map<String, Object> params){
        String id = (String)params.get("id");

        QueryWrapper<OrderEntity> wrapper = new QueryWrapper<>();
        wrapper.eq(StringUtils.isNotBlank(id), "id", id);

        return wrapper;
    }

    /**
     * 订单确认页
     * @return
     */
    @Override
    public OrderConfirmVo orderConfirm() {

        OrderConfirmVo confirmOrder = new OrderConfirmVo();
        //用户
        MemberDTO memberDTO = LoginUserInterceptor.threadLocal.get();
        Long memberId = memberDTO.getId();
        /**
         * 查询用户收货地址列表
         */
        List<MemberReceiveAddressDTO> addressDTOList = memberServerFeign.getAddressListByMemberId(memberId).getData();
        confirmOrder.setMemberAddressVos(addressDTOList);
        /**
         * 订单购物项
         */
        List<OrderItem> items = cartServerFeign.getOrderItemsByMemberId(memberId).getData();
        confirmOrder.setItems(items);
        /**
         * TODO 订单确认令牌（防止重复确认） , 幂等性
         */
        return confirmOrder;
    }
}























