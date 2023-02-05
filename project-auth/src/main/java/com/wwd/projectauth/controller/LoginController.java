package com.wwd.projectauth.controller;

import com.wwd.common.utils.Result;
import com.wwd.common.constant.AuthServerConstant;
import com.wwd.projectauth.dto.UserLoginDTO;
import com.wwd.projectauth.dto.UserRegistDTO;
import com.wwd.projectauth.feign.MemberFeignServer;
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

    /**
     * 社交登录
     */
//    @GetMapping("oauth/gitee/success")
//    public Object socialLogin(@RequestParam("code") String code, HttpSession session) throws IOException {
//
//        HashMap<String, String> map = new HashMap<>();
//
//        map.put("grant_type", "authorization_code");
//        map.put("client_id", "cb922fb6126a9cd3a6eb644c4443e2f78e0c225c52a08e9b4525baeec5b72eef");
//        map.put("client_secret", "217ce839177e4926d0d3f099d1cbd2e04ec28f87970ba068335da1729932c56c");
//        map.put("redirect_uri", "http://auth.project.com/oauth/gitee/success");
//        map.put("code", code);
//        //1、根据code获取accessToken
//        HttpUtils.doPost()
//
//        HttpClient httpClient = HttpClientBuilder.create().build();
//        HttpPost httpPost = new HttpPost(url);
//        HttpResponse response = httpClient.execute(httpPost);
//        //2、处理
//        if (response.getStatusLine().getStatusCode() == 200) {
//            //获取到accessToken
//
//            String json = EntityUtils.toString(response.getEntity());
//            System.out.println("获取到的token为：" + json);
//            JSONObject jsonObject = JSON.parseObject(json);
//            String access_token = jsonObject.getString("access_token");
//            System.out.println("获取到的access_token：" + access_token);
//
//
//            //gitee还需要再去请求user去获取数据
////            GiteeUser giteeUser =  giteeComponent.getGiteeUser(json);
//            String urluser = "https://gitee.com/api/v5/user?access_token=" + access_token;
//            HttpClient httpClientUser = HttpClientBuilder.create().build();
//            HttpGet httpPostUser = new HttpGet(urluser);           //记得用httpGet请求，否则会405拒绝请求
//            HttpResponse responseUser = httpClientUser.execute(httpPostUser);
//
//            GiteeUser giteeUser = new GiteeUser();
//            String user = EntityUtils.toString(responseUser.getEntity());
//            System.out.println("gitee用户信息"+user);
//
//            JSONObject jsonObjectUser = JSON.parseObject(user);
//            String id = jsonObjectUser.getString("id");
//            System.out.println(id);
//            giteeUser.setId(id);
//            String name = jsonObjectUser.getString("name");
//            System.out.println(name);
//            giteeUser.setName(name);
//            String bio = jsonObjectUser.getString("bio");
//            System.out.println(bio);
//            giteeUser.setBio(bio);
//
//            //知道当前是哪个社交用户登录成功
//            //1、当前用户如果是第一次进网站，就自动注册进来（为当前社交用户生成一个会员信息账号,以后这个社交账号就对应指定的会员）
//            //登录或者注册这个社交用户
////            Result r = memberFeignServer.oauth2Login(giteeUser);
////            if (r.getCode() == 0) {
////                MemberRespVo memberRespVo = r.getData(new TypeReference<MemberRespVo>() {
////                });
////                System.out.println("登录成功，用户信息：" + memberRespVo);
////                log.info("登录成功，用户信息：" + memberRespVo);
////                //TODO 1、默认发的令牌 session=dadas,作用域只是当前域，（解决子域与父域session共享问题）
////                //TODO 2、使用json的序列化方式来序列化对象数据到redis中
////                session.setAttribute(AuthServerConstant.SESSION_LOGIN_KEY, memberRespVo);
////                //2、登录成功就跳回首页
////                return "redirect:http://project.com";
////            } else {
////                return "redirect:http://auth.project.com/login.html";
////            }
//            return "redirect:http://project.com";
//        } else {
//            return "redirect:http://auth.project.com/login.html";
//        }
////        if (response.getStatusLine().get)
////        String url = "https://gitee.com/oauth/token";
////        OAuthTokenDTO oAuthTokenDTO = new OAuthTokenDTO();
////        oAuthTokenDTO.setCode(code);
////        Object object = restTemplate.postForObject(url, oAuthTokenDTO, Object.class);
////        return null;
//    }
//
//    @GetMapping("oauth2.0/gitee/fail")
//    public String socialLoginFail(@RequestParam("code") String code){
//        return null;
//    }

}
















