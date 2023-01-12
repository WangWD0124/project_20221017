package com.wwd.projectauth.feign;

import com.wwd.common.utils.Result;
import com.wwd.projectauth.dto.UserRegistDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("project-member")
public interface MemberFeignServer {

    @PostMapping("member/member/register")
    public Result register(@RequestBody UserRegistDTO userRegistDTO);
}
