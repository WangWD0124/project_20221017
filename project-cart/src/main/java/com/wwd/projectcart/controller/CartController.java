package com.wwd.projectcart.controller;

import com.wwd.common.utils.Result;
import com.wwd.projectcart.dto.Cart;
import com.wwd.projectcart.dto.CartItem;
import com.wwd.projectcart.dto.UserInfoDTO;
import com.wwd.projectcart.intercepter.CartIntercepter;
import com.wwd.projectcart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 访问临时或用户购物车
     * @param model
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
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
     * 查询添加成功后的单个购物项
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

    @GetMapping("/getOrderItemsByMemberId/{memberId}")
    public Result<List<CartItem>> getOrderItemsByMemberId(@PathVariable("memberId") Long memberId){

        List<CartItem> cartItems = cartService.getCartItemsChecked(memberId);
        return new Result<List<CartItem>>().ok(cartItems);
    }


    /**
     * 修改购物项-是否选中
     * @param skuId
     * @param isChecked
     * @return
     */
    @GetMapping("/checkCart")
    public String checkCart(@RequestParam Long skuId, @RequestParam Integer isChecked){

        cartService.checkCart(skuId, isChecked);
        return "redirect:http://cart.project.com/cart.html";
    }

    /**
     * 修改购物项-数量
     * @param skuId
     * @param num
     * @return
     */
    @GetMapping("/countItem")
    public String countItem(@RequestParam Long skuId, @RequestParam Integer num){

        cartService.countItem(skuId, num);
        return "redirect:http://cart.project.com/cart.html";
    }

    /**
     * 删除购物项
     * @param skuId
     * @return
     */
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam Long skuId){

        cartService.deleteItem(skuId);
        return "redirect:http://cart.project.com/cart.html";
    }


}
