package com.wwd.projectcart.controller;

import com.wwd.projectcart.dto.UserInfoDTO;
import com.wwd.projectcart.intercepter.CartIntercepter;
import com.wwd.projectcart.service.CartService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class CartController {

    @GetMapping({"/cart.html"})
    public String cartList(HttpSession session){

        UserInfoDTO userInfoDTO = CartIntercepter.threadLocal.get();
        System.out.println("-------------"+userInfoDTO);
        return "cartList";
    }
}
