package com.myFirstSpring.controller;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.myFirstSpring.model.Comment;
import com.myFirstSpring.model.EntityType;
import com.myFirstSpring.model.HostHolder;
import com.myFirstSpring.service.CommentService;
import com.myFirstSpring.service.QuestionService;
import com.myFirstSpring.util.myUtil;

@Controller
public class CommentController
{
	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	QuestionService questionService;
	
	@RequestMapping(path={"/addComment"}, method={RequestMethod.POST})
	public String addComment(@RequestParam("questionId") int questionId,
							@RequestParam("content") String content)
	{
		try
		{
			Comment comment = new Comment();
			if (hostHolder.getUser() != null)
			{
				comment.setUserId(hostHolder.getUser().getId());
			} else {
				comment.setUserId(myUtil.ANONYMOUS_USERID);
//			return "redirect:/reglogin";
			}
			comment.setContent(content);
			comment.setCreatedDate(new Date());
			comment.setEntityId(questionId);
			comment.setEntityType(EntityType.ENTITY_QUESTION);
			commentService.addComment(comment);
			
			// 更新问题的评论数量
			int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
			questionService.updateCommentCount(comment.getEntityId(), count);
		} catch (Exception e)
		{
			logger.error("增加评论失败" + e.getMessage());
		}
		
		return "redirect:/question/" + questionId;
	}
}
