package com.wwd.projectauth.controller;

import com.wwd.common.utils.Result;
import com.wwd.projectauth.constant.AuthServerConstant;
import com.wwd.projectauth.dto.UserRegistDTO;
import com.wwd.projectauth.feign.MemberFeignServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class LoginController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private MemberFeignServer memberFeignServer;

    /**
     * 注册
     */
    @PostMapping("/register")
    public String register(@Valid UserRegistDTO userRegistDTO, BindingResult result, RedirectAttributes redirectAttributes){//TODO RedirectAttributes页面重定向携带数据，session原理

        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",errors);

            //效验出错回到注册页面
            return "redirect:http://auth.project.com/reg.html";
        }
        //效验正确注册用户

        //1、校验验证码
        String code = userRegistDTO.getCode();

        String cachedCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegistDTO.getPhone());
        //if (!StringUtils.isEmpty(code)){
            //if (code.equals(cachedCode.split("_")[0])){
                //redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegistDTO.getPhone());
                //2、校验用户名是否存在


                //3、校验手机号码是否存在


                //4、提交用户数据进行保存
                Result registerResult = memberFeignServer.register(userRegistDTO);
                if (registerResult.getCode()==0){
                    //4-1、完成注册，跳转登录页
                    return "redirect:http://auth.project.com/login.html";
                } else {
                    //4-2、注册失败，跳转注册页
                    return "redirect:http://auth.project.com/reg.html";
                }
            //} else {
//                Map<String, String> errors2 = new HashMap<>();
//                errors2.put("code", "验证码错误");
//                redirectAttributes.addFlashAttribute("errors", errors2);
//                return "redirect:http://auth.project.com/reg.html";
            //}
        //} else {
//            Map<String, String> errors1 = new HashMap<>();
//            errors1.put("code", "验证码为空");
//            redirectAttributes.addFlashAttribute("errors", errors1);
//            return "redirect:http://auth.project.com/reg.html";
        //}


    }
}
















