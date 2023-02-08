package com.wwd.modules.ware.feign;

import com.wwd.common.utils.Result;
import com.wwd.modules.ware.vo.MemberReceiveAddressDTO;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("project-member")
public interface MemberServiceFeign {

    @GetMapping("{id}")
    Result<MemberReceiveAddressDTO> get(@PathVariable("id") Long id);
}
