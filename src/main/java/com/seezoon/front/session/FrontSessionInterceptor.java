package com.seezoon.front.session;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.seezoon.admin.modules.sys.utils.HttpStatus;

public class FrontSessionInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		FrontUser frontUser =FrontSubject.getUserSession(session);
		if (null == frontUser) {
			response.setStatus(HttpStatus.NEED_LOGIN.getValue());
		    response.setCharacterEncoding("UTF-8");  
		    response.setContentType("application/json; charset=utf-8");  
			
			JSONObject resJson = new JSONObject();
			resJson.put("responeCode", "-2");
			resJson.put("responeMsg", "用户未登录");
		    PrintWriter out = null;  
		    try {  
		        out = response.getWriter();  
		        out.append(resJson.toString());
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    } finally {  
		        if (out != null) {  
		            out.close();
		        }  
		    }
			return false;
		} else {
			FrontSubject.put(frontUser);
			return true;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
