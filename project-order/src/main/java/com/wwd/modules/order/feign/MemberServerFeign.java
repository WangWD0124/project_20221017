package com.wwd.modules.order.feign;

import com.wwd.common.utils.Result;
import com.wwd.modules.order.vo.MemberReceiveAddressDTO;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("project-member")
public interface MemberServerFeign {

    /**
     * 远程查询收货地址
     */
    @GetMapping("menber/memberreceiveaddress/addressList/{memberId}")
    Result<List<MemberReceiveAddressDTO>> getAddressListByMemberId(@PathVariable("memberId") Long memberId);
}
