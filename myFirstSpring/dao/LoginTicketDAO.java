package com.myFirstSpring.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.myFirstSpring.model.LoginTicket;

@Mapper
public interface LoginTicketDAO
{
	String TABLE_NAME = " login_ticket ";
	String INSERT_FIELDS = " user_id, ticket, expired, status ";
	String SELECT_FIELDS = " id, " + INSERT_FIELDS;
	
	@Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
			") values (#{userId},#{ticket},#{expired},#{status})"})
	void addTicket(LoginTicket ticket);
	
	@Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where ticket=#{ticket}"})
	LoginTicket selectByTicket(String ticket); 
	
	@Update({"update ", TABLE_NAME, " set status=#{status} where ticket=#{ticket}"})
	void updateStatus(@Param("status") int status, @Param("ticket") String ticket);
}
