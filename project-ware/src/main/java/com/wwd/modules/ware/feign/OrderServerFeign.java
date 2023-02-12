package com.wwd.modules.ware.feign;

import com.wwd.common.utils.Result;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("project-order")
public interface OrderServerFeign {

    //订单状态信息
    @GetMapping("order/order/status/{orderSn}")
    public Result<Integer> getStatusByOrderSn(@PathVariable("orderSn") String orderSn);

    //修改订单状态信息
    @GetMapping("updateStatus/{orderSn}/{status}")
    public Result<Integer> updateStatusByOrderSn(@PathVariable("orderSn") String orderSn, @PathVariable("status") Integer status);

}
