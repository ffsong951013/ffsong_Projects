package com.myFirstSpring.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.myFirstSpring.dao.LoginTicketDAO;
import com.myFirstSpring.dao.UserDAO;
import com.myFirstSpring.model.LoginTicket;
import com.myFirstSpring.model.User;
import com.myFirstSpring.util.myUtil;

@Service
public class UserService
{
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private LoginTicketDAO loginTicketDAO;
	
	/**
	 * 用户注册
	 * @param username
	 * @param password
	 * @return map
	 */
	public Map<String, String> register(String username, String password)
	{
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isEmpty(username))
		{
			map.put("msg", "用户名不能为空！");
			return map;
		}
		if (StringUtils.isEmpty(password))
		{
			map.put("msg", "密码不能为空！");
			return map;
		}
		User user = userDAO.selectByName(username);
		if (user != null)
		{
			map.put("msg", "该用户已经存在！");
			return map;
		}
		
		user = new User();
		user.setName(username);
		user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
		user.setSalt(UUID.randomUUID().toString().substring(0, 5));
		user.setPassword(myUtil.MD5(password + user.getSalt()));
		userDAO.addUser(user);
		
		//注册成功后，下发 ticket表示注册成功并自动登录
		String ticket = addLoginTicket(user.getId());
		map.put("ticket", ticket);
		
		return map;
	}
	
	/**
	 * 用户登录
	 * @param username
	 * @param password
	 * @return map
	 */
	public Map<String, String> login(String username, String password)
	{
		Map<String, String> map = new HashMap<String, String>();
		if (StringUtils.isEmpty(username))
		{
			map.put("msg", "用户名不能为空！");
			return map;
		}
		if (StringUtils.isEmpty(password))
		{
			map.put("msg", "密码不能为空！");
			return map;
		}
		User user = userDAO.selectByName(username);
		if (user == null)
		{
			map.put("msg", "该用户不存在！");
			return map;
		}
		if (!myUtil.MD5(password + user.getSalt()).equals(user.getPassword()))
		{
			map.put("msg", "密码错误！");
			return map;
		}
		
		//登录成功后，给用户下发 ticket
		String ticket = addLoginTicket(user.getId());
		map.put("ticket", ticket);
		
		return map;
	}
	
	/**
	 * 登录时给每个用户下发 ticket，用于标识用户的登录/登出状态。
	 * ticket的有效期保证在有效期内，下次访问该网页还是处于登录的状态。
	 * @param userId
	 * @return loginTicket
	 */
	public String addLoginTicket(int userId)
	{
		LoginTicket loginTicket = new LoginTicket();
		loginTicket.setUserId(userId);
		loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-", ""));
		Date date = new Date();
		date.setTime(3600*24*100 + date.getTime());
		loginTicket.setExpired(date);
		loginTicket.setStatus(0);
		loginTicketDAO.addTicket(loginTicket);
		return loginTicket.getTicket();
	}
	
	/**
	 * 用户登出，把 ticket对应的用户的 status设置为1
	 * @param ticket
	 */
	public void logout(String ticket)
	{
		loginTicketDAO.updateStatus(1, ticket);
	}
	
	public User getUser(int id)
	{
		return userDAO.selectById(id);
	}
	
	public User selectByName(String name)
	{
		return userDAO.selectByName(name);
	}
}
