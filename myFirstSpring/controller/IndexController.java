package com.myFirstSpring.controller;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.myFirstSpring.model.User;
import com.myFirstSpring.service.TestService;

@Controller
public class IndexController
{
	// slf4j:Simple Logging Facade for Java 简单门面日志
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
	
	@Autowired				//不需要再作初始化（传统方式：TestService testService = new TestService();）
	TestService testService;
	
	@RequestMapping(path={"/","/index"})	//"/"或"/index"
	@ResponseBody
	public String index(HttpSession session)
	{
		logger.info("Visit home");
		
		return testService.getMessage(1) + "Hello My FirstSpring!" + session.getAttribute("msg");
	}
	
	@RequestMapping(path={"/profile/{groupId}/{userId}"})
	@ResponseBody
	public String profile(@PathVariable("userId") int userId,
						@PathVariable("groupId") String groupId,
						@RequestParam(value = "type", defaultValue = "1") int type,
						@RequestParam(value = "key", defaultValue = "zz", required = false) String key)
	{
		return String.format("Profile Page of %s / %d, t:%d k:%s", groupId, userId, type, key);
	}
	
	@RequestMapping(path={"/ftl"}, method = {RequestMethod.GET})
	public String template(Model model){
		model.addAttribute("value1", "v1");
		model.addAttribute("value2", 2);
		
		List<String> colors = Arrays.asList(new String[]{"RED", "GREEN", "BLUE"});
		model.addAttribute("colors", colors);
		
		Map<String, String> map = new HashMap<>();
		for (int i = 0; i < 4; i++)
		{
			map.put(String.valueOf(i), String.valueOf(i * i));
		}
		model.addAttribute("map", map);
		
		model.addAttribute("user", new User("LEE"));
		return "home";
	}
	
	@RequestMapping(path={"/request"}, method = {RequestMethod.GET})
	@ResponseBody
	public String request(Model model,HttpServletResponse response,
							HttpServletRequest request,
							HttpSession session,
							@CookieValue("JSESSIONID") String sessionId){
		StringBuilder sb = new StringBuilder();
		//用注解的方式显示cookie
		sb.append("COOKIEVALUE:" + sessionId + "<br/>");
		//显示header的信息
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements())
		{
			String name = headerNames.nextElement();
			sb.append(name + " : " + request.getHeader(name) + "<br/>");
		}
		//也可以单独显示cookie的信息
		if (request.getCookies() != null)
		{
			for (Cookie cookie : request.getCookies())
			{
				sb.append("Cookie:" + cookie.getName() + ",Value:" + cookie.getValue());
			}
		}
		sb.append("<br/>" + request.getMethod() + "<br/>");
		sb.append(request.getQueryString() + "<br/>");
		sb.append(request.getPathInfo() + "<br/>");
		sb.append(request.getRequestURI() + "<br/>");
		
		response.addHeader("testHeader", "hello");
		response.addCookie(new Cookie("testCookie", "testCookie"));
		
		return sb.toString();
	}
	
	@RequestMapping(path={"/redirect/{code}"}, method = {RequestMethod.GET})
	public RedirectView redirect(@PathVariable("code") int code,	//返回一个模板
							HttpSession session) {
		session.setAttribute("msg", "jump from redirect");
		RedirectView rv = new RedirectView("/", true);	//定义一个模板并指定访问路径
		if (code == 301)	//301永久性跳转（更安全高效）、302临时性跳转
		{
			rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
		}
		return rv;
	}
	
	@RequestMapping(path={"/admin"}, method = {RequestMethod.GET})
	@ResponseBody
	public String admin(@RequestParam("key") String key) {
		if ("123".equals(key))
		{
			return "Hello admin";
		}
		throw new IllegalArgumentException("参数不对！");
	}
	
	@ExceptionHandler()		//统一异常处理
	@ResponseBody
	public String error(Exception e) {
		return "error" + e.getMessage();	//抛出异常时，由统一的异常捕获并显示信息
	}
}
