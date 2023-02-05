package com.wwd.modules.order.web;

import com.wwd.modules.order.service.OrderService;
import com.wwd.modules.order.vo.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String order(Model model){
        //订单确认页信息
        OrderConfirmVo confirmOrder = orderService.orderConfirm();
        model.addAttribute("confirmOrder", confirmOrder);
        return "confirm";
    }
}
