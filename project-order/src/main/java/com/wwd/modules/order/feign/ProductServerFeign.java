package com.wwd.modules.order.feign;

import com.wwd.common.utils.Result;
import com.wwd.modules.order.vo.SpuInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient("project-product")
public interface ProductServerFeign {

    @GetMapping("product/spuinfo/skuId/{id}")
    Result<SpuInfoDTO> getSpuInfoBySkuId(@PathVariable("id") Long id);


}
