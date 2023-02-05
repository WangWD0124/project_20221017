package com.wwd.modules.order.interceptor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wwd.common.constant.AuthServerConstant;
import com.wwd.modules.order.vo.MemberDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public final static ThreadLocal<MemberDTO> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        MemberDTO attribute = JSON.parseObject(request.getSession().getAttribute(AuthServerConstant.LOGIN_USER).toString(), new TypeReference<MemberDTO>(){});
        if (attribute != null){
            threadLocal.set(attribute);
            return true;
        } else {
            request.setAttribute("msg", "请先登录");
            response.sendRedirect("http://auth.project.com/login.html");
            return false;
        }
    }
}
