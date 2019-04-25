package com.myFirstSpring.interceptor;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.myFirstSpring.dao.LoginTicketDAO;
import com.myFirstSpring.dao.UserDAO;
import com.myFirstSpring.model.HostHolder;
import com.myFirstSpring.model.LoginTicket;
import com.myFirstSpring.model.User;

/**
 * 拦截器：用于处理在用户访问时，验证用户的登录/登出状态和权限判断
 * @author SFF
 * @date 2018-7-28
 */
@Component
public class PassportInterceptor implements HandlerInterceptor
{
	@Autowired
	LoginTicketDAO loginTicketDAO;
	
	@Autowired
	UserDAO userDAO;
	
	@Autowired
	HostHolder hostHolder;
	
	/**
	 * 在处理所以 http请求之前，先判断这个用户是谁，并把取出的这个用户放入 hostHolder中
	 */
	@Override
	public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o) throws Exception
	{
		//获取请求中的ticket
		String ticket = null;
		if (httpServletRequest.getCookies() != null)
		{
			for (Cookie cookie : httpServletRequest.getCookies())
			{
				if (cookie.getName().equals("ticket"))
				{
					ticket = cookie.getValue();
					break;
				}
			}
		}
		
		//将取出的ticket与数据库中的ticket比较
		if (ticket != null)
		{
			LoginTicket loginTicket = loginTicketDAO.selectByTicket(ticket);
			if (loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0 )
			{
				return true;
			}
			
			User user = userDAO.selectById(loginTicket.getUserId());
			//把取出的这个用户放入hostHolder中，以便后面的链路都可以使用这个用户
			hostHolder.setUser(user);
		}
		
		return true;
	}
	
	/**
	 * 在所有模板渲染之前，将user放入 modelAndView中，即放入Model的上下文，使得在模板 freemarker中可以直接使用这些变量
	 */
	@Override
	public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, ModelAndView modelAndView) throws Exception
	{
		if (modelAndView != null && hostHolder.getUser() != null)
		{
			modelAndView.addObject("user", hostHolder.getUser());
			//System.out.println(hostHolder.getUser());
		}
	}
	
	/**
	 * 访问结束时，将 ThreadLocal中 user清除掉
	 */
	@Override
	public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			Object o, Exception e) throws Exception
	{
		hostHolder.clear();
	}

}
