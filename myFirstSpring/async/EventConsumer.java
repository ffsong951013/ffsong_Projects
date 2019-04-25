package com.myFirstSpring.async;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.myFirstSpring.util.JedisAdapter;
import com.myFirstSpring.util.RedisKeyUtil;

@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware
{
	@Autowired
	JedisAdapter jedisAdapter;
	
	private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
	
	// config 用于关联事件类型 和 具体的事件处理函数
	private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();
	
	// 获取上下文，以便获得所有的具体EventHandler
	private ApplicationContext applicationContext;
	
	@Override
	public void afterPropertiesSet() throws Exception
	{
		// 用于存放所有的 EventHandler（getBeansOfType()返回 map<类名，类的实例>）
		Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
		if (beans != null)
		{
			// 遍历获取beans键值对
			for (Map.Entry<String, EventHandler> entry : beans.entrySet())
			{
				// 建立列表用于存放上下文所有 EventHandler 的类型
				List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
				for (EventType type : eventTypes)
				{
					if (!config.containsKey(type))
					{
						// 若 config 中没有相应的类型，则将它添加到 config 中去
						config.put(type, new ArrayList<EventHandler>());
					}
					// 根据key（类型）查找到值（EventHandler）并添加到其中list，这样 config 中类型和 EventHandler 就匹配上了
					config.get(type).add(entry.getValue());
				}
			}
		}
		
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (true)
				{
					String key = RedisKeyUtil.getEventQueueKey();
					List<String> events = jedisAdapter.brpop(0, key);
					
					for (String message : events)
					{
						// brpop()会把本身的 键 取出来，作为第一个元素
						if (message.equals(key))
						{
							continue;
						}
						
						EventModel eventModel = JSON.parseObject(message, EventModel.class);
						if (!config.containsKey(eventModel.getType()))
						{
							logger.error("不能识别的事件");
						}
						
						for (EventHandler handler : config.get(eventModel.getType()))
						{
							handler.doHandler(eventModel);
						}
					}
				}
			}
		});
		thread.start();
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		this.applicationContext = applicationContext;
	}
}
