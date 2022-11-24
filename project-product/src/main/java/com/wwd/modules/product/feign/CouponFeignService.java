package com.wwd.modules.product.feign;

import com.wwd.common.feign.dto.coupon.MemberPriceDTO;
import com.wwd.common.feign.dto.coupon.SkuFullReductionDTO;
import com.wwd.common.feign.dto.coupon.SkuLadderDTO;
import com.wwd.common.feign.dto.coupon.SpuBoundsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("project-coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds")
    void save(@RequestBody SpuBoundsDTO dto);

    @PostMapping("/coupon/skuladder")
    void save(@RequestBody SkuLadderDTO dto);

    @PostMapping("/coupon/skufullreduction")
    void save(@RequestBody SkuFullReductionDTO dto);

    @PostMapping("/coupon/memberprice")
    void save(@RequestBody MemberPriceDTO dto);

}
