package com.netspider.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.netspider.db.DBUtil;

//此工具类作用:实现sql的DQL,DML操作
//此工具类需要子类继承,实现抽象方法
public abstract class BaseDaoUtil<T> {
	/**
	 * DML增删改操作
	 * @param sql 		sql语句
	 * @param params	sql语句的占位参数数组
	 * @return			受影响行数
	 */
	public int dml(String sql,Object[] params){
		Connection conn=null;
		PreparedStatement ps=null;
		int result=-1;
		try{
			conn=DBUtil.getConnection();
			//于编译声明sql
			ps=conn.prepareStatement(sql);
			//装填所有sql参数
			if(params!=null){
				for(int i=0;i<params.length;i++){
					ps.setObject(i+1, params[i]);
				}
			}
			//返回受影响行数
			result=ps.executeUpdate();
		}catch(Exception e){e.printStackTrace();}
		finally{
			try{
				//关闭ps
				if(ps!=null)ps.close();
				//关闭连接
				DBUtil.closeConnection(conn);
			}catch(Exception e){e.printStackTrace();}
		}
		return result;
	}
}
