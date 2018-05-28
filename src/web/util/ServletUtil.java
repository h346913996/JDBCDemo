package web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import dao.bean.Tables;

public class ServletUtil {
	
	/**
	 * xml初始化变量名称
	 */
	public static final String PATH_INIT_PARAMETER = "tempPath";

	/**
	 * xml初始化变量名称
	 */
	public static final String THRESHOLD_INIT_PARAMETER = "sizeThreshold";

	/**
	 * xml初始化变量名称
	 */
	public static final String FILEMAX_INIT_PARAMETER = "fileSizeMax";

	/**
	 * xml初始化变量名称
	 */
	public static final String CONTANTMAX_INIT_PARAMETER = "sizeMax";

	/**
	 * xml初始化变量名称
	 */
	public static final String SQLTABLE_INIT_PARAMETER = "sqlTable";

	/**
	 * xml初始化变量名称
	 */
	public static final String SQLNAME_INIT_PARAMETER = "sqlName";

	/**
	 * xml初始化变量名称
	 */
	public static final String CHARSET_INIT_PARAMETER = "charset";
	
	/**
	 * xml初始化变量名称
	 */
	public static final String ALLOWORIGIN_INIT_PARAMETER = "allowOrigin";
	
	/**
	 * 将stream转换成String
	 * @param is 输入流
	 * @param charSet 编码
	 * @return 被转换成String的输入流内容
	 */
	public static String convertStreamToString(InputStream is,String charSet) {
		BufferedReader reader = null;
		reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		String result = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
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
	 * 打包字符串
	 * @param str 要被打包的字符串
	 * @return 符合输出规范的类
	 */
	public static Tables packString(String str){
		Tables tables = new Tables();// 第一个表信息用于存放分页情况
		tables.setTableName("tb1");
		List<String> fields = new ArrayList<String>();// 记录总条数，每页大小，页码
		fields.add("blob"); // 总计记录条数
		tables.setFields(fields);// 放入表中
		List<List<Object>> records = new ArrayList<List<Object>>(); // 存储记录，每次加入一整条记录
		List<Object> record = new ArrayList<Object>(); // 存储单个记录，每次加入一个字段值
		record.add(str);
		records.add(record);// 存储
		tables.setRecords(records);
		return tables;
	}
	
	/**
	 * 参数验证
	 * @param str 参数
	 * @return true：有效参数	false：无效参数
	 */
	public static boolean validate(String str){
		if(str==null){
			return false;
		}else if(str.equals("")){
			return false;
		}else if((str.toLowerCase()).equals("null")){
			return false;
		}else{
			return true;
		}
	} 

}
