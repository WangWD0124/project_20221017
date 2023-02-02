package com.wwd.projectcart.controller;

import com.wwd.projectcart.dto.Cart;
import com.wwd.projectcart.dto.CartItem;
import com.wwd.projectcart.dto.UserInfoDTO;
import com.wwd.projectcart.intercepter.CartIntercepter;
import com.wwd.projectcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping({"/cart.html"})
    public String cartList(Model model) throws ExecutionException, InterruptedException {

        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * 添加商品至购物车
     * @param skuId
     * @param num
     * @param attributes
     * @return
     */
    @GetMapping("/addCartItem")
    public String addToCart(@RequestParam Long skuId, @RequestParam Integer num, RedirectAttributes attributes) throws ExecutionException, InterruptedException {

        cartService.addToCart(skuId, num);
        attributes.addFlashAttribute("skuId", skuId);
        return "redirect:http://cart.project.com/addToCartSuccess";//TODO 利用重定向避免网页重复刷新造成重复添加商品到购物车，重定向需使用RedirectAttributes
    }

    /**
     * 查询购物项
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/addToCartSuccess")
    public String addToCartSuccess(@RequestParam Long skuId, Model model){

        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem", cartItem);
        return "success";
    }

    @GetMapping("/checkCart")
    public String checkCart(@RequestParam Long skuId, @RequestParam Integer isChecked){

        cartService.checkCart(skuId, isChecked);
        return "redirect:http://cart.project.com/cart.html";
    }

    @GetMapping("/countItem")
    public String countItem(@RequestParam Long skuId, @RequestParam Integer num){

        cartService.countItem(skuId, num);
        return "redirect:http://cart.project.com/cart.html";
    }

    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam Long skuId){

        cartService.deleteItem(skuId);
        return "redirect:http://cart.project.com/cart.html";
    }


}
