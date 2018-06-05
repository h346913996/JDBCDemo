package dao;

import java.sql.SQLException;
import java.util.Map;

import dao.exception.JdbcRuntimeException;


public interface DataAccessObjects {
	
	public static final String SUCCESS_MESSAGE = "操作成功 ！	-version 332";
	/**
	 * 查询某条记录的某个属性
	 * @param tableName 表名
	 * @param keyword <关键字属性名，关键字值>
	 * @param propertyName 属性名称
	 * @return Json形式的字符串
	 * @throws JdbcRuntimeException 不存在查询结果或查询数据异常时抛出
	 * @throws SQLException 
	 */
	public String query(String tableName,Map<String,Object> keyword,String propertyName) throws JdbcRuntimeException, SQLException;
	/**
	 * 更新某条记录的一个或者多个属性
	 * @param tableName 表名
	 * @param property 需要更新的<属性名称，值>
	 * @param keyword 查询所需的<关键字属性名，关键字值>
	 * @return true:成功	false:失败
	 */
//	public boolean update(String tableName,Map<String,Object> properties,Map<String,Object> keyword) throws JdbcRuntimeException, SQLException;
	/**
	 * 增加一条记录
	 * @param tableName 表名
	 * @param record 记录<属性名，值>
	 * @return true:成功	false:失败
	 */
//	public boolean add(String tableName,Map<String,Object> record) throws JdbcRuntimeException, SQLException;
	/**
	 * 文件读取
	 * @param tableName	文件所在表名
	 * @param keyword 查找文件所需关键字<属性名称，属性值>
	 * @param propertyName 存放文件的属性名称
	 * @return 输出流
	 * @throws SQLException 
	 */
//	public InputStream FileRead(String tableName,Map<String,Object> keyword,String propertyName) throws JdbcRuntimeException, SQLException;
	/**
	 * sql查询语句执行
	 * @param sql sql语句
	 * @param values 参数数组
	 * @param pageSize 每页大小
	 * @param pageIndex 页码
	 * @param charSet 将流转换成String的编码
	 * @return json形式的数据集
	 * @throws SQLException 
	 */
	public String excuteSqlSearch(String sql,String[] values,String pageSize,String pageIndex,String charSet) throws SQLException;
	/**
	 * sql操作语句执行
	 * @param sql sql语句
	 * @param values 参数数组
	 * @return json形式的数据集
	 * @throws SQLException 
	 */
	public String excuteSql(String sql,String[] values) throws SQLException;
}
