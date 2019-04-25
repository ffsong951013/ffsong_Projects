package com.myFirstSpring.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.myFirstSpring.model.HostHolder;

/**
 * 拦截器：未登录跳转
 * @author SFF
 * @date 2018-7-28
 */
@Component
public class LoginRequiredInterceptor implements HandlerInterceptor
{
	@Autowired
	HostHolder hostHolder;
	
	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o) throws Exception
	{
		if (hostHolder.getUser() == null)
		{
			//如果用户未登录，就跳转到登录/注册页面，同时将当前访问页面的url作为参数加上
			httpServletResponse.sendRedirect("/reglogin?next=" + httpServletRequest.getRequestURI());
		}
		return true;
	}
	
	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, ModelAndView modelAndView) throws Exception
	{
		
	}
	
	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception
	{
		
	}

}
