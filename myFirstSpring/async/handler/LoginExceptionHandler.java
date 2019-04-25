package com.myFirstSpring.async.handler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.myFirstSpring.async.EventHandler;
import com.myFirstSpring.async.EventModel;
import com.myFirstSpring.async.EventType;
import com.myFirstSpring.util.MailSender;

@Component
public class LoginExceptionHandler implements EventHandler
{
//	@Autowired
	MailSender mailSender = new MailSender();
	
	@Override
	public void doHandler(EventModel eventModel)
	{
		// xxx判断发现这个用户登录异常
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", eventModel.getExt("username"));
		mailSender.sendWithHTMLTemplate(eventModel.getExt("email"), "登录", "mails/login_exception.html", map);
	}

	@Override
	public List<EventType> getSupportEventTypes()
	{
		return Arrays.asList(EventType.LOGIN);
	}

}
