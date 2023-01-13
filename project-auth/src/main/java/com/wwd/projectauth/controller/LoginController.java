package com.wwd.projectauth.controller;

import com.wwd.common.utils.Result;
import com.wwd.projectauth.constant.AuthServerConstant;
import com.wwd.projectauth.dto.UserLoginDTO;
import com.wwd.projectauth.dto.UserRegistDTO;
import com.wwd.projectauth.feign.MemberFeignServer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
     * 注册发送手机验证码
     */
    //TODO 注册发送手机验证码

    /**
     * 注册
     */
    @PostMapping("/register")
    public String register(@Valid UserRegistDTO userRegistDTO, BindingResult result, RedirectAttributes redirectAttributes){//TODO RedirectAttributes页面重定向携带数据，session原理
        //效验数据格式要求出错
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",errors);
            //回到注册页面
            return "redirect:http://auth.project.com/reg.html";
        }
        //1、校验验证码//TODO 校验验证码
        String code = userRegistDTO.getCode();//提交的验证码
        String cachedCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegistDTO.getPhone());//正确验证码
        //if (!StringUtils.isEmpty(code)){
            //if (code.equals(cachedCode.split("_")[0])){
                //redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + userRegistDTO.getPhone());
                //2、验证码校验通过，提交用户数据进行保存
                Result registerResult = memberFeignServer.register(userRegistDTO);
                //3-1、手机号已被注册
                if (registerResult.getCode()==1){
                    Map<String, String> registeErrors = new HashMap<>();
                    registeErrors.put("phone", registerResult.getMsg());
                    redirectAttributes.addFlashAttribute("errors", registeErrors);
                    return "redirect:http://auth.project.com/reg.html";//注册失败，跳转注册页
                }
                //3-2、用户名已存在
                if (registerResult.getCode() == 2) {
                    Map<String, String> registeErrors = new HashMap<>();
                    registeErrors.put("userName", registerResult.getMsg());
                    redirectAttributes.addFlashAttribute("errors", registeErrors);
                    return "redirect:http://auth.project.com/reg.html";//注册失败，跳转注册页
                }
                //3-3、完成注册，跳转登录页
                return "redirect:http://auth.project.com/login.html";
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

    /**
     * 登录
     */
    @PostMapping("login")
    public String login(@Valid UserLoginDTO userLoginDTO, BindingResult result, RedirectAttributes redirectAttributes){

        //效验数据格式要求出错
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors",errors);
            //回到登录页面
            return "redirect:http://auth.project.com/login.html";
        }
        Result loginResult = memberFeignServer.login(userLoginDTO);
        if (loginResult.getCode() == 0){
            return "redirect:http://project.com";//登录成功，跳转首页
        } else {
            Map<String, String> loginErrors = new HashMap<>();
            loginErrors.put("msg", loginResult.getMsg());
            redirectAttributes.addFlashAttribute("errors", loginErrors);
            return "redirect:http://auth.project.com/login.html";//登录失败，跳转登录页
        }
    }
}
















