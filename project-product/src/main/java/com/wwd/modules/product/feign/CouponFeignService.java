package com.wwd.modules.product.feign;

import com.wwd.common.feign.dto.SpuBoundsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("project-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds")
    void save(@RequestBody SpuBoundsDTO dto);

}
