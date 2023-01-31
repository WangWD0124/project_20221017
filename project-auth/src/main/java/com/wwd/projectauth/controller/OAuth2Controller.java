package com.wwd.projectauth.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwd.common.utils.Result;
import com.wwd.projectauth.dto.SocialUser;
import com.wwd.projectauth.feign.MemberFeignServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    private MemberFeignServer memberFeignServer;

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
     * 授权回调
     */
    @GetMapping(value = "/oauth/gitee/callback")
    public String giteeCallback(HttpServletRequest request, HttpSession session) throws Exception {
        // 得到Authorization Code
        String code = request.getParameter("code");
        // 我们放在地址中的状态码
        String state = request.getParameter("state");
        String uuid = (String) session.getAttribute("state");
        // 验证信息我们发送的状态码
        if (null != uuid) {
            // 状态码不正确，直接返回登录页面
            if (!uuid.equals(state)) {
                return "redirect:http://auth.project.com/login.html";
            }
        }

        // Step2：通过Authorization Code获取Access Token
        String url = "https://gitee.com/oauth/token?grant_type=authorization_code" +
                "&client_id=" + CLIENTID +
                "&client_secret=" + CLIENTSECRET +
                "&code=" + code +
                "&redirect_uri=" + URL;
        JSONObject accessTokenJson = GiteeHttpClient.getAccessToken(url);
        SocialUser socialUser = new SocialUser();
        socialUser.setAlcess_token(accessTokenJson.getString("access_token"));
        socialUser.setExpires_in(accessTokenJson.getString("expires_in"));

        // Step3: 获取用户信息
        url = "https://gitee.com/api/v5/user?access_token=" + accessTokenJson.get("access_token");
        JSONObject jsonObject = GiteeHttpClient.getUserInfo(url);
        socialUser.setSocial_uid(jsonObject.getString("id"));
        socialUser.setName(jsonObject.getString("name"));

        /**
         * 获取到用户信息之后，就该写你自己的业务逻辑了
         */
        Result result = memberFeignServer.giteeInfo(socialUser);
        session.setAttribute("loginUser", result.getData());
        return "redirect:http://project.com";
    }
}
