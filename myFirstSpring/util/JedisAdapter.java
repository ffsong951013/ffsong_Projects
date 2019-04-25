package com.myFirstSpring.util;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Transaction;

@Service
public class JedisAdapter
{
	private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
	
	private static JedisPool pool;
	
	static
	{
		JedisPoolConfig config = new JedisPoolConfig();
		//配置最大jedis实例数
		config.setMaxTotal(1000);
		//配置资源池最大闲置数
		config.setMaxIdle(200);
		//等待可用连接的最大时间
		config.setMaxWaitMillis(10 * 1000);
		//在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的
		config.setTestOnBorrow(true);
		
		pool = new JedisPool("redis://localhost:6379/10");	// 初始化
	}
	
	// 多线程环境同步初始化（保证项目中有且仅有一个连接池）
	public synchronized static Jedis getJedis()
	{
		if (pool != null)
		{
			Jedis jedis = pool.getResource();
			return jedis;
		} else {
			return null;
		}
	}
	
//	public static void main(String[] args)
//	{
//		Jedis jedis = new Jedis("redis://localhost:6379/10");
//		
//		Jedis j = JedisAdapter.getJedis();
//		j.lpush("testDB", "DB10_j");
//		jedis.lpush("testDB", "DB10_jedis");
//		JedisAdapter.lpush("testDB", "DB10");
//		
//		System.out.println(j.lrange("testDB", 0, 100));
//		System.out.println(jedis.lrange("testDB", 0, 100));
//		
//		System.out.println(j.brpop(0, "testDB"));
//		System.out.println(JedisAdapter.brpop(0, "testDB"));
//	}
	
	/**
	 * 添加元素
	 * @param key
	 * @param value
	 * @return
	 */
	public long sadd(String key, String value)
	{
		Jedis jedis = JedisAdapter.getJedis();
		try
		{
			jedis.sadd(key, value);
		} catch (Exception e)
		{
			logger.error("发生异常：" + e.getMessage());
		} finally
		{
			if (jedis != null)
			{
				jedis.close();	// 若jedis不为空，则把它返回给pool
			}
		}
		return 0;
	}
	
	/**
	 * 移除元素
	 * @param key
	 * @param value
	 * @return
	 */
	public long srem(String key, String value)
	{
		Jedis jedis = JedisAdapter.getJedis();
		try
		{
			jedis.srem(key, value);
		} catch (Exception e)
		{
			logger.error("发生异常：" + e.getMessage());
		} finally
		{
			if (jedis != null)
			{
				jedis.close();	// 若jedis不为空，则把它返回给pool
			}
		}
		return 0;
	}
	
	/**
	 * 一共有多少个元素
	 * @param key
	 * @return
	 */
	public long scard(String key)
	{
		Jedis jedis = JedisAdapter.getJedis();
		try
		{
			jedis.scard(key);
		} catch (Exception e)
		{
			logger.error("发生异常：" + e.getMessage());
		} finally
		{
			if (jedis != null)
			{
				jedis.close();	// 若jedis不为空，则把它返回给pool
			}
		}
		return 0;
	}
	
	/**
	 * 判断是不是指定set的元素
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean sismember(String key, String value)
	{
		Jedis jedis = JedisAdapter.getJedis();
		try
		{
			return jedis.sismember(key, value);
		} catch (Exception e)
		{
			logger.error("发生异常：" + e.getMessage());
		} finally
		{
			if (jedis != null)
			{
				jedis.close();	// 若jedis不为空，则把它返回给pool
			}
		}
		return false;
	}
	
	public long lpush(String key, String value)
	{
		Jedis jedis = JedisAdapter.getJedis();
		long len = 0;
		try
		{
//			System.out.println(key + ":" + value);
//			System.out.println(jedis.lpush(key, value));
			
			len = jedis.lpush(key, value);
		} catch (Exception e)
		{
			logger.error("发生异常" + e.getMessage());
		} finally 
		{
			if (jedis != null)
			{
				jedis.close();
			}
		}
		return len;
	}
	
	/**
	 * 移出并获取列表的最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
	 * @param timeout
	 * @param key
	 * @return
	 */
	public List<String> brpop(int timeout, String key)
	{
		Jedis jedis = JedisAdapter.getJedis();
		List<String> list = null;
		try
		{
			list = jedis.brpop(timeout, key);
			return list;
		} catch (Exception e)
		{
			logger.error("发生异常" + e.getMessage());
		} finally
		{
			if (jedis != null)
			{
				jedis.close();
			}
		}
		return list;
	}
	
	/**
	 * 标记一个事务块的开始。
	 * @param jedis
	 * @return
	 */
	public Transaction multi(Jedis jedis)
	{
		try
		{
			return jedis.multi();
		} catch (Exception e)
		{
			logger.error("发生异常" + e.getMessage());
		}
		return null;
	}
	
	/**
	 * 执行所有事务块内的命令。
	 * @param tx
	 * @param jedis
	 * @return
	 */
	public List<Object> exec(Transaction tx, Jedis jedis)
	{
		try
		{
			return tx.exec();
		} catch (Exception e)
		{
			logger.error("发生异常" + e.getMessage());
		} finally
		{
			if (tx != null)
			{
				try
				{
					tx.close();
				}
				catch (IOException ioe)
				{
					logger.error("发生异常" + ioe.getMessage());
				}
			}
			if (jedis != null)
			{
				jedis.close();
			}
		}
		return null;
	}
	
	public long zadd(String key, double score, String value)
	{
		Jedis jedis = JedisAdapter.getJedis();
		try
		{
			return jedis.zadd(key, score, value);
		} catch (Exception e)
		{
			logger.error("发生异常" + e.getMessage());
		} finally 
		{
			if (jedis != null)
			{
				jedis.close();
			}
		}
		return 0;
	}
	
	public Set<String> zrange(String key, int start, int end)
	{
		Jedis jedis = JedisAdapter.getJedis();
		try
		{
			return jedis.zrange(key, start, end);
		} catch (Exception e)
		{
			logger.error("发生异常" + e.getMessage());
		} finally 
		{
			if (jedis != null)
			{
				jedis.close();
			}
		}
		return null;
	}
	
	public Set<String> zrevrange(String key, int start, int end)
	{
		Jedis jedis = JedisAdapter.getJedis();
		try
		{
			return jedis.zrevrange(key, start, end);
		} catch (Exception e)
		{
			logger.error("发生异常" + e.getMessage());
		} finally 
		{
			if (jedis != null)
			{
				jedis.close();
			}
		}
		return null;
	}
	
	public long zcard(String key)
	{
		Jedis jedis = JedisAdapter.getJedis();
		try
		{
			return jedis.zcard(key);
		} catch (Exception e)
		{
			logger.error("发生异常" + e.getMessage());
		} finally 
		{
			if (jedis != null)
			{
				jedis.close();
			}
		}
		return 0;
	}
	
	public Double zscore(String key, String member)
	{
		Jedis jedis = JedisAdapter.getJedis();
		try
		{
			return jedis.zscore(key, member);
		} catch (Exception e)
		{
			logger.error("发生异常" + e.getMessage());
		} finally 
		{
			if (jedis != null)
			{
				jedis.close();
			}
		}
		return null;
	}
}
