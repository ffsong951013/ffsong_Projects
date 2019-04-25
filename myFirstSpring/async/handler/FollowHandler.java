package com.myFirstSpring.async.handler;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.myFirstSpring.async.EventHandler;
import com.myFirstSpring.async.EventModel;
import com.myFirstSpring.async.EventType;
import com.myFirstSpring.model.Message;
import com.myFirstSpring.model.User;
import com.myFirstSpring.service.MessageService;
import com.myFirstSpring.service.UserService;
import com.myFirstSpring.util.myUtil;

@Component
public class FollowHandler implements EventHandler
{
	@Autowired
	MessageService messageService;
	
	@Autowired
	UserService userService;

	@Override
	public void doHandler(EventModel eventModel)
	{
		Message message = new Message();
		message.setFromId(myUtil.SYSTEM_USERID);
		message.setToId(eventModel.getEntityOwnerId());
		message.setCreatedDate(new Date());
		User user = userService.getUser(eventModel.getActorId());
		message.setContent("用户" + user.getName() + "赞了你的评论，http://127.0.0.1:8080/question/" + eventModel.getExt("questionId"));

		messageService.addMessage(message);
	}

	@Override
	public List<EventType> getSupportEventTypes()
	{
		return Arrays.asList(EventType.LIKE);
	}
	
}
