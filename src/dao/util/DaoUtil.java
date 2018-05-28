package dao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;

public class DaoUtil {
	/**
	 * sql拼接函数
	 * 
	 * @param sql
	 *            带{0-9} 的sql语句
	 * @param values
	 *            将要替换的值
	 * @return 一个拼接完成的sql语句
	 */
	public static String stitchingStatement(String sql, String[] values) {
		if (values != null) {
			for (int i = 0, len = values.length; i < len; i++) {
				sql = sql.replaceAll("\\{[" + i + "]\\}",
						"fb7a0b12-6fcf-44db-8ccb-5e36693b0401" + i);
			}
			sql = sql.replaceAll("\\{[0-9]*\\}", "");
			for (int i = 0, len = values.length; i < len; i++) {
				sql = sql.replaceAll(
						"fb7a0b12-6fcf-44db-8ccb-5e36693b0401" + i, values[i]);
			}
		}
		return sql;
	}

	/**
	 * 返回执行的语句更改了哪些表。
	 * 
	 * @param sql
	 *            sql语句
	 * @return 语句执行相关的表名
	 */
	public static String getSqlTableName(String sql) {
		String[] str = sql.toUpperCase().split(" ");
		if (str[0].equals("DELETE") || str[0].equals("INSERT")) {
			return str[2];
		} else if (str[0].equals("UPDATE")) {
			return str[1];
		} else if (str[0].equals("CREATE")) {
			return "ALL_TAB_COMMENTS";
		} else if (str[0].equals("SELECT")) {
			for (int i = 0, len = str.length; i < len; i++) {
				if (str[i].equals("FROM")) {
					return str[i + 1];
				}
			}
			return null;
		} else {
			return null;
		}
	}

	/**
	 * 将stream转换成String
	 * 
	 * @param is
	 *            输入流
	 * @param charSet
	 *            编码
	 * @return 被转换成String的输入流内容
	 */
	public static String convertStreamToString(InputStream is, String charSet) {
		BufferedReader reader = null;
		if (is == null) {
			return "";
		}
		reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		String result = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			result = new String(sb.toString().getBytes(), charSet);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 获取插入语句的字段名称和值
	 * 
	 * @param sql
	 *            sql语句
	 * @return
	 */
	public static Map<String, Object> getInsertProperty(String sql) {
		int begin = sql.indexOf("(");
		int end = sql.indexOf(")");
		String[] propertyName = sql.substring(begin + 1, end - 1).split(",");
		sql = sql.substring(end);
		begin = sql.indexOf("(");
		String[] property = sql.substring(begin + 1, sql.length() - 1).split(
				",");
		Map<String, Object> keyword = new LinkedHashMap<String, Object>();
		for (int i = 0, l = propertyName.length; i < l; i++) {
			keyword.put(propertyName[i], property[i].replaceAll("'", "")
					.replaceAll("||", ""));
		}
		return keyword;
	}
}
