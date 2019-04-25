package com.myFirstSpring.controller;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.myFirstSpring.service.TestService;

@Controller
public class SettingController
{
	@Autowired
	TestService testService;
	
	@RequestMapping(path={"/setting"},method = {RequestMethod.GET})
	@ResponseBody
	public String setting(HttpSession session)
	{
		return "Setting OK." + testService.getMessage(2);
	}
}
