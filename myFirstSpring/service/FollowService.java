package com.myFirstSpring.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import com.myFirstSpring.util.JedisAdapter;
import com.myFirstSpring.util.RedisKeyUtil;

@Service
public class FollowService
{
	@Autowired
	JedisAdapter jedisAdapter;
	
	/**
	 * 关注某个实体
	 * @param userId
	 * @param entityType
	 * @param entityId
	 * @return 是否关注成功
	 */
	public boolean follow(int userId, int entityType, int entityId)
	{
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		String followeeKey = RedisKeyUtil.getFollowerKey(userId, entityType);
		Date date = new Date();
		
		Jedis jedis = JedisAdapter.getJedis();
		Transaction tx = jedisAdapter.multi(jedis);
		tx.zadd(followerKey, date.getTime(), String.valueOf(userId));
		tx.zadd(followeeKey, date.getTime(), String.valueOf(entityId));		// 返回被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
		List<Object> ret = jedisAdapter.exec(tx, jedis);					// list接收事物块内所有命令的返回值
		return ret.size() == 2 && (long)ret.get(0) > 0 && (long)ret.get(1) > 0;
	}
	
	/**
	 * 取消关注
	 * @param userId
	 * @param entityType
	 * @param entityId
	 * @return 取消关注是否成功
	 */
	public boolean unfollow(int userId, int entityType, int entityId)
	{
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		String followeeKey = RedisKeyUtil.getFollowerKey(userId, entityType);
		
		Jedis jedis = JedisAdapter.getJedis();
		Transaction tx = jedisAdapter.multi(jedis);
		tx.zrem(followerKey, String.valueOf(userId));
		tx.zrem(followeeKey, String.valueOf(entityId));		// 返回被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
		List<Object> ret = jedisAdapter.exec(tx, jedis);					// list接收事物块内所有命令的返回值
		return ret.size() == 2 && (long)ret.get(0) > 0 && (long)ret.get(1) > 0;
	}
	
	/**
	 * help类，将 set 转化为 list
	 * @param idset
	 * @return
	 */
	private List<Integer> getIdsFromSet(Set<String> idset)
	{
		List<Integer> ids = new ArrayList<>();
		for (String str : idset)
		{
			ids.add(Integer.parseInt(str));
		}
		return ids;
	}
	
	/**
	 * 获得所有的粉丝/关注者（表明我有多少粉丝）
	 * @param entityType
	 * @param entityId
	 * @param count
	 * @return 关注的 list
	 */
	public List<Integer> getFollowers(int entityType, int entityId, int count)
	{
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return getIdsFromSet(jedisAdapter.zrevrange(followerKey, 0, count));
	}
	
	/**
	 * 分页获得所有的粉丝/关注者（表明我有多少粉丝）
	 * @param entityType
	 * @param entityId
	 * @param offset
	 * @param count
	 * @return 分页返回关注的 list
	 */
	public List<Integer> getFollowers(int entityType, int entityId, int offset, int count)
	{
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return getIdsFromSet(jedisAdapter.zrevrange(followerKey, offset, count));
	}
	
	/**
	 * 获得所有的被关注者（我关注了多少人）
	 * @param entityType
	 * @param entityId
	 * @param count
	 * @return 分页返回我关注的人 list
	 */
	public List<Integer> getFollowees(int entityType, int entityId, int count)
	{
		String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
		return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, 0, count));
	}
	
	/**
	 * 分页获得所有被关注者（我关注了多少人）
	 * @param entityType
	 * @param entityId
	 * @param offset
	 * @param count
	 * @return 返回我关注的人的 list
	 */
	public List<Integer> getFollowees(int entityType, int entityId, int offset, int count)
	{
		String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
		return getIdsFromSet(jedisAdapter.zrevrange(followeeKey, offset, count));
	}
	
	/**
	 * 获取所有的粉丝数量
	 * @param entityType
	 * @param entityId
	 * @return 返回用户的粉丝数量
	 */
	public long getFollowerCount(int entityType, int entityId)
	{
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return jedisAdapter.zcard(followerKey);
	}
	
	/**
	 * 获取所有的被关注者数量
	 * @param entityType
	 * @param entityId
	 * @return 返回用户关注的数量
	 */
	public long getFolloweeCount(int entityType, int entityId)
	{
		String followeeKey = RedisKeyUtil.getFolloweeKey(entityType, entityId);
		return jedisAdapter.zcard(followeeKey);
	}
	
	/**
	 * 判断某个用户是否是粉丝
	 * @param userId
	 * @param entityType
	 * @param entityId
	 * @return 返回某个用户是否是粉丝
	 */
	public boolean isFollower(int userId, int entityType, int entityId)
	{
		String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
		return jedisAdapter.zscore(followerKey, String.valueOf(userId)) != null;
	}
}
