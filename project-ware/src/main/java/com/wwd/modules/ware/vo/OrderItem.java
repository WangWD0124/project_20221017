package com.wwd.modules.ware.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项
 */
@Data
public class OrderItem {

    private Long skuId;
    private Boolean check = true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
    /**
     * 订单商品总重量
     */
    private BigDecimal weight;

    public BigDecimal getWeight() {
        return new BigDecimal("0.888");
    }

    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(""+this.count));
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
