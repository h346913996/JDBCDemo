package dao.impl;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dao.DataAccessObjects;
import dao.bean.QueryResult;
import dao.bean.Tables;
import dao.exception.JdbcRuntimeException;
import dao.util.DaoUtil;
import dao.util.JDBCUtil;

public class OracleDAO implements DataAccessObjects {

	private static final String SQL_QUERYUSERTABLE = "SELECT X.* from ( "
			+ " SELECT ROWNUM NUMBERS,Z.* FROM ( "
			+ "SELECT ROWNUM ROWNUMBER,A.* FROM ( "
			+ "SELECT * FROM ALL_TAB_COMMENTS WHERE OWNER=? ) A "
			+ ") Z WHERE ROWNUM<=?" + ") X WHERE X.NUMBERS>=?";
	private static final String SQL_QUERYTABLENUM = "SELECT COUNT(*) FROM TABLENAME";
	private static final String SQL_QUERYTABLE = "SELECT * FROM TABLENAME";
	private static final String SQL_WHERE = " WHERE";
	private static final String SQL_PROPERTIES = " WORDNAME = ?";
	private static final String SQL_UPDATE = "UPDATE TABLENAME SET";
	private static final String SQL_INSERT = "INSERT INTO TABLENAME( PROPERTYNAME ) VALUES ( PROPERTYVALUE )";
	private static final String SQL_DESCRIBETABLE = "SELECT A.COLUMN_NAME,B.DATA_TYPE,B.DATA_LENGTH,B.NULLABLE,A.COMMENTS FROM all_col_comments A,all_tab_columns B WHERE A.Table_Name=B.TABLE_NAME AND A.OWNER = B.OWNER AND A.COLUMN_NAME = B.COLUMN_NAME AND B.Table_Name=?";
	private static final String SQL_DESCRIBETABLEWITHOWNER = "AND A.OWNER = ?";

	@Override
	public String query(String userName, String pageSize, String pageIndex)
			throws SQLException {
		Gson gson = new Gson();// 实例化Gson工具
		Connection connection = JDBCUtil.getConnection();// 获取数据库连接
		PreparedStatement ps = null;
		ResultSet rs = null;
		String json;
		Map<String, Object> result = new LinkedHashMap<String, Object>();// 用于存储最终结果
		result.put("Result", new QueryResult());// 存放Result
		try {

			ps = connection.prepareStatement(SQL_QUERYUSERTABLE);// 读取数据库语句
			ps.setString(1, userName);// 替换用户名
			int pageSizeInt = Integer.parseInt(pageSize);// 转换参数
			int pageIndexInt = Integer.parseInt(pageIndex);// 转换参数
			ps.setInt(2, (pageSizeInt * pageIndexInt));// 替换数字以限定查询区间
			ps.setInt(3, ((pageIndexInt - 1) * pageSizeInt) + 1);// 替换数字以限定查询区间
			rs = ps.executeQuery();// 执行数据库语句p
			ResultSetMetaData data = rs.getMetaData();// 获取数据库列
			Tables[] tables = new Tables[2];// 存放表信息
			tables[0] = new Tables();// 第一个表信息用于存放分页情况
			tables[0].setTableName("tb1");
			List<String> fields1 = new ArrayList<String>();// 记录总条数，每页大小，页码
			fields1.add("sum"); // 总计记录条数
			fields1.add("size"); // 每页大小
			fields1.add("indx"); // 当前页码
			tables[0].setFields(fields1);// 放入表中
			List<List<Object>> records1 = new ArrayList<List<Object>>(); // 存储记录，每次加入一整条记录
			List<Object> splitData = new ArrayList<Object>();// 存放分页数据
			Map<String, Object> keyword = new LinkedHashMap<String, Object>();// 存放查询关键字
			keyword.put("OWNER", userName);
			splitData.add(getSum("ALL_TAB_COMMENTS", keyword));// 获取该查询总记录数
			splitData.add(pageSize);
			splitData.add(pageIndex);
			records1.add(splitData);// 组装
			tables[0].setRecords(records1);// 放入表中

			tables[1] = new Tables();// 第二表信息用于存放查询到的信息
			tables[1].setTableName("ALL_TAB_COMMENTS");
			int columns = data.getColumnCount();// 获取数据库列数
			List<String> fields2 = new ArrayList<String>(); // 存储表字段名称等
			for (int i = 1; i <= columns; i++) {
				fields2.add(data.getColumnName(i)); // 放入字段名
			}
			tables[1].setFields(fields2);
			List<List<Object>> records2 = new ArrayList<List<Object>>(); // 存储记录，每次加入一整条记录
			while (rs.next()) {
				List<Object> record = new ArrayList<Object>();// 存放单条记录，每次加入一个字段的值
				for (int i = 1; i <= columns; i++) {
					String str = rs.getString(data.getColumnName(i));
					if (str == null) {
						record.add("");
					} else {
						record.add(str);
					}

				}
				records2.add(record);// 将整条记录放入records2
			}
			QueryResult re = (QueryResult) result.get("Result");// 从集合中获取查询结果对象
			if (records2.size() == 0) {// 判断集合是否存放查询到的记录
				re.setMsg("未找到选定行");// 添加结果信息
			} else {
				tables[1].setRecords(records2);
				re.setCode(1);// 更改结果码
				re.setMsg(SUCCESS_MESSAGE);// 添加结果信息
				result.put("Tables", tables);// 将表信息放入结果集合
			}
		} catch (SQLException e) {
			QueryResult re = (QueryResult) result.get("Result");// 从集合中获取查询结果对象
			if ((e.getMessage()).indexOf("12519") != -1
					|| (e.getMessage()).indexOf("12518") != -1) { // 数据库连接上限导致的错误
				re.setMsg("服务繁忙，请稍后再试！");
			} else {
				re.setMsg(e.getMessage());// 存储出错原因
			}
		} catch (NumberFormatException e) {
			QueryResult re = (QueryResult) result.get("Result");// 从集合中获取查询结果对象
			re.setMsg("输入参数错误- pageSize:" + pageSize + "，pageIndex:"
					+ pageIndex);// 存储出错原因
		} catch (JdbcRuntimeException e) {
			e.printStackTrace();
			QueryResult re = (QueryResult) result.get("Result");// 从集合中获取查询结果对象
			re.setMsg("输入参数错误- userName:" + userName);// 存储出错原因
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				connection.close();// 依次关闭接口
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		json = gson.toJson(result);
		return json;
	}

	/**
	 * 获取某表记录行数
	 * 
	 * @param tableName
	 *            表名
	 * @param keyword
	 *            查询打关键字<字段名。值>
	 * @return 返回查询到的记录行数
	 * @throws SQLException
	 */
	private String getSum(String tableName, Map<String, Object> keyword)
			throws SQLException, JdbcRuntimeException {
		Connection connection = JDBCUtil.getConnection();// 获取数据库连接
		PreparedStatement ps = null;
		ResultSet rs = null;
		String sum = null;
		StringBuffer sbSql = new StringBuffer(SQL_QUERYTABLENUM.replaceAll(
				"TABLENAME", tableName));// 替换数据库名
		sbSql.append(SQL_WHERE);
		sbSql.append(joint(keyword, " AND"));// 使用另外写的拼接函数
		String key = null;
		for (String word : keyword.keySet()) {// 获取的其中一个字段名
			key = word;
			break;
		}
		String sql = sbSql.toString().replaceAll("\\*", key);// 替换*以提高查询效率
		try {
			ps = connection.prepareStatement(sql);// 读取数据库语句
			int index = 1;// 第一条记录所在
			index = setMap(ps, index, keyword);// 使用自己写的批量替换方法
			rs = ps.executeQuery();// 执行数据库语句
			if (rs.next()) {
				sum = rs.getString(1);// 获取该表记录行数
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			connection.close();// 依次关闭接口
		}
		return sum;
	}

	/**
	 * sql语句拼接
	 * 
	 * @param keyword
	 *            <属性名，值>
	 * @param split
	 *            分隔字符
	 * @return
	 */
	private String joint(Map<String, Object> keyword, String split) {
		int index = 1;// 设置计数变量
		StringBuffer sql = new StringBuffer();
		for (String word : keyword.keySet()) {// 遍历map中的key并且拼凑数据库语句
			if (index != 1) {
				sql.append(split);// 如果不是第一个关键字要加分隔字符以衔接；
			}
			sql.append(SQL_PROPERTIES.replaceAll("WORDNAME", word));// 将关键字名称替换入语句
			index++;// 计数
		}
		return sql.toString();
	}

	/**
	 * sql语句批量替代"?"
	 * 
	 * @param ps
	 *            PreparedStatement接口
	 * @param index
	 *            从第几个开始
	 * @param keyword
	 *            要进行替代"?"的集合<属性名陈，属性值>
	 * @return 接下来将要被替代的"?"的位置
	 * @throws SQLException
	 */
	private int setMap(PreparedStatement ps, int index,
			Map<String, Object> keyword) throws SQLException,
			JdbcRuntimeException {
		for (String word : keyword.keySet()) {// 遍历关键字以取值
			Object obj = keyword.get(word);// 获取值
			// 判断值类型以便使用不同的方法将值代入数据库语句
			if (obj instanceof String) {
				ps.setString(index, (String) obj);
			} else if (obj instanceof Integer) {
				ps.setInt(index, (Integer) obj);
			} else if (obj instanceof Double) {
				ps.setDouble(index, (Double) obj);
			} else if (obj instanceof Float) {
				ps.setFloat(index, (Float) obj);
			} else if (obj instanceof Date) {
				ps.setTimestamp(index,
						new java.sql.Timestamp(((Date) obj).getTime()));
			} else if (obj instanceof InputStream) {
				ps.setBinaryStream(index, (InputStream) obj);
			} else {
				throw new JdbcRuntimeException("无法识别的查询数据类型");// 报错
			}
			index++;// 计数
		}
		return index;
	}

	@Override
	public boolean update(String tableName, Map<String, Object> properties,
			Map<String, Object> keyword) throws JdbcRuntimeException,
			SQLException {
		Connection connection = JDBCUtil.getConnection();// 获取数据库连接
		PreparedStatement ps = null;
		int affected;
		StringBuffer sql = new StringBuffer(SQL_UPDATE.replaceAll("TABLENAME",
				tableName));// 替换数据库名
		sql.append(joint(properties, " ,"));// 使用另外写的拼接函数
		sql.append(SQL_WHERE);
		sql.append(joint(keyword, " AND"));// 使用另外写的拼接函数
		try {
			ps = connection.prepareStatement(sql.toString());// 读取数据库语句
			int index = 1;// 计数
			index = setMap(ps, index, properties);// 使用自己写的批量替换方法
			index = setMap(ps, index, keyword);// 使用自己写的批量替换方法
			affected = ps.executeUpdate();// 执行更新语句
			connection.commit();// 提交操作
		} finally {
			if (ps != null) {
				ps.close();
			}
			connection.close();
			// 依次关闭接口
		}
		if (affected != 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean add(String tableName, Map<String, Object> record)
			throws JdbcRuntimeException, SQLException {
		Connection connection = JDBCUtil.getConnection();// 获取数据库连接
		PreparedStatement ps = null;
		int affected = 0;
		String sql = SQL_INSERT.replaceAll("TABLENAME", tableName);// 替换数据库名
		StringBuffer propertyName = new StringBuffer();
		int index = 1;// 设置计数变量
		for (String word : record.keySet()) {// 遍历map中的key并且拼凑数据库语句
			if (index != 1) {
				propertyName.append(",");// 如果不是第一个关键字要加分隔字符以衔接；
			}
			propertyName.append(word);// 将关键字名称加入语句
			index++;// 计数
		}
		StringBuffer values = new StringBuffer("?");
		for (int i = 0, len = record.size() - 1; i < len; i++) {
			values.append(",?");// 获取同等数量的"?"
		}
		sql = sql.replaceAll("PROPERTYNAME", propertyName.toString());// 替换到数据库语句中
		sql = sql.replaceAll("PROPERTYVALUE", values.toString());// 替换到数据库语句中
		try {
			ps = connection.prepareStatement(sql);// 读取数据库语句
			index = 1;// 计数归位
			index = setMap(ps, index, record);// 使用自己写的批量替换方法
			affected = ps.executeUpdate();// 执行更新语句
			connection.commit();// 提交操作
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			// 依次关闭接口
		}
		if (affected != 0) {// 验证受到影响的行数
			return true;
		}
		return false;
	}

	@Override
	public InputStream FileRead(String tableName, Map<String, Object> keyword,
			String propertyName) throws JdbcRuntimeException, SQLException {
		StringBuffer sql = new StringBuffer(SQL_QUERYTABLE.replaceAll(
				"TABLENAME", tableName));// 替换数据库名
		sql.append(SQL_WHERE);// 添加where，准备增加限制条件
		sql.append(joint(keyword, " AND"));// 使用另外写的拼接函数
		// 缓存查询
		InputStream is = null;
		Connection connection = JDBCUtil.getConnection();// 获取数据库连接
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql.toString());// 读取数据库语句
			int index = 1;// 计数
			index = setMap(ps, index, keyword);// 使用自己写的批量替换方法
			rs = ps.executeQuery();// 执行数据库语句
			if (rs.next()) {
				is = rs.getBinaryStream(propertyName);// 查询结果
			} else {
				throw new JdbcRuntimeException("文件不存在！");// 报错
			}
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				connection.close();
				// 依次关闭接口
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return is;
	}

	@Override
	public String query(String tableName, Map<String, Object> keyword,
			String propertyName) throws JdbcRuntimeException, SQLException {
		StringBuffer sql = new StringBuffer(SQL_QUERYTABLE.replaceAll(
				"TABLENAME", tableName));// 替换数据库名
		sql.append(SQL_WHERE);// 添加where，准备增加限制条件
		sql.append(joint(keyword, " AND"));// 使用另外写的拼接函数
		String result = null;
		Connection connection = JDBCUtil.getConnection();// 获取数据库连接
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(sql.toString());// 读取数据库语句
			int index = 1;// 计数
			index = setMap(ps, index, keyword);// 使用自己写的批量替换方法
			rs = ps.executeQuery();// 执行数据库语句
			if (rs.next()) {
				result = rs.getString(propertyName);// 查询结果
			} else {
				throw new JdbcRuntimeException("没有符合条件的数据");// 报错
			}
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (ps != null) {
				ps.close();
			}
			connection.close();
			// 依次关闭接口
		}
		return result;
	}

	@Override
	public String excuteSqlSearch(String sql, String[] values, String pageSize,
			String pageIndex, String charSet) throws SQLException {
		sql = DaoUtil.stitchingStatement(sql, values);// 拼接sql语句
		Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping()
				.create();// 实例化Gson工具
		// Gson gson = new Gson();// 实例化Gson工具
			Connection connection = JDBCUtil.getConnection();// 获取数据库连接
			PreparedStatement ps = null;
			ResultSet rs = null;
			Map<String, Object> result = new LinkedHashMap<String, Object>();// 用于存储最终结果
			result.put("Result", new QueryResult());// 存放Result
			String json;
			try {
				ps = connection.prepareStatement(sql);// 读取数据库语句
				rs = ps.executeQuery();// 执行语句
				ResultSetMetaData data = rs.getMetaData();// 获取数据库列
				Tables[] tables = new Tables[2];// 存放表信息

				tables[0] = new Tables();// 第一个表信息用于存放分页情况
				tables[0].setTableName("tb1");
				List<String> fields1 = new ArrayList<String>();// 记录总条数，每页大小，页码
				fields1.add("sum"); // 总计记录条数
				fields1.add("size"); // 每页大小
				fields1.add("indx"); // 当前页码
				tables[0].setFields(fields1);// 放入表中

				tables[1] = new Tables();
				tables[1].setTableName(data.getTableName(1));

				List<String> fields = new ArrayList<String>();// 存放字段名
				int columns = data.getColumnCount();// 获取表的字段数
				for (int i = 1; i <= columns; i++) {
					fields.add(data.getColumnName(i));// 循环以获得字段名称
				}
				tables[1].setFields(fields);
				List<List<Object>> records = new ArrayList<List<Object>>(); // 存储记录，每次加入一整条记录
				int index = 1;// 当前查询的第几条记录
				int pageSizeInt = Integer.parseInt(pageSize);// 转换参数
				int pageIndexInt = Integer.parseInt(pageIndex);// 转换参数
				int front = ((pageIndexInt - 1) * pageSizeInt) + 1; // 从第几条开始属于查询内容
				int behind = (pageSizeInt * pageIndexInt); // 到第几条结束
				while (rs.next()) {
					List<Object> record = new ArrayList<Object>(); // 存储单个记录，每次加入一个字段值
					for (int i = 1; i <= columns; i++) {
						if (data.getColumnTypeName(i).equals("BLOB")) {
							record.add(DaoUtil.convertStreamToString(
									rs.getBinaryStream(i), charSet));// 将流转换成string再存入
						} else {
							String str = rs.getString(i);
							if (str == null) {
								record.add("");// 循环以获得字段值
							} else {
								record.add(str);// 循环以获得字段值
							}
						}
					}
					if (index >= front && index <= behind) {
						records.add(record);// 存储
					}
					index++;
				}
				QueryResult re = (QueryResult) result.get("Result");// 从集合中获取查询结果对象
				if (records.size() == 0) {// 判断集合是否存放查询到的记录
					re.setMsg("未找到选定行");// 添加结果信息
				} else {
					List<List<Object>> records1 = new ArrayList<List<Object>>(); // 存储记录，每次加入一整条记录
					List<Object> splitData = new ArrayList<Object>();// 存放分页数据
					splitData.add(String.valueOf(index - 1));// 获取该查询总记录数
					splitData.add(pageSize);
					splitData.add(pageIndex);
					records1.add(splitData);// 组装
					tables[0].setRecords(records1);// 放入表中
					tables[1].setRecords(records);
					re.setCode(1);// 更改结果码
					re.setMsg(SUCCESS_MESSAGE);// 添加结果信息
					result.put("Tables", tables);// 将表信息放入结果集合

				}

			} catch (SQLException e) {
				QueryResult re = (QueryResult) result.get("Result");// 从集合中获取查询结果对象
				if ((e.getMessage()).indexOf("12519") != -1
						|| (e.getMessage()).indexOf("12518") != -1) { // 数据库连接上限导致的错误
					re.setMsg("服务繁忙，请稍后再试！");
				} else {
					re.setMsg(e.getMessage());
				}
			} finally {
				try {
					if (rs != null) {
						rs.close();
					}
					if (ps != null) {
						ps.close();
					}
					connection.close();
					// 依次关闭接口
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			json = gson.toJson(result);
			return json;
	}

	@Override
	public String excuteSql(String sql, String[] values) throws SQLException {
		Connection connection = JDBCUtil.getConnection();// 获取数据库连接
		PreparedStatement ps = null;
		String json = null;
		Map<String, Object> result = new LinkedHashMap<String, Object>();// 用于存储最终结果
		result.put("Result", new QueryResult());// 存放Result
		Gson gson = new Gson();// 实例化Gson工具
		try {
			sql = DaoUtil.stitchingStatement(sql, values);// 拼接sql语句
			int affected = 0;
			// if( (sql.toUpperCase()).indexOf("INSERT") != -1 ){
			// CallableStatement psc =
			// connection.prepareCall("BEGIN "+sql+"returning id into ?; END;");//
			// 读取数据库语句
			// psc.registerOutParameter(1,Types.NUMERIC);
			// psc.execute();
			// System.out.print(psc.getString(1));
			// affected = 1;
			// }else{
			ps = connection.prepareStatement(sql);// 读取数据库语句
			affected = ps.executeUpdate();// 执行更新语句
			// }
			// 删除相关表缓存

			connection.commit();// 提交操作
			if (affected != 0) {// 验证受到影响的行数
				QueryResult re = (QueryResult) result.get("Result");// 从集合中获取查询结果对象
				re.setCode(1);// 更改结果码
				re.setMsg(SUCCESS_MESSAGE);// 添加结果信息
			}
		} catch (SQLException e) {
			e.printStackTrace();
			QueryResult re = (QueryResult) result.get("Result");// 从集合中获取查询结果对象
			if ((e.getMessage()).indexOf("12519") != -1
					|| (e.getMessage()).indexOf("12518") != -1) { // 数据库连接上限导致的错误
				re.setMsg("服务繁忙，请稍后再试！");
			} else {
				re.setMsg(e.getMessage());
			}
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				connection.close();
				// 依次关闭接口
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		json = gson.toJson(result);
		return json;
	}

	/**
	 * 获取主键字段名方法
	 * 
	 * @param tableName
	 *            表名
	 * @return 主键字段名
	 */
	public String getPrimaryKeyName(String tableName) {
		Connection connection;
		String primaryKeyName = null;
		try {
			connection = JDBCUtil.getConnection();// 获取数据库连接
			DatabaseMetaData meta = connection.getMetaData();// 获取数据库信息
			ResultSet rs = meta.getPrimaryKeys(null, null, tableName);// 获取表信息
			while (rs.next()) {
				primaryKeyName = rs.getString("COLUMN_NAME");
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return primaryKeyName;

	}

	@Override
	public Tables describeTable(String[] tableName) {
		Connection connection = null;
		try {
			connection = JDBCUtil.getConnection();// 获取数据库连接
		} catch (SQLException e) {
			e.printStackTrace();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		Tables tables = null;// 存放表信息
		try {
			String sql = null;
			if (tableName.length > 1) {
				sql = SQL_DESCRIBETABLE + SQL_DESCRIBETABLEWITHOWNER;
			} else {
				sql = SQL_DESCRIBETABLE;
			}
			ps = connection.prepareStatement(sql);// 读取数据库语句
			ps.setString(1, tableName[1]);
			if (tableName.length > 1) {
				ps.setString(2, tableName[0]);
			}
			rs = ps.executeQuery();// 执行数据库语句p
			tables = new Tables();// 存放表信息
			tables.setTableName(tableName[0]);
			List<String> fields = new ArrayList<String>();// 存储字段名
			fields.add("COLUMN_NAME"); // 字段名
			fields.add("DATA_TYPE"); // 字段类型
			fields.add("DATA_LENGTH"); // 字段类型
			fields.add("NULLABLE"); // 是否为空
			fields.add("COMMENTS"); // 注释
			tables.setFields(fields);// 放入表中
			List<List<Object>> records = new ArrayList<List<Object>>(); // 存储字段信息，每次加入一条字段信息
			while (rs.next()) {
				List<Object> record = new ArrayList<Object>();// 存放单条记录，每次加入一个字段的数据
				for (int i = 0; i <= 4; i++) {
					String str = rs.getString(fields.get(i));
					if (str == null) {
						record.add("");
					} else {
						record.add(str);
					}

				}
				records.add(record);// 将整条记录放入records2
			}
			tables.setRecords(records);// 放入表中
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (ps != null) {
					ps.close();
				}
				connection.close();// 依次关闭接口
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return tables;
	}
}
