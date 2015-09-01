package com.netspider.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.netspider.db.DBUtil;


public class UrlseedDao {
	private static final String FINDALL = "select id,url from url_seed";

	public static void main(String[] args) {
		UrlseedDao dao = new UrlseedDao();
		List<String> list=dao.findAll( null);
		for(String li:list){
			System.out.println(li);
		}

	}

	public List findAll( Object[] params) {
		String sql=FINDALL;
		List l = new ArrayList();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = DBUtil.getConnection();
			// 预编译声明sql
			ps = conn.prepareStatement(sql);
			// 装填所有sql参数
			if (params != null) {
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
			}
			// 执行sql,一次性获得结果集,多行记录的表
			rs = ps.executeQuery();
			// 迭代结果集,将每一行添加到集合l中
			while (rs.next()) {
				// 一行记录对应一个实例化对象,并将实例化后的对象添加到集合l中
				l.add(rs.getString(2));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				// 关闭结果集
				if (rs != null)
					rs.close();
				// 关闭ps
				if (ps != null)
					ps.close();
				// 关闭连接
				DBUtil.closeConnection(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return l;
	}
}
