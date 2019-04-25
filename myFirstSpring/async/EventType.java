package com.myFirstSpring.async;

/**
 * 事件的类型
 * @author SFF
 * @date 2018-8-28
 */
public enum EventType
{
	LIKE(0),
	COMMENT(1),
	LOGIN(2),
	MAIL(3),
	FOLLOW(4),
	UNFOLLOW(5);
	
	private int value;
	EventType(int value) 
	{
		this.value = value;
	}
	public int getValue() 
	{
		return value;
	}
}
