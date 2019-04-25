package com.myFirstSpring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.myFirstSpring.dao.QuestionDAO;
import com.myFirstSpring.model.Question;

@Service
public class QuestionService
{
	@Autowired
	QuestionDAO questionDAO;
	
	@Autowired
	SensitiveService sensitiveService;
	
	public Question getById(int id)
	{
		return questionDAO.getById(id);
	}
	
	public int addQuestion(Question question)
	{
		//HTML过滤(它会对一些标签做转译处理，同时存到数据库)
		question.setContent(HtmlUtils.htmlEscape(question.getContent()));
		question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
		//敏感词过滤
		question.setContent(sensitiveService.filter(question.getContent()));
		question.setTitle(sensitiveService.filter(question.getTitle()));
		
		return questionDAO.addQuestion(question) > 0 ? question.getId() : 0;
	}
	
	public List<Question> getLastestQuestions(int userId, int offset, int limit)
	{
		return questionDAO.selectLastestQuestions(userId, offset, limit);
	}
	
	public void updateCommentCount(int id, int count)
	{
		questionDAO.updateCommentCount(id, count);
	}
}
