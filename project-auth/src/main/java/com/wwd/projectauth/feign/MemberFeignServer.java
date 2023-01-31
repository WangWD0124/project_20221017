package com.wwd.projectauth.feign;

import com.alibaba.fastjson.JSONObject;
import com.wwd.common.annotation.LogOperation;
import com.wwd.common.utils.Result;
import com.wwd.projectauth.dto.SocialUser;
import com.wwd.projectauth.dto.UserLoginDTO;
import com.wwd.projectauth.dto.UserRegistDTO;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("project-member")
public interface MemberFeignServer {

    @PostMapping("member/member/register")
    public Result register(@RequestBody UserRegistDTO userRegistDTO);

    @PostMapping("member/member/login")
    public Result login(@RequestBody UserLoginDTO userLoginDTO);

    @PostMapping("member/member/giteeInfo")
    public Result giteeInfo(SocialUser socialUser);
}
