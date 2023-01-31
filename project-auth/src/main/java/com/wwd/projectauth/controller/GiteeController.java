package com.wwd.projectauth.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.UUID;

@Controller
public class GiteeController {

    /**
     * gitee授权中提供的 appid 和 appkey
     */
    @Value("${oauth.gitee.clientid}")
    public String CLIENTID;
    @Value("${oauth.gitee.clientsecret}")
    public String CLIENTSECRET;
    @Value("${oauth.gitee.callback}")
    public String URL;
    /**
     * 请求授权页面
     */
    @GetMapping(value = "/oauth/gitee/auth")
    public String giteeAuth(HttpSession session) {
        // 用于第三方应用防止CSRF攻击
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        session.setAttribute("state", uuid);
        // Step1：获取Authorization Code
        String url = "https://gitee.com/oauth/authorize?response_type=code" +
                "&client_id=" + CLIENTID +
                "&redirect_uri=" + URL +
                "&state=" + uuid +
                "&scope=user_info";
        //重定向
        return "redirect:"+url;
    }
}
