package com.myFirstSpring.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * 将敏感词文本导入，并建立前缀树；然后过滤敏感词
 * @author SFF
 * @date 2018-8-1
 */
@Service
public class SensitiveService implements InitializingBean
{
	private static final Logger logger = LoggerFactory.getLogger(SensitiveService.class);
	
	@Override
	public void afterPropertiesSet() throws Exception
	{
		try
		{
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("SensitiveWords.txt");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
			String lineTxt;
			while ((lineTxt = bufferedReader.readLine()) != null)
			{
				addWord(lineTxt);
			}
			bufferedReader.close();
		} catch (Exception e)
		{
			logger.info("读取敏感词文件失败：" + e.getMessage());
		}
	}
	
	// 把敏感词添加到前缀树中
	private void addWord(String lineTxt)
	{
		TrieNode tempNode = rootNode;
		for (int i = 0; i < lineTxt.length(); ++i)
		{
			Character c = lineTxt.charAt(i);
			
			if (isSymbol(c))
			{
				continue;
			}
			
			// 获取当前节点的子节点
			TrieNode node = tempNode.getSubNode(c);
			
			// 如果当前节点没有子节点，就新建一个节点并添加到树上
			if (node == null)
			{
				node = new TrieNode();
				tempNode.addSubNode(c, node);
			}
			
			// 如果有子节点，当前节点指向下一个节点
			tempNode = node;
			
			// 如果到达文件末尾，则标记
			if (i == lineTxt.length() - 1)
			{
				tempNode.setKeyWordsEnd(true);
			}
		}
	}
	
	private class TrieNode
	{
		// 用来标记敏感词的末尾
		private boolean end = false;
		
		// 当前节点下所有的子节点
		private Map<Character, TrieNode> subNodes = new HashMap<Character, TrieNode>();
		
		// 添加节点
		public void addSubNode(Character key, TrieNode node)
		{
			subNodes.put(key, node);
		}
		
		// 获取节点
		TrieNode getSubNode(Character key)
		{
			return subNodes.get(key);
		}
		
		// 判断是不是敏感词的末尾
		boolean isKeywordsEnd()
		{
			return end;
		}
		
		// 初始化构造一个敏感词的末尾
		void setKeyWordsEnd(boolean end)
		{
			this.end = end;
		}
	}
	
	private TrieNode rootNode = new TrieNode();
	
	private boolean isSymbol(char c)
	{
		int ic = (int) c;
		// 东亚文字 0x2E80-0x9FFF
		return !CharUtils.isAsciiAlphanumeric(c) && (ic < 0x2E80 || ic > 0x9FFF);
	}
	
	public String filter(String text)
	{
		if (StringUtils.isBlank(text))
		{
			return text;
		}
		
		String replacement = "***";
		
		StringBuilder result =  new StringBuilder();
		TrieNode tempNode = rootNode;	// 指针1——>指向树的指针
		int begin = 0;					// 指针2——>指向文本开始位置
		int position = 0;				// 指针3——>标记指针
		
		while (position < text.length())
		{
			char c = text.charAt(position);
			
			// 如果字符 c 不是东亚文字，则跳出循环
			if (isSymbol(c))
			{
				if (tempNode == rootNode)
				{
					result.append(c);
					++begin;
				}
				
				++position;
				continue;
			}
			
			tempNode = tempNode.getSubNode(c);
			
			if (tempNode == null)
			{
				// 若当前节点的子节点的为空，则直接将begin指向的char加到result中
				result.append(text.charAt(begin));
				position = begin + 1;
				begin = position;
				tempNode = rootNode;
			} else if (tempNode.isKeywordsEnd()) {
				// 若查找到有敏感词，则用replacement替换
				result.append(replacement);
				position = position + 1;
				begin = position;
				tempNode = rootNode;
			} else {
				// 当前节点的子节点不为空且也没有到达标记节点，则position向后移
				++position;
			}
		}
		// position移动到末尾，则将begin到position之间的字符加上
		result.append(text.substring(begin));
		return result.toString();
	}
	
	public static void main(String[] args)
	{
		SensitiveService s = new SensitiveService();
		s.addWord("色情");
		System.out.println(s.filter("你好色  情，白色情人节"));
	}
}
