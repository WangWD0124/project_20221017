package com.wwd.projectcart.intercepter;

import com.wwd.common.constant.CartConstant;
import com.wwd.projectcart.dto.MemberDTO;
import com.wwd.projectcart.dto.UserInfoDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * 拦截器：在目标方法执行之前，判断用户的登录状态，并封装传递给controller目标请求
 */
@Component
public class CartIntercepter implements HandlerInterceptor {

    //TODO ThreadLocal实现同一线程数据共享
    public final static ThreadLocal<UserInfoDTO> threadLocal = new ThreadLocal<>();
    /**
     * 在目标方法执行之前
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        HttpSession session = request.getSession();
        MemberDTO loginUser = (MemberDTO) session.getAttribute("loginUser");
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        if (loginUser != null){//已登录
            userInfoDTO.setUserId(loginUser.getId());
        }
        //未登录
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0){
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)){
                    userInfoDTO.setUserKey(cookie.getValue());
                    userInfoDTO.setTempUser(true);//设置临时用户
                }
            }
        }
        //不是临时用户cookie或cookie过期，设置user-key
        if (StringUtils.isEmpty(userInfoDTO.getUserKey())){
            String uuid = UUID.randomUUID().toString();
            userInfoDTO.setUserKey(uuid);
        }

        threadLocal.set(userInfoDTO);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        UserInfoDTO userInfoDTO = threadLocal.get();
        //不是临时用户cookie或cookie过期，创建user-key的cookie
        if (!userInfoDTO.getTempUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoDTO.getUserKey());
            cookie.setDomain("project.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }
}
