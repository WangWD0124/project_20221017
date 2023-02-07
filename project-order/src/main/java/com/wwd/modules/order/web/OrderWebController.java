package com.wwd.modules.order.web;

import com.wwd.modules.order.service.OrderService;
import com.wwd.modules.order.vo.OrderConfirmVo;
import com.wwd.modules.order.vo.OrderSubmitResponseVo;
import com.wwd.modules.order.vo.OrderSubmitVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        //订单确认页信息
        OrderConfirmVo confirmOrder = orderService.orderConfirm();
        model.addAttribute("confirmOrder", confirmOrder);
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String order(@RequestBody OrderSubmitVo orderSubmitVo) {
        //提交订单确认信息
        OrderSubmitResponseVo orderSubmitResponseVo = orderService.orderSubmit(orderSubmitVo);
        if (orderSubmitResponseVo.getCode() == 0){//提交订单成功
            return "pay";//跳转支付页面
        } else {
            return "";//提交订单失败，跳转回订单确认页
        }
    }
}








