package com.myFirstSpring.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.myFirstSpring.model.Question;

@Repository
@Mapper
public interface QuestionDAO
{
	String TABLE_NAME = " question ";
	String INSERT_FIELDS = " title, content, user_id, created_date, comment_count ";
	String SELECT_FIELDS = " id, " + INSERT_FIELDS;
	
	//简单的sql语句可以直接采用注解的方式
	@Insert({ "insert into ", TABLE_NAME, "(", INSERT_FIELDS,
			") values(#{title},#{content},#{userId},#{createdDate},#{commentCount})" })
	int addQuestion(Question question);
	
	@Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
	Question getById(int id);
	
	//相对复杂的可以采用xml配置的方式
	List<Question> selectLastestQuestions(@Param("userId") int userId,
										  @Param("offset") int offset,
										  @Param("limit") int limit);
	
	@Update({"update ", TABLE_NAME, " set comment_count=#{count} where id=#{id}"})
	void updateCommentCount(@Param("id") int id, @Param("count") int count);
}
