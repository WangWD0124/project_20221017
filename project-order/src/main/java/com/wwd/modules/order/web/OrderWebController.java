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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String order(@RequestBody OrderSubmitVo orderSubmitVo, Model model, RedirectAttributes redirectAttributes) {
        //提交订单确认信息
        OrderSubmitResponseVo orderSubmitResponseVo = new OrderSubmitResponseVo();
        String msg = "下单失败：";
        try {
            orderSubmitResponseVo = orderService.orderSubmit(orderSubmitVo);
        } catch (Exception e){
            msg += "商品库存不足，订单库存锁定失败";
        }
        if (orderSubmitResponseVo.getCode() == 0){//提交订单成功
            model.addAttribute("order", orderSubmitResponseVo.getOrder());
            return "pay";//跳转支付页面
        } else {
            switch (orderSubmitResponseVo.getCode()){
                case 1: msg += "订单信息过期，请刷新"; break;
                case 2: msg += "订单商品价格发生变化，请确认后再次提交"; break;
            }
        }
        redirectAttributes.addFlashAttribute("msg", msg);
        return "redirect:http://order.project.com/toTrade";//提交订单失败，跳转回订单确认页
    }
}








