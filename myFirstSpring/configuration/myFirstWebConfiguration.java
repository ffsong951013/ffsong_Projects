package com.myFirstSpring.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.myFirstSpring.interceptor.LoginRequiredInterceptor;
import com.myFirstSpring.interceptor.PassportInterceptor;

@Component
public class myFirstWebConfiguration extends WebMvcConfigurerAdapter
{
	@Autowired
	PassportInterceptor passportInterceptor;
	
	@Autowired
	LoginRequiredInterceptor loginRequiredInterceptor;
	
	/**
	 * 将拦截器添加到拦截链路上，使得spring在初始化时，就可以使用这个拦截器，在所有的请求之前都会先经过拦截器
	 */
	@Override
	public void addInterceptors(InterceptorRegistry interceptorregistry)
	{
		interceptorregistry.addInterceptor(passportInterceptor);
		interceptorregistry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/user/*");
		super.addInterceptors(interceptorregistry);
	}
}
