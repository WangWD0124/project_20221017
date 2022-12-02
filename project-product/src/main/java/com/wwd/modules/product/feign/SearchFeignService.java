package com.wwd.modules.product.feign;

import com.wwd.common.annotation.LogOperation;
import com.wwd.common.es.SkuEsModel;
import com.wwd.common.utils.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("project-search")
public interface SearchFeignService {

    @PostMapping("/search/product")
    public Result productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList);
}
