package com.qf.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 对数据库进行基本操作
 * @author 32848
 *
 */
public class DatabaseOperation {
	/**
	 * Query all results according to SQL statement
	 * 忽略了下划线
	 * @param <T>
	 * @param sqlString 	SQL statement
	 * @param objects		SQL parameter
	 * @return
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T> List<T> find(Class<T> type, String sqlString , Object...objects) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Connection connection = null;
		List<T> resultList = new ArrayList<T>();
		ResultSet executeQuery = null;
		
		connection = DatabaseConnecter.getConnection();
		
		PreparedStatement preparedStatement = null;
		
		preparedStatement = DatabaseConnecter.getPreparedStatement(connection, sqlString, objects);
		
		try {
			if (null != objects) { executeQuery = preparedStatement.executeQuery(); }
			else { executeQuery = preparedStatement.executeQuery(sqlString); }
			
			if (null != executeQuery) {
				while (executeQuery.next()) {
					ResultSetMetaData metaData = executeQuery.getMetaData();
					int columnCount = metaData.getColumnCount();
					
					Constructor<T> constructor = type.getConstructor();
					T newInstance = constructor.newInstance();
					
					for (int index = 0; index < columnCount; index++) {
						String columnName = metaData.getColumnName(index + 1);
						String tableNa = columnName;
						Method[] declaredMethods = type.getDeclaredMethods();
						
						for (Method method : declaredMethods) {
							
							/* 去掉下划线 */
							String[] split = columnName.split("_");
							String name = "";
							
							for (String str : split) {
								name += str;
							}
							
							if (method.getName().equalsIgnoreCase("set" + name)) {
								method.invoke(newInstance, executeQuery.getObject(tableNa));
							}
						}
						
					}
					
					resultList.add(newInstance);
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally { DatabaseConnecter.close(connection, preparedStatement, null); }
		
		return resultList;
	}
	
	/**
	 * Update database
	 * @param sqlString
	 * @param objects
	 * @throws SQLException
	 */
	public static void alterData(String sqlString, Object...objects) throws SQLException {
		Connection connection = DatabaseConnecter.getConnection();
		PreparedStatement preparedStatement = DatabaseConnecter.getPreparedStatement(connection, sqlString, objects);
		if (null != objects) preparedStatement.execute();
		else preparedStatement.execute(sqlString);
	}
	
	/**
	 * Find an result according to SQL statement
	 * @param <T>
	 * @param type
	 * @param sqlString		SQL statement
	 * @param objects		SQL parameter
	 * @return
	 * @throws SecurityException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static <T> T findOne(Class<T> type, String sqlString, Object...objects) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<T> find = find(type, sqlString, objects);
		
		if (0 == find.size()) return null;	return find.get(0);
	}
}
