package com.wwd.modules.order.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareSkuLockVo {

    private String orderSn;//订单号
    private List<OrderItem> locks;//需要锁任的所有库存信息
}
