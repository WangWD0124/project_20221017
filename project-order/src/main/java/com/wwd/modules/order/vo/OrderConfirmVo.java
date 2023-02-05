package com.wwd.modules.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderConfirmVo {

    /**
     * 收货地址列表
     */
    private List<MemberReceiveAddressDTO> memberAddressVos;
    /**
     * 订单购物项
     */
    private List<OrderItem> items;

    /**
     * 订单商品总数
     */
    private Integer count;

    public Integer getCount() {
        Integer count = 0;
        if (items != null && items.size() > 0){
            for (OrderItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    /**
     * 商品总金额
     */
    private BigDecimal total;

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null && items.size() > 0){
            for (OrderItem item : items) {
                sum.add(item.getTotalPrice());
            }
        }
        return total;
    }

    /**
     * 商品应付金额
     */
    private BigDecimal payPrice;

    public BigDecimal getPayPrice() {
        return getTotal();
    }

    /**
     * 订单确认令牌（防止重复确认）
     */
    private String orderToken;


}
