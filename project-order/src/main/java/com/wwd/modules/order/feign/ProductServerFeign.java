package com.wwd.modules.order.feign;

import com.wwd.common.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

@FeignClient("project-product")
public interface ProductServerFeign {


}
