 package com.myFirstSpring.controller;

import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.myFirstSpring.async.EventModel;
import com.myFirstSpring.async.EventProducer;
import com.myFirstSpring.async.EventType;
import com.myFirstSpring.service.UserService;

@Controller
public class LoginController
{
	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
	
	@Autowired
	UserService userService;
	
	@Autowired
	EventProducer eventProducer;
	
	@RequestMapping(path={"/reg/"}, method={RequestMethod.POST})
	public String reg(Model model,
						@RequestParam("username") String username,
						@RequestParam("password") String password,
						@RequestParam(value = "next", required = false) String next,
						HttpServletResponse response)
	{
		try
		{
			Map<String, String> map = userService.register(username, password);
//			System.out.println(map.containsKey("msg"));
			if (map.containsKey("ticket")){
				// 把 ticket 以 cookie 的形式存下来
				Cookie cookie = new Cookie("ticket", map.get("ticket"));
				cookie.setPath("/");	//表示此 cookie在"/"目录下的子目录均可见
				response.addCookie(cookie);
				if (StringUtils.isEmpty(next))
				{
					return "redirect:" + next;
				}
				return "redirect:/";
			} else {
				model.addAttribute("msg", map.get("msg"));
				return "login";
			}
		} catch (Exception e)
		{
			logger.error("注册异常" + e.getMessage());
			return "login";
		}
	}
	
	@RequestMapping(path={"/login/"}, method={RequestMethod.POST})
	public String login(Model model,
						@RequestParam("username") String username,
						@RequestParam("password") String password,
						@RequestParam(value = "next", required = false) String next,
						@RequestParam(value = "rememberme", required = false) String rememberme,
						HttpServletResponse response)
	{
		try
		{
			Map<String, String> map = userService.login(username, password);
			
			if (map.containsKey("ticket")){
				Cookie cookie = new Cookie("ticket", map.get("ticket"));	// 把 ticket 以 cookie 的形式存下来
				cookie.setPath("/");										//表示此 cookie在"/"目录下的子目录均可见
				response.addCookie(cookie);
				
				// 登录事件
				eventProducer.fireEvent(new EventModel(EventType.LOGIN).setExt("username", username)
																  .setExt("email", "sffxfg@qq.com"));
				
//				System.out.println("redirect:" + next);
//				System.out.println(StringUtils.isEmpty(next));
				if (!StringUtils.isEmpty(next))
				{
					return "redirect:" + next;
				}
				return "redirect:/";
			} else {
				model.addAttribute("msg", map.get("msg"));
				return "login";
			}
		} catch (Exception e)
		{
			logger.error("注册异常" + e.getMessage() + e.toString());
			return "login";
		}
	}
	
	/**
	 * 登录注册页面
	 * @param model
	 * @return
	 */
	@RequestMapping(path={"/reglogin"}, method={RequestMethod.GET})
	public String reg(Model model, @RequestParam(value = "next", required = false) String next)
	{
		model.addAttribute("next", next);
		return "login";
	}
	
	/**
	 * 登出页面
	 * @param ticket
	 * @return
	 */
	@RequestMapping(path={"/logout"}, method={RequestMethod.GET})
	public String logout(@CookieValue("ticket") String ticket)
	{
		userService.logout(ticket);
		return "redirect:/";
	}
}
