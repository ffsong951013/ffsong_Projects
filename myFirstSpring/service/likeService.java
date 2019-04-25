package com.myFirstSpring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.myFirstSpring.util.JedisAdapter;
import com.myFirstSpring.util.RedisKeyUtil;

@Service
public class likeService
{
	@Autowired
	JedisAdapter jedisAdapter;
	
	public long getLikeCount(int entityType, int entityId)
	{
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
		return jedisAdapter.scard(likeKey);								// 用于打开页面时获取喜欢的人数
	}
	
	public int getLikeStatus(int userId, int entityType, int entityId)
	{
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
		if (jedisAdapter.sismember(likeKey, String.valueOf(userId)))
		{
			return 1;
		}
		String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
		return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
	}
	
	public long like(int userId, int entityType, int entityId)
	{
		String likeKey = RedisKeyUtil.getLikeKey(entityType, entityId);
		jedisAdapter.sadd(likeKey, String.valueOf(userId));				// 将一个用户添加到likeKey，表示一个用户点赞
		
		String disLikeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
		jedisAdapter.srem(disLikeKey, String.valueOf(userId));			// 同时将用户从点踩disLikeKey中解放出来，因为不可能同时点赞又点踩
		
		return jedisAdapter.scard(likeKey);
	}
	
	public long disLike(int userId, int entityType, int entityId)
	{
		String disLikeKey = RedisKeyUtil.getLikeKey(entityType, entityId);// 与likeKey相反
		jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
		
		String likeKey = RedisKeyUtil.getDisLikeKey(entityType, entityId);
		jedisAdapter.srem(likeKey, String.valueOf(userId)); 
		
		return jedisAdapter.scard(likeKey);
	}
}
