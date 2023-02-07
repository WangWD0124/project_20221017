package com.wwd.modules.order.feign;

import com.wwd.common.utils.Result;
import com.wwd.modules.order.vo.SkuHasStockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("project-ware")
public interface WareServerFeign {

    @PostMapping("ware/waresku/stock")
    Result<List<SkuHasStockVo>> getSkuHasStockVoByIds(@RequestBody List<Long> skuIds);

}
