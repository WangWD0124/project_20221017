package com.wwd.modules.order.feign;

import com.wwd.common.utils.Result;
import com.wwd.modules.order.vo.FareVo;
import com.wwd.modules.order.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("project-ware")
public interface WareServerFeign {

    @PostMapping("ware/waresku/stock")
    Result<List<SkuHasStockVo>> getSkuHasStockVoByIds(@RequestBody List<Long> skuIds);

    @GetMapping("fare/{addId}")
    public Result<FareVo> getFare(@PathVariable("addrId") Long addrId);
}
