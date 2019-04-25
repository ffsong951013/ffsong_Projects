package com.myFirstSpring.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import com.myFirstSpring.dao.CommentDAO;
import com.myFirstSpring.model.Comment;

@Service
public class CommentService
{
	@Autowired
	CommentDAO commentDAO;
	
	@Autowired
	SensitiveService sensitiveService;
	
	// 查找某个实体下的全部评论
	public List<Comment> getCommentsByEntity(int entityId, int entityType)
	{
		return commentDAO.selectCommentByEntity(entityId, entityType);
	}
	
	// 添加评论
	public int addComment(Comment comment)
	{
		comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
		comment.setContent(sensitiveService.filter(comment.getContent()));
		return commentDAO.addComment(comment) > 0 ? comment.getId() : 0;
	}
	
	// 统计某个实体的评论总数
	public int getCommentCount(int entityId, int entityType)
	{
		return commentDAO.getCommentCount(entityId, entityType);
	}
	
	// 用户的评论数
	public int getUserCommentCount(int userId)
	{
		return commentDAO.getUserCommentCount(userId);
	}
	
	// 更新评论状态，使该评论失效，注意：并不删除它
	public boolean updateStatus(int commentId)
	{
		return commentDAO.updateStatus(1, commentId) > 0;
	}
	
	// 查找一个评论
	public Comment getCommentById(int id)
	{
		return commentDAO.getCommentById(id);
	}
}
