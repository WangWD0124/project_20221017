package com.wwd.modules.product.feign;

import com.wwd.common.feign.dto.ware.SkuHasStockVo;
import com.wwd.common.utils.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("project-ware")
public interface WareFeignService {

    @PostMapping("/ware/waresku/stock")
    Result<List<SkuHasStockVo>> getSkuHasStockVoByIds(@RequestBody List<Long> skuIds);

}
