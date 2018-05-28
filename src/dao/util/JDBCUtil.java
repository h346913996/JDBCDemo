package dao.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class JDBCUtil {
	private static Context ctx;
	private static DataSource dataSource;
	
	static {
		try {
			ctx=new InitialContext();
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
		try {
			dataSource = (DataSource)ctx.lookup("java:comp/env/jdbc/drp");// 创建数据源
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
    public static Connection getConnection() throws SQLException{
        Connection connection = null;
        connection = dataSource.getConnection(); //从连接池中获取一个连接
        connection.setAutoCommit(false);
        return connection;
    }
    
}
