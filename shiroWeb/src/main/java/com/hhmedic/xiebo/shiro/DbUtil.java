package com.hhmedic.xiebo.shiro;

import java.sql.*;

public class DbUtil {

	// 数据库地址
	private static String dbUrl="jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2b8";
	// 用户名
	private static String dbUserName="xiebo";
	// 密码
	private static String dbPassword="123456";
	// 驱动名称
	private static String jdbcName="com.mysql.jdbc.Driver";

	public Connection getCon() throws Exception{
		Class.forName(jdbcName);
		Connection con=DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
		return con;
	}

	public void closeCon(Connection con) throws  Exception {
		if (con != null) {
			con.close();
		}
	}

	public static void main(String[] args) {
		DbUtil dbUtil = new DbUtil();
		try {
			dbUtil.getCon();
			System.out.println("数据库连接成功");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("数据库连接失败");
		}
	}
}
