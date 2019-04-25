package com.myFirstSpring.model;

import org.springframework.stereotype.Component;

/**
 * 把user取出放在上下文中，保证后面的链路也可以访问这个用户
 * @author SFF
 * @date 2018-7-28
 */
@Component
public class HostHolder
{
	// 为每一条线程都分配了对象，每一个线程都拥有这个对象的一个副本，且他们的存储的地址是不一样的
	private static ThreadLocal<User> users = new ThreadLocal<User>();	// users只能在方法中使用
	
	public User getUser() {
		return users.get();
	}
	
	public void setUser(User user) {
		users.set(user);
	}
	
	public void clear() {
		users.remove();
	}
}
