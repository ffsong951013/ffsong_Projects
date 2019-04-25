package com.myFirstSpring.async;

import java.util.List;

/**
 * 具体的handler 和 EventConsumer 之间的接口
 * @author SFF
 * @date 2018-8-28
 */
public interface EventHandler
{
	// 处理具体事件的方法
	void doHandler(EventModel eventModel);
	
	// 注册自己，告诉 EventConsumer 某个类型的 event 该由哪个 EventHandler 去处理
	List<EventType> getSupportEventTypes();
}
