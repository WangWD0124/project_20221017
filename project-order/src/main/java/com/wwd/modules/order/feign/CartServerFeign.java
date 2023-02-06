package com.wwd.modules.order.feign;

import com.wwd.common.utils.Result;
import com.wwd.modules.order.vo.OrderItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("project-cart")
public interface CartServerFeign {

    @GetMapping("getOrderItemsByMemberId/{memberId}")
    Result<List<OrderItem>> getOrderItemsByMemberId(@PathVariable("memberId") Long memberId);
}
