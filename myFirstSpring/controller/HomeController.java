package com.myFirstSpring.controller;

import java.util.ArrayList;
import java.util.List;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.myFirstSpring.model.EntityType;
import com.myFirstSpring.model.HostHolder;
import com.myFirstSpring.model.Question;
import com.myFirstSpring.model.User;
import com.myFirstSpring.model.ViewObject;
import com.myFirstSpring.service.CommentService;
import com.myFirstSpring.service.FollowService;
import com.myFirstSpring.service.QuestionService;
import com.myFirstSpring.service.UserService;

@Controller
public class HomeController
{
//	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	
	@Autowired
	UserService userService;
	
	@Autowired
	QuestionService questionService;
	
	@Autowired
	FollowService followService;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	HostHolder hostHolder;
	
	@RequestMapping(path={"/user/{userId}"}, method={RequestMethod.GET})
	public String userIndex(Model model, @PathVariable("userId") int userId)
	{
		model.addAttribute("vos", getQuestions(userId, 0, 10));
		
		User user = userService.getUser(userId);
		ViewObject vo = new ViewObject();
		vo.set("user", user);
		vo.set("commentCount", commentService.getUserCommentCount(userId));
		vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
		vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, userId));
		if (hostHolder.getUser() != null)
		{
			vo.set("followed", followService.isFollower(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId));
		} else {
			vo.set("followed", false);
		}
		model.addAttribute("profileUser", vo);
		return "profile";
	}
	
	@RequestMapping(path={"/","/index"}, method={RequestMethod.GET, RequestMethod.POST})	// "/"或"/index"都可以访问
	public String index(Model model,
						@RequestParam(value = "pop", defaultValue = "0") int pop)
	{
		model.addAttribute("vos", getQuestions(0, 0, 10));	//此处必须写 vos，用vos接收getQuestions返回的 vos
		return "index";
	}
	
	private List<ViewObject> getQuestions(int userId, int offset, int limit)
	{
		List<Question> questionList = questionService.getLastestQuestions(userId, offset, limit);
		List<ViewObject> vos = new ArrayList<ViewObject>();
		for (Question question : questionList)
		{
			ViewObject vo = new ViewObject();
			vo.set("question", question);
			vo.set("followCount", followService.getFollowerCount(EntityType.ENTITY_QUESTION, question.getId()));
			vo.set("user", userService.getUser(question.getId()));
			vos.add(vo);
		}
		return vos;
	}
}
