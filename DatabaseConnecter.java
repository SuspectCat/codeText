package com.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnecter {
	private static Properties databaseProperties = new Properties();
	private static String driver = null;
	private static String url = null;
	private static String user = null;
	private static String password = null;
	
	static {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		InputStream resourceAsStream = contextClassLoader.getResourceAsStream("db.properties");
		try {
			databaseProperties.load(resourceAsStream);
			
			driver = databaseProperties.getProperty("jdbc.driver");
			url = databaseProperties.getProperty("jdbc.url");
			user = databaseProperties.getProperty("jdbc.user");
			password = databaseProperties.getProperty("jdbc.password");
			
			Class.forName(driver);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Create an connection for database
	 * @return
	 */
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Register a database statement
	 * @param connection
	 * @return
	 */
	public static Statement getStatement(Connection connection) {
		try {
			return connection.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Register a database statement which has SQL parameter
	 * @param connection	Database connection
	 * @param sqlString		SQL language
	 * @param objects		SQL parameter
	 * @return
	 */
	public static PreparedStatement getPreparedStatement(Connection connection, String sqlString, Object...objects) {
		try {
			PreparedStatement prepareStatement = connection.prepareStatement(sqlString);
			
			if (null != objects) { for (int i = 0; i < objects.length; i++) { prepareStatement.setObject(i + 1, objects[i]); }}
			
			return prepareStatement;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Close the connection of database
	 * @param connection
	 * @param statement
	 * @param resultSet
	 */
	public static void close(Connection connection, Statement statement, ResultSet resultSet) {
		try {
			if (null != resultSet) resultSet.close();
			if (null != statement) statement.close();
			if (null != connection) connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
