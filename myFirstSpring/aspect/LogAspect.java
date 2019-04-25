package com.myFirstSpring.aspect;

import java.util.Date;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/*
 * 在持久层、业务层和控制层分别采用 @Repository、@Service 和 @Controller 对分层中的类进行注释，而用 @Component 对那些比较中立的类进行注释;
 * 这里就是说把这个类交给Spring管理，重新起个名字叫userManager，由于不好说这个类属于哪个层面，就用@Component
 */
@Aspect
@Component
public class LogAspect
{
	private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
	
	@Before("execution(* com.myFirstSpring.controller.*Controller.*(..))")
	public void beforeMethod(JoinPoint joinPoint)
	{
		StringBuilder sb = new StringBuilder();
		for (Object arg : joinPoint.getArgs())
		{
			if (arg != null)
			{
				sb.append("arg:" + arg.toString() + "|");
//				System.out.println("信息：" + arg.toString());
			}
		}
		logger.info("before method" + new Date() + sb.toString());
	}
	
	@After("execution(* com.myFirstSpring.controller.*Controller.*(..))")
	public void afterMethod()
	{
		logger.info("after method" + new Date());
	}
}
