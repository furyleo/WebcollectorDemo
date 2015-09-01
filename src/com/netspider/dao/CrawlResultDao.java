package com.netspider.dao;

public class CrawlResultDao extends BaseDaoUtil{
	String insertsql="insert into tb_content(url,keyword) values(?,?)";
	public int insert(Object[] objs){
		return super.dml(insertsql, objs);
	}
	public static void main(String[] args) {
		CrawlResultDao dao=new CrawlResultDao();
		dao.insert(new Object[]{"http://www.baidu.com","women"});
		System.out.println("over..");
	}
}
