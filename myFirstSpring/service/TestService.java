package com.myFirstSpring.service;

import org.springframework.stereotype.Service;

@Service
public class TestService
{
	//这里是依赖注入概念，用于分发对象，而不用像传统方式使用时便new一个对象
	public String getMessage(int userId) 
	{
		return "Hello Message:" + String.valueOf(userId);
	}
}
