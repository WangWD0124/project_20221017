package com.wwd.projectcart.service.Impl;


import com.alibaba.fastjson.JSON;
import com.wwd.common.utils.Result;
import com.wwd.projectcart.dto.Cart;
import com.wwd.projectcart.dto.CartItem;
import com.wwd.projectcart.dto.SkuInfoDTO;
import com.wwd.projectcart.dto.UserInfoDTO;
import com.wwd.projectcart.feign.ProductFeignService;
import com.wwd.projectcart.intercepter.CartIntercepter;
import com.wwd.projectcart.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ThreadPoolExecutor executor;

    @Autowired
    private ProductFeignService productFeignService;

    private final String CART_PREFIX = "cart:";

    /**
     * 添加商品到购物车
     * @param skuId
     * @param num
     * @return
     */
    @Override
    public void addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        BoundHashOperations<String, Object, Object> cart = getCartOps();
        //判断商品是否已经存在购物车
        String res = (String) cart.get(skuId.toString());
        if (StringUtils.isEmpty(res)){//商品不存在
            CartItem cartItem = new CartItem();

            CompletableFuture<Void> skuInfoDTOCompletableFuture = CompletableFuture.runAsync(() -> {
                cartItem.setSkuId(skuId);
                cartItem.setCheck(true);
                cartItem.setCount(num);
                Result<SkuInfoDTO> result_skuInfoDTO = productFeignService.get(skuId);
                SkuInfoDTO skuInfoDTO = result_skuInfoDTO.getData();
                cartItem.setTitle(skuInfoDTO.getSkuTitle());
                cartItem.setImage(skuInfoDTO.getSkuDefaultImg());
                cartItem.setPrice(skuInfoDTO.getPrice());
            }, executor);

            CompletableFuture<Void> skuAttrCompletableFuture = CompletableFuture.runAsync(() -> {
                Result<List<String>> result_skuAttr = productFeignService.getSaleAttrValueStringListBySkuId(skuId);
                List<String> skuAttr = result_skuAttr.getData();
                cartItem.setSkuAttr(skuAttr);
            }, executor);


            CompletableFuture.allOf(skuInfoDTOCompletableFuture, skuAttrCompletableFuture).get();
            String string = JSON.toJSONString(cartItem);
            cart.put(cartItem.getSkuId().toString(), string);
        } else {//商品存在，只增加数量
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            cartItem.setCount(cartItem.getCount() + num);
            String string = JSON.toJSONString(cartItem);
            cart.put(skuId.toString(), string);
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {

        BoundHashOperations<String, Object, Object> cart = getCartOps();
        String res = (String) cart.get(skuId);
        CartItem cartItem = JSON.parseObject(res, CartItem.class);
        return cartItem;
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {

        Cart cart = new Cart();
        //判断加载临时购物车还是加载用户购物车
        UserInfoDTO userInfoDTO = CartIntercepter.threadLocal.get();
        String cartKey = "";
        if (userInfoDTO.getUserId() != null){//已登录用户
            List<CartItem> tempCartItems = getCartItems(CART_PREFIX + userInfoDTO.getUserKey());//临时购物车项
            if (tempCartItems != null) {//如果临时购物车还存在购物项未合并，则合并并删除临时购物车
                for (CartItem tempCartItem : tempCartItems) {
                    addToCart(tempCartItem.getSkuId(), tempCartItem.getCount());
                }
            }
            clearCart(CART_PREFIX + userInfoDTO.getUserKey());
            List<CartItem> cartItems = getCartItems(CART_PREFIX + userInfoDTO.getUserId());//用户购物车
            cart.setItems(cartItems);
        } else { //临时用户
            List<CartItem> cartItems = getCartItems(CART_PREFIX + userInfoDTO.getUserKey());
            cart.setItems(cartItems);
        }
        return cart;
    }

    @Override
    public void clearCart(String cartKey) {

        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkCart(Long skuId, Integer isChecked) {

        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(isChecked==1?true:false);
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(cartItem.getSkuId().toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void countItem(Long skuId, Integer num) {

        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(cartItem.getCount() + num);
        cartItem.setTotalPrice(cartItem.getTotalPrice());
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.put(cartItem.getSkuId().toString(), JSON.toJSONString(cartItem));
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    private List<CartItem> getCartItems(String cartKey) {

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0){
            List<CartItem> cartItems = values.stream().map(value ->
                    JSON.parseObject(value.toString(), CartItem.class)
            ).collect(Collectors.toList());
            return cartItems;
        }
        return null;
    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        //判断加到临时购物车还是加到用户购物车
        UserInfoDTO userInfoDTO = CartIntercepter.threadLocal.get();
        String cartKey = "";
        if (userInfoDTO.getUserId() != null){//已登录用户
            cartKey = CART_PREFIX + userInfoDTO.getUserId();
        } else { //临时用户
            cartKey = CART_PREFIX + userInfoDTO.getUserKey();
        }

        BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(cartKey);
        return operations;
    }


}
