package com.myFirstSpring.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.myFirstSpring.model.Comment;

@Repository
@Mapper
public interface CommentDAO
{
	String TABLE_NAME = " comment ";
	String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type, status ";
	String SELECT_FIELDS = " id, " + INSERT_FIELDS;
	
	//简单的sql语句可以直接采用注解的方式
	@Insert({ "insert into ", TABLE_NAME, "(", INSERT_FIELDS,
			") values(#{userId},#{content},#{createdDate},#{entityId},#{entityType},#{status})" })
	int addComment(Comment comment);
	
	//相对复杂的可以采用xml配置的方式
	@Select({" select ", SELECT_FIELDS, " from ", TABLE_NAME,
			" where entity_id=#{entityId} and entity_type=#{entityType} order by created_date desc"})
	List<Comment> selectCommentByEntity(@Param("entityId") int entityId, @Param("entityType") int entityType);
	
	@Select({" select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
	Comment getCommentById(int id);
	
	@Select({" select count(id) from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType}"})
	int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);
	
	@Select({" select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
	int getUserCommentCount(int userId);
	
	@Update({" update ", TABLE_NAME, " set status=#{status} where id=#{id}"})
	int updateStatus(@Param("status") int status, @Param("id") int id);
}
