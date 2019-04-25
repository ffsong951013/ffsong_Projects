package com.myFirstSpring.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.myFirstSpring.async.EventModel;
import com.myFirstSpring.async.EventProducer;
import com.myFirstSpring.async.EventType;
import com.myFirstSpring.async.handler.likeHandler;
import com.myFirstSpring.model.Comment;
import com.myFirstSpring.model.EntityType;
import com.myFirstSpring.model.HostHolder;
import com.myFirstSpring.service.CommentService;
import com.myFirstSpring.service.likeService;
import com.myFirstSpring.util.myUtil;

@Controller
public class LikeController
{
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	likeService likeService;
	
	@Autowired
	EventProducer eventProducer;
	
	@Autowired
	likeHandler likeHandler;
	
	@Autowired
	CommentService commentService;
	
	
	@RequestMapping(path = {"/like"}, method = {RequestMethod.POST})
	@ResponseBody
	public String like(@RequestParam("commentId") int commentId)
	{
		if (hostHolder == null)
		{
			return myUtil.getJSONString(999);
		}
		
		Comment comment = commentService.getCommentById(commentId);
		eventProducer.fireEvent(new EventModel(EventType.LIKE)
				.setActorId(hostHolder.getUser().getId())
				.setEntityId(commentId)
				.setEntityType(EntityType.ENTITY_COMMENT)
				.setEntityOwnerId(comment.getUserId())
				.setExt("questionId", String.valueOf(comment.getEntityId())));
		
		long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		return myUtil.getJSONString(0, String.valueOf(likeCount));
	}
	
	@RequestMapping(path = {"/dislike"}, method = {RequestMethod.POST})
	@ResponseBody
	public String dislike(@RequestParam("commentId") int commentId)
	{
		if (hostHolder == null)
		{
			return myUtil.getJSONString(999);
		}
		
		long likeCount = likeService.disLike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
		return myUtil.getJSONString(0, String.valueOf(likeCount));
	}
}
