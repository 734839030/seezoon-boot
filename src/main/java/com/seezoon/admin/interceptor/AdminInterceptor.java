package com.seezoon.admin.interceptor;

import com.seezoon.admin.modules.sys.security.SecurityUtils;
import com.seezoon.admin.modules.sys.security.User;
import com.seezoon.boot.context.dto.AdminUser;
import com.seezoon.boot.context.utils.AdminThreadContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 后端通用拦截器
 *
 * @author hdf 2017年9月24日
 */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        User user = SecurityUtils.getUser();
        if (null != user) {
            AdminThreadContext.putUser(new AdminUser(user.getUserId(), user.getDsf()));
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        AdminThreadContext.removeUser();
    }
}
