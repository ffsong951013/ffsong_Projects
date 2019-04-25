package com.myFirstSpring.util;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Template;
import freemarker.template.TemplateException;

@Service
public class MailSender implements InitializingBean
{
	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
	private JavaMailSenderImpl javaMailSenderImpl;
	
	// 邮件的发送者
	@Value("${spring.mail.username}")
	private String from;
	
	@Autowired
	JavaMailSender javaMailSender;
	
	// 发送邮件的模板引擎
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	/**
	 * @param params 发送邮件的主题对象 object
	 * @param to 发送给谁
	 * @param title 邮件标题
	 * @param templateName 模板名称
	 * @return
	 */
	public boolean sendWithHTMLTemplate(String to, String title, String templateName, Map<String, Object> model)
	{
		try
		{
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();			// 创建一封邮件
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);	// 设置邮件的参数
			helper.setFrom(from);	// 从配置文件中获取发送者姓名
			helper.setTo(InternetAddress.parse(to));	// 发送给谁
			helper.setSubject("【" + title + "】");	// 邮件标题
			
			try
			{
				Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
				try
				{
					String text = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
					
					helper.setText(text, true);
					javaMailSender.send(mimeMessage);
				} catch (TemplateException e)
				{
					logger.error("模板异常" + e.getMessage());
				}
			} catch (IOException e)
			{
				logger.error(e.getMessage());
			}
			return true;
		} catch (Exception e)
		{
			logger.error("发送邮件失败" + e.toString());
			return false;
		}
	}
	
	
	@Override
	public void afterPropertiesSet() throws Exception
	{
		javaMailSenderImpl = new JavaMailSenderImpl();
		javaMailSenderImpl.setPort(465);
		javaMailSenderImpl.setProtocol("smtps");
		Properties javaMailProperties = new Properties();
		javaMailProperties.put("mail.smtp.ssl.enable", true);
		javaMailSenderImpl.setJavaMailProperties(javaMailProperties);
	}

}
