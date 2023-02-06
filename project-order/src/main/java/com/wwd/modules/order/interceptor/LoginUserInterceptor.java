package com.wwd.modules.order.interceptor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wwd.common.constant.AuthServerConstant;
import com.wwd.modules.order.vo.MemberDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberDTO> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
        String s = JSON.toJSONString(attribute);
        MemberDTO memberDTO = JSON.parseObject(s, MemberDTO.class);
        if (memberDTO != null){
            threadLocal.set(memberDTO);
            return true;
        } else {
            request.getSession().setAttribute("msg", "请先登录");
            response.sendRedirect("http://auth.project.com/login.html");
            return false;
        }
    }
}
