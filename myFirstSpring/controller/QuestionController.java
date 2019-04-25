package com.myFirstSpring.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.myFirstSpring.model.Comment;
import com.myFirstSpring.model.EntityType;
import com.myFirstSpring.model.HostHolder;
import com.myFirstSpring.model.Question;
import com.myFirstSpring.model.ViewObject;
import com.myFirstSpring.service.CommentService;
import com.myFirstSpring.service.QuestionService;
import com.myFirstSpring.service.UserService;
import com.myFirstSpring.service.likeService;
import com.myFirstSpring.util.myUtil;

/**
 * @author SFF
 * @date 2018-8-1
 */
@Controller
public class QuestionController
{
	@Autowired
	HostHolder hostHolder;
	
	@Autowired
	UserService userService;
	
	@Autowired
	QuestionService questionService;
	
	@Autowired
	CommentService commentService;
	
	@Autowired
	likeService likeService;
	
	private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
	
	@RequestMapping(value="/question/add", method={RequestMethod.POST})
	@ResponseBody
	public String addQuestion(@RequestParam("title") String title, @RequestParam("content") String content)
	{
		try
		{
			// 构造一个问题出来
			Question question = new Question();
			question.setTitle(title);
			question.setContent(content);
			question.setCreatedDate(new Date());
			question.setCommentCount(0);
			if (hostHolder.getUser() == null)
			{
				// 如果用户没登录，给一个匿名的用户
				// question.setUserId(myUtil.ANONYMOUS_USERID);
				// 或者
				return myUtil.getJSONString(999);
			} else {
				question.setUserId(hostHolder.getUser().getId());
			}
			if (questionService.addQuestion(question) > 0)
			{
				// 如果成功添加，设置json code为0
				return myUtil.getJSONString(0);
			}
		} catch (Exception e)
		{
			logger.error("增加题目失败" + e.getMessage());
		}
		return myUtil.getJSONString(1, "失败");
	}
	
	@RequestMapping(value = "/question/{qid}")
	public String questionDetail(Model model, @PathVariable("qid") int qid)
	{
		Question question = questionService.getById(qid);
		model.addAttribute("question", question);
		model.addAttribute("user", userService.getUser(question.getId()));
		
		// 获取该qid的全部评论
		List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
		List<ViewObject> vos = new ArrayList<ViewObject>();
		// 遍历commentList并添加到vo中
		for (Comment comment : commentList)
		{
			ViewObject vo = new ViewObject();
			vo.set("comment", comment);
			// 判断是否喜欢
			if (hostHolder.getUser() == null)
			{
				vo.set("liked", 0);		// 未登录表示没有喜欢也没有不喜欢
			} else {
				vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, comment.getId()));
			}
			// 获取喜欢的数量
			vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
			vo.set("user", userService.getUser(comment.getUserId()));
			vos.add(vo);
		}
		model.addAttribute("vos", vos);
		
		return "detail";
	}
}
