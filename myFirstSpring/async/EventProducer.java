package com.myFirstSpring.async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.myFirstSpring.util.JedisAdapter;
import com.myFirstSpring.util.RedisKeyUtil;

@Service
public class EventProducer
{
	@Autowired
	JedisAdapter jedisAdapter;
	
	/**
	 * 发送事件，就是把一批事件推到队列中
	 * @param eventModel
	 * @return
	 */
	public boolean fireEvent(EventModel eventModel)
	{
		try
		{
			String json = JSONObject.toJSONString(eventModel);
			String key = RedisKeyUtil.getEventQueueKey();
			
			jedisAdapter.lpush(key, json);
			
			System.out.println(key + ":" + json);
			return true;
		} catch (Exception e)
		{
			return false;
		}
	}
}
