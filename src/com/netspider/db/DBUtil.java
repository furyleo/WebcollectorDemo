package com.netspider.db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

import org.apache.commons.dbcp.BasicDataSource;

public class DBUtil {
	private static Logger logger=Logger.getLogger(DBUtil.class.getName());
	private static String driver = "";
	private static String url = "";
	private static String user = "";
	private static String pwd = "";
	private static int initialSize;
	private static BasicDataSource ds=null;
	static{
		
			Properties props=new Properties();
			try {
				props.load(DBUtil.class.getClassLoader().getResourceAsStream("db.properties"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			driver=props.getProperty("driver");
			url=props.getProperty("url");
			user=props.getProperty("user");
			pwd=props.getProperty("pwd");
			logger.info("DBUtil-->读取配置文件完毕");
			//连接池
			initialSize=Integer.parseInt(props.getProperty("initialSize"));
			ds=new BasicDataSource();
			ds.setDriverClassName(driver);
			ds.setUrl(url);
			ds.setUsername(user);
			ds.setPassword(pwd);
			ds.setInitialSize(initialSize);
			logger.info("DBUtil-->初始化连接池完毕");
//			Class.forName(driver);
		
		
	}
	public static Connection getConnection() throws SQLException {
		logger.info("DBUtil-->获取连接");
		return ds.getConnection();
//		return DriverManager.getConnection(url, user, pwd);
	}
	public static void closeConnection(Connection conn) throws SQLException{
		logger.info("DBUtil-->关闭连接");
		if(conn!=null) conn.close();
	}
}
