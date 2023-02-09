package com.wwd.modules.ware.exception;

public class NotStockException extends Throwable {
    public NotStockException(Long skuId) {

        System.out.println("商品没有库存："+skuId);
    }
}
