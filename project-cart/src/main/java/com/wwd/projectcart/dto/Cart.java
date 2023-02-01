package com.wwd.projectcart.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车
 */
@Data
public class Cart {

    List<CartItem> items;
    private Integer countNum;//商品数量
    private Integer countType;//商品类型数量
    private BigDecimal totalAmount;//商品总价
    private BigDecimal reduce = new BigDecimal("0.00");//减免价格

    public Integer getCountNum() {
        int count = 0;
        if (this.items != null && this.items.size() > 0){
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public void setCountNum(Integer countNum) {
        this.countNum = countNum;
    }

    public Integer getCountType() {
        int count = 0;
        if (this.items != null && this.items.size() > 0){
            for (CartItem item : items) {
                count += 1;
            }
        }
        return count;
    }

    public void setCountType(Integer countType) {
        this.countType = countType;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0");
        if (this.items != null && this.items.size() > 0){
            for (CartItem item : items) {
                amount = amount.add(item.getTotalPrice());
            }
        }
        BigDecimal subtract = amount.subtract(getReduce());

        return subtract;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
