package com.wwd.projectcart.service;

import com.wwd.projectcart.dto.Cart;
import com.wwd.projectcart.dto.CartItem;

import java.util.concurrent.ExecutionException;

public interface CartService {
    void addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    CartItem getCartItem(Long skuId);

    Cart getCart() throws ExecutionException, InterruptedException;

    void clearCart(String cartKey);

    void checkCart(Long skuId, Integer isChecked);

    void countItem(Long skuId, Integer num);

    void deleteItem(Long skuId);
}
