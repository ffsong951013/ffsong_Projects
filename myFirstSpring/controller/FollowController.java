package com.myFirstSpring.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.myFirstSpring.async.EventModel;
import com.myFirstSpring.async.EventProducer;
import com.myFirstSpring.async.EventType;
import com.myFirstSpring.model.EntityType;
import com.myFirstSpring.model.HostHolder;
import com.myFirstSpring.model.Question;
import com.myFirstSpring.model.User;
import com.myFirstSpring.model.ViewObject;
import com.myFirstSpring.service.CommentService;
import com.myFirstSpring.service.FollowService;
import com.myFirstSpring.service.QuestionService;
import com.myFirstSpring.service.UserService;
import com.myFirstSpring.util.myUtil;

public class FollowController
{
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	QuestionService questionService;
	
	@Autowired
	EventProducer eventProducer;

	@Autowired
	FollowService followService;
	
	@Autowired
	UserService userService;
	
	/**
	 * 关注某个用户
	 * @param userId
	 * @return 返回关注人数
	 */
	@RequestMapping(path={"/followUser"}, method={RequestMethod.POST})
	@ResponseBody
	public String followUser(@RequestParam("userId") int userId)
	{
		if (hostHolder.getUser() == null)
		{
			return myUtil.getJSONString(999);
		}
		
		boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
		
		eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
								.setActorId(hostHolder.getUser().getId())
								.setEntityId(userId)
								.setEntityType(EntityType.ENTITY_USER)
								.setEntityOwnerId(userId));
		// 返回关注人数
		return myUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(EntityType.ENTITY_USER, hostHolder.getUser().getId())));
	}
	
	/**
	 * 取消关注某个用户
	 * @param userId
	 * @return 返回关注人数
	 */
	@RequestMapping(path={"/unfollowUser"}, method={RequestMethod.POST})
	@ResponseBody
	public String unFollowUser(@RequestParam("userId") int userId)
	{
		if (hostHolder.getUser() == null)
		{
			return myUtil.getJSONString(999);
		}
		
		boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_USER, userId);
		
		eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
								.setActorId(hostHolder.getUser().getId())
								.setEntityId(userId)
								.setEntityType(EntityType.ENTITY_USER)
								.setEntityOwnerId(userId));
		// 返回关注人数
		return myUtil.getJSONString(ret ? 0 : 1, String.valueOf(followService.getFolloweeCount(EntityType.ENTITY_USER, hostHolder.getUser().getId())));
	}
	
	/**
	 * 关注问题
	 * @param questionId
	 * @return 返回问题的信息
	 */
	@RequestMapping(path={"/followQuestion"}, method={RequestMethod.POST})
	@ResponseBody
	public String followQuestion(@RequestParam("questionId") int questionId)
	{
		if (hostHolder.getUser() == null)
		{
			return myUtil.getJSONString(999);
		}
		
		Question question = questionService.getById(questionId);
		if (question == null)
		{
			return myUtil.getJSONString(1, "问题不存在");
		}
		
		boolean ret = followService.follow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
		
		eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
								.setActorId(hostHolder.getUser().getId())
								.setEntityId(questionId)
								.setEntityType(EntityType.ENTITY_QUESTION)
								.setEntityOwnerId(question.getUserId()));
		
		Map<String, Object> info = new HashMap<String, Object>();
		info.put("headUrl", hostHolder.getUser().getHeadUrl());
		info.put("name", hostHolder.getUser().getName());
		info.put("id", hostHolder.getUser().getId());
		info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
		// 返回问题的信息
		return myUtil.getJSONString(ret ? 0 : 1, info);
	}
	
	/**
	 * 取消关注某个问题
	 * @param questionId
	 * @return 返回问题的信息
	 */
	@RequestMapping(path={"/unfollowQuestion"}, method={RequestMethod.POST})
	@ResponseBody
	public String unfollowQuestion(@RequestParam("questionId") int questionId)
	{
		if (hostHolder.getUser() == null)
		{
			return myUtil.getJSONString(999);
		}
		
		Question question = questionService.getById(questionId);
		if (question == null)
		{
			return myUtil.getJSONString(1, "问题不存在");
		}
		
		boolean ret = followService.unfollow(hostHolder.getUser().getId(), EntityType.ENTITY_QUESTION, questionId);
		
		eventProducer.fireEvent(new EventModel(EventType.FOLLOW)
								.setActorId(hostHolder.getUser().getId())
								.setEntityId(questionId)
								.setEntityType(EntityType.ENTITY_QUESTION)
								.setEntityOwnerId(question.getUserId()));

		Map<String, Object> info = new HashMap<String, Object>();
		info.put("id", hostHolder.getUser().getId());
		info.put("count", followService.getFollowerCount(EntityType.ENTITY_QUESTION, questionId));
		// 返回问题的信息
		return myUtil.getJSONString(ret ? 0 : 1, info);
	}
	
	/**
	 * 关注的所有人
	 * @param model
	 * @param userId
	 * @return 
	 */
	@RequestMapping(path={"/user/{userId}/followers"}, method={RequestMethod.GET})
	public String followers(Model model, @PathVariable("userId") int userId)
	{
		List<Integer> followerIds = followService.getFollowers(EntityType.ENTITY_USER, userId, 0, 10);
		if (hostHolder.getUser() != null)
		{
			model.addAttribute("followers", getUsersInfo(hostHolder.getUser().getId(), followerIds));
		} else {
			model.addAttribute("followers", getUsersInfo(0, followerIds));
		}
		model.addAttribute("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userId));
		model.addAttribute("curUser", userService.getUser(userId));
		return "followers";
	}
	
	/**
	 * 所有的粉丝
	 * @param model
	 * @param userId
	 * @return 
	 */
	@RequestMapping(path={"/user/{userId}/followees"}, method={RequestMethod.GET})
	public String followees(Model model, @PathVariable("userId") int userId)
	{
		List<Integer> followeeIds = followService.getFollowees(EntityType.ENTITY_USER, userId, 0, 10);
		if (hostHolder.getUser() != null)
		{
			model.addAttribute("followees", getUsersInfo(hostHolder.getUser().getId(), followeeIds));
		} else {
			model.addAttribute("followees", getUsersInfo(0, followeeIds));
		}
		model.addAttribute("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, userId));
		model.addAttribute("curUser", userService.getUser(userId));
		return "followees";
	}
	
	/**
	 * 获取用户的各种信息
	 * @param localUserId
	 * @param userIds
	 * @return 用户信息的 vo
	 */
	private List<ViewObject> getUsersInfo(int localUserId, List<Integer> userIds)
	{
		List<ViewObject> userInfos = new ArrayList<ViewObject>();
		for (Integer userid : userIds)
		{
			User user = userService.getUser(userid);
			if (user == null)
			{
				continue;
			}
			ViewObject vo = new ViewObject();
			vo.set("user", user);
			vo.set("commentCount", commentService.getCommentById(userid));
			vo.set("followerCount", followService.getFollowerCount(EntityType.ENTITY_USER, userid));
			vo.set("followeeCount", followService.getFolloweeCount(EntityType.ENTITY_USER, userid));
			if (localUserId != 0)
			{
				vo.set("followed", followService.isFollower(localUserId, EntityType.ENTITY_USER, userid));
			} else
			{
				vo.set("followed", false);
			}
		}
		return userInfos;
	}
} 
