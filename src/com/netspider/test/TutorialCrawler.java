/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.netspider.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.nodes.Document;

import cn.edu.hfut.dmic.webcollector.crawler.DeepCrawler;
import cn.edu.hfut.dmic.webcollector.model.Links;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.util.RegexRule;

import com.netspider.dao.CrawlResultDao;
import com.netspider.dao.UrlseedDao;

/**
 * WebCollector 2.x版本的tutorial 2.x版本特性： 1）自定义遍历策略，可完成更为复杂的遍历业务，例如分页、AJAX
 * 2）内置Berkeley DB管理URL，可以处理更大量级的网页 3）集成selenium，可以对javascript生成信息进行抽取
 * 4）直接支持多代理随机切换 5）集成spring jdbc和mysql connection，方便数据持久化 6）集成json解析器
 * 7）使用slf4j作为日志门面 8）修改http请求接口，用户自定义http请求更加方便
 * 
 * 可在cn.edu.hfut.dmic.webcollector.example包中找到例子(Demo)
 * 
 * @author hu
 */
public class TutorialCrawler extends DeepCrawler {

	/*
	 * 2.x版本中，爬虫的遍历由用户自定义(本质还是广度遍历，但是每个页面 生成的URL，也就是遍历树中每个节点的孩子节点，是由用户自定义的)。
	 * 
	 * 1.x版本中，默认将每个页面中，所有满足正则约束的链接，都当作待爬取URL，通过 这种方法可以完成在一定范围内(例如整站)的爬取(根据正则约束)。
	 * 
	 * 所以在2.x版本中，我们只要抽取页面中满足正则的URL，作为Links返回，就可以 完成1.x版本中BreadthCrawler的功能。
	 */
	static RegexRule regexRule = new RegexRule();

	public TutorialCrawler(String crawlPath) {
		super(crawlPath);
	}
	
	/*
	 * 检查页面是否存在关键字
	 */
	public boolean isExistKeywords(String html) {
		boolean isExist = false;
		if (html.indexOf(keyword) != -1) {
			isExist = true;
		}
		return isExist;
	}

	@Override
	public Links visitAndGetNextLinks(Page page) {
		Document doc = page.getDoc();
		
		String html = page.getHtml();
        String title = doc.title();
        System.out.println("URL:" + page.getUrl() +"  关键字："+getKeyword()+ "  标题:" + title);
        Map<String,String> map=new HashMap<String,String>();
        map.put("url", page.getUrl());
		if (isExistKeywords(html)) {
			save(map);
		}

		/* 下面是2.0版本新加入的内容 */
		/*
		 * 抽取page中的链接返回，这些链接会在下一轮爬取时被爬取。 不用担心URL去重，爬虫会自动过滤重复URL。
		 */
		Links nextLinks = new Links();

		/*
		 * 我们只希望抽取满足正则约束的URL， Links.addAllFromDocument为我们提供了相应的功能
		 */
		nextLinks.addAllFromDocument(doc, regexRule);

		/*
		 * Links类继承ArrayList<String>,可以使用add、addAll等方法自己添加URL
		 * 如果当前页面的链接中，没有需要爬取的，可以return null
		 * 例如如果你的爬取任务只是爬取seed列表中的所有链接，这种情况应该return null
		 */
		return nextLinks;
	}
	public void save(Map<String,String> map){
		CrawlResultDao dao = new CrawlResultDao();
		/* 将数据插入db */
		int result=dao.insert(new Object[]{map.get("url"),keyword});
		if (result == 1) {
			System.out.println("database插入成功");
		}
	}

	public static void main(String[] args) throws Exception {
		TutorialCrawler t=new TutorialCrawler("");
		t.start("是");
		
	}

	/**
	 * 启动方法,根据关键字解析种子列表,并设置规则
	 */
	public void start(String kw) {
		UrlseedDao dao = new UrlseedDao();
		List<String> list = dao.findAll(null);
		for (String li : list) {
			String match = li.substring(li.indexOf(".") + 1, li.lastIndexOf("."));
			regexRule.addRule("^http://.*" + match + ".*/.*");//忽略大小写？
			regexRule.addRule("^https://.*" + match + ".*/.*");
			regexRule.addRule("^ftp://.*" + match + ".*/.*");
//			regexRule.addRule("-.*[.][(jpg)|(png)|(gif)|(bmp)|(jpeg)]$");
			search(li,kw);
		}

	}

	private  String keyword = "";

	public  String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * 传入url遍历
	 * 
	 * @param seed
	 */
	public void search(String seed,String kw) {
		/*
		 * 构造函数中的string,是爬虫的crawlPath，爬虫的爬取信息都存在crawlPath文件夹中,
		 * 不同的爬虫请使用不同的crawlPath
		 */
		TutorialCrawler crawler = new TutorialCrawler("crawlerdb");
		crawler.setKeyword(kw);
		crawler.setThreads(5);
		crawler.addSeed(seed);
		crawler.setResumable(false);

		/* 2.x版本直接支持多代理随机切换 */
		Proxys proxys = new Proxys();
		crawler.setProxys(proxys);

		/* 设置是否断点爬取 */
		crawler.setResumable(false);

		try {
			crawler.start(2);
		} catch (Exception e) {
			System.out.println("爬虫启动失败");
			e.printStackTrace();
		}
	}

}