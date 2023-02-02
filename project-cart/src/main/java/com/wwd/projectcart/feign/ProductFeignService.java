package com.wwd.projectcart.feign;

import com.wwd.common.utils.Result;
import com.wwd.projectcart.dto.SkuInfoDTO;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("project-product")
public interface ProductFeignService {

    @GetMapping("product/skuinfo/{id}")
    Result<SkuInfoDTO> get(@PathVariable("id") Long id);

    @GetMapping("product/skusaleattrvalue/valueString/{skuId}")
    Result<List<String>> getSaleAttrValueStringListBySkuId(@PathVariable("skuId") Long skuId);

}
