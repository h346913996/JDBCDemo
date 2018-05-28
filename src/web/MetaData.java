package web;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import service.DaoWebService;
import web.util.ServletUtil;

import com.google.gson.Gson;

import dao.DataAccessObjects;
import dao.bean.QueryResult;
import dao.bean.Tables;
import dao.exception.JdbcRuntimeException;

public class MetaData extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * dao层接口
	 */
	private DataAccessObjects dao;

	

	/**
	 * 编码
	 */
	private String charset;

	/**
	 * 临时文件目录
	 */
	private String tempPath;
	
	/**
	 * 允许跨域调用
	 */
	private String allowOrigin;

	/**
	 * 非加密sql语句所在表
	 */
	private String sqlTable;

	/**
	 * 非加密sql语句字段名称
	 */
	private String sqlName;

	/**
	 * 缓存区大小
	 */
	private int sizeThreshold;

	/**
	 * 单个文件限制大小
	 */
	private int fileSizeMax;

	/**
	 * 总文件限制大小
	 */
	private int sizeMax;

	/**
	 * Constructor of the object.
	 */
	public MetaData() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if("1".equals(allowOrigin)){
			response.setHeader("Access-Control-Allow-Origin", "*"); //解决跨域访问报错 
			response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE"); 
			response.setHeader("Access-Control-Max-Age", "3600"); //设置过期时间 
			response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, client_id, uuid, Authorization");
		}
		if ("61".equals(request.getParameter("aau68"))) {// 如果不为空则调用查询方法
			getUserTable(request, response);
		} else if ("63".equals(request.getParameter("ccu70"))) {// 如果不为空则调用上传方法
			blobUpload(request, response);
		} else if ("39".equals(request.getParameter("dm8it"))) {// 如果不为空则使用下载方法
			blobDownload(request, response);
		} else if ("38".equals(request.getParameter("xzu47"))) {// 如果不为空则使用执行语句方法
			excuteSql(request, response);
		} else if ("5c5".equals(request.getParameter("ea7"))) {// 如果不为空则使用执行语句方法
			excuteSql(request, response);
		} else if ("151".equals(request.getParameter("cba11"))) {// 如果不为空则使用执行语句方法
			describeTable(request, response);
		}
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doGet(request, response);
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
	public void init() throws ServletException {
		this.tempPath = this.getInitParameter(ServletUtil.PATH_INIT_PARAMETER); // 读取配置文件的数据
		this.sizeThreshold = Integer.parseInt(this
				.getInitParameter(ServletUtil.THRESHOLD_INIT_PARAMETER));
		this.fileSizeMax = Integer.parseInt(this
				.getInitParameter(ServletUtil.FILEMAX_INIT_PARAMETER));
		this.sizeMax = Integer.parseInt(this
				.getInitParameter(ServletUtil.CONTANTMAX_INIT_PARAMETER));
		this.sqlTable = this.getInitParameter(ServletUtil.SQLTABLE_INIT_PARAMETER);
		this.sqlName = this.getInitParameter(ServletUtil.SQLNAME_INIT_PARAMETER);
		this.charset = this.getInitParameter(ServletUtil.CHARSET_INIT_PARAMETER);
		this.allowOrigin = this.getInitParameter(ServletUtil.ALLOWORIGIN_INIT_PARAMETER);
	}

	/**
	 * 获取某用户下的表
	 * 
	 * @param request
	 *            由客户端送来服务端的请求
	 * @param response
	 *            由服务端送到客户端的反馈
	 * @throws IOException
	 */
	private void getUserTable(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String pageSize = request.getParameter("k"); // 获取每页大小
		String pageIndex = request.getParameter("l"); // 获取需要查询的是第几页
		String userName = request.getParameter("j"); // 获取用户名
		QueryResult qrs = new QueryResult(); // 存储结果对象
		Map<String, Object> result = new LinkedHashMap<String, Object>();// 用于存储最终结果
		result.put("Result", qrs);// 存放Result
		Gson gson = new Gson(); // 实例化json转换工具
		String json = null;
		dao = DaoWebService.getDao(); // 实例化model层
		try {
			json = dao.query(userName, pageSize, pageIndex);// 执行查询函数
		} catch (SQLException e) {
			 if((e.getMessage()).indexOf("12519")!=-1 || (e.getMessage()).indexOf("12518")!=-1){ //数据库连接上限导致的错误
				 qrs.setMsg("服务繁忙，请稍后再试！");
			 }else{
				 qrs.setMsg(e.getMessage());
			 }
			json = gson.toJson(result);
		} catch (NullPointerException e) {
			qrs.setMsg("服务繁忙，请稍后再试！");
			json = gson.toJson(result);
		}
		PrintWriter out = response.getWriter();
		String jsoncallback = request.getParameter("jsoncallback"); // 获取jsoncallback
		if (ServletUtil.validate(jsoncallback)) { // 如果jsoncallback参数有效，则进行jsonp包装
			out.print(jsoncallback + "(" + json + ")");
		} else {
			out.print(json);
		}
		out.flush();
		out.close();
	}

	/**
	 * blob 文件下载
	 * 
	 * @param request
	 *            由客户端送来服务端的请求
	 * @param response
	 *            由服务端送到客户端的反馈
	 * @throws IOException
	 */
	private void blobDownload(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		dao = DaoWebService.getDao(); // 实例化model层
		String blobName = request.getParameter("h"); // blob字段名
		String primaryKeyValue = request.getParameter("j"); // 主键值
		String primaryKeyName = request.getParameter("i"); // 主键名
		String tableName = request.getParameter("g"); // 表名
		String fileMIMETypeName = request.getParameter("k");// 文件类型字段名
		String fileNameName = request.getParameter("m"); // 文件名字段名
		String outType = request.getParameter("l");// 强制定义文件类型
		String outName = request.getParameter("n");// 强制定义 文件名
		String charSet = request.getParameter("o");// 编码
		String downloadOrShowString = request.getParameter("p");// 直接下载或输出文字
		String fileName = outName;// 强制定义文件名赋值
		String mimeType = outType;// 强制定义文件类型赋值
		Map<String, Object> keyword = new LinkedHashMap<String, Object>(); // 用于存放主键，执行查询操作所需
		keyword.put(primaryKeyName, primaryKeyValue); // 将主键字段名和值放入
		QueryResult qrs = new QueryResult(); // 存储结果对象
		Map<String, Object> result = new LinkedHashMap<String, Object>();// 用于存储最终结果
		result.put("Result", qrs);// 存放Result
		Gson gson = new Gson(); // 实例化json转换工具
		String json = null; //存放返回的json串
		boolean exceptionCatched = false;
		boolean  alreadyCalledResponse = false;
		try {
			if (!ServletUtil.validate(outName)) { // 如果没有强制定义文件名
				fileName = dao.query(tableName, keyword, fileNameName); // 将数据库里的文件名取出
			}
			if (!ServletUtil.validate(outType)) { // 如果没有强制定义的输出类型
				mimeType = dao.query(tableName, keyword, fileMIMETypeName); // 将数据库里的文件类型取出
			}
			if (!ServletUtil.validate(charSet)) {// 如果没有设置编码
				charSet = "UTF-8";// 默认为UTF-8
			}
			InputStream is = dao.FileRead(tableName, keyword, blobName); // 读取文件获得输入流
			if ("1".equals(downloadOrShowString) || mimeType.indexOf("text/plain")==-1) {
				response.setHeader("Content-Type", mimeType);// 设置信息头的文件类型
				String file_name = new String(fileName.getBytes(), "ISO-8859-1"); // 解决中文无法显示的问题
				if ("1".equals(downloadOrShowString)) {
					response.setHeader("content-disposition",
							"attachment;filename=\"" + file_name + "\"");// 设置信息头的文件名，文件名加两边加引号保证火狐浏览器下载时不截断空格
				}
				OutputStream out = response.getOutputStream();// 创建输出流
				byte buffer[] = new byte[1024]; // 创建缓冲区
				int len = 0;
				while ((len = is.read(buffer)) > 0) {// 循环将输入流中的内容读取到缓冲区当中
					out.write(buffer, 0, len);// 输出缓冲区的内容到浏览器，实现文件下载
				}
				out.close();// 关闭输出流
				alreadyCalledResponse = true;
			} else {
				qrs.setCode(1);
				qrs.setMsg(DataAccessObjects.SUCCESS_MESSAGE);
				result.put("Tables", ServletUtil.packString(ServletUtil
						.convertStreamToString(is, charSet)));
			}
			is.close(); // 关闭文件输入流
		} catch (Exception e) {// 数据库语句参数异常
			if((e.getMessage()).indexOf("12519")!=-1 || (e.getMessage()).indexOf("12518")!=-1){ //数据库连接上限导致的错误
				qrs.setMsg("服务繁忙，请稍后再试！");
			}else{
				qrs.setMsg(e.getMessage());
			}
			exceptionCatched = true; //告知finally代码块出现异常，需要输出结果
		} finally {
			if (!alreadyCalledResponse == true || exceptionCatched == true) {
				PrintWriter owOut = response.getWriter();
				json = gson.toJson(result); // 转化成json格式
				String jsoncallback = request.getParameter("jsoncallback"); // 获取jsoncallback
				if (ServletUtil.validate(jsoncallback)) { // 如果jsoncallback参数有效，则进行jsonp包装
					owOut.print(jsoncallback + "(" + json + ")");
				} else {
					owOut.print(json);
				}
				owOut.flush();
				owOut.close();
			}
		}
	}

	/**
	 * 非加密sql语句运行
	 * 
	 * @param request
	 *            由客户端送来服务端的请求
	 * @param response
	 *            由服务端送到客户端的反馈
	 * @throws IOException
	 */
	private void excuteSql(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		dao = DaoWebService.getDao(); // 实例化model层
		String mode = request.getParameter("g"); // 获取操作模式
		Map<String, Object> keyword = new LinkedHashMap<String, Object>();// 存放查询关键字
		if("38".equals(request.getParameter("xzu47"))){ //如果调用的是xzu47接口
			keyword.put("ID", request.getParameter("h")); // 获取数据库语句存放id<字段名，值>
		} else{ //调用了5c5接口
			keyword.put("GUID", request.getParameter("h")); // 获取数据库语句存放id<字段名，值>
		}
		String divide = request.getParameter("i"); // 获取分隔符
		String values = request.getParameter("j"); // 获取参数值
		String pageSize = request.getParameter("k"); // 获取分页大小
		String pageIndex = request.getParameter("l"); // 获取页码
		String sql = null;
		String json = null;
		QueryResult qrs = new QueryResult(); // 存储结果对象
		Map<String, Object> result = new LinkedHashMap<String, Object>();// 用于存储最终结果
		result.put("Result", qrs);// 存放Result
		Gson gson = new Gson(); // 实例化json转换工具
		try {
			sql = dao.query(sqlTable, keyword, sqlName); // 查询sql语句
			if ("1".equals(mode)) { // 确定模式
				if (ServletUtil.validate(values) && !"0".equals(values)) {
					if (ServletUtil.validate(divide)) {
						json = dao.excuteSqlSearch(sql, values.split(divide),
								pageSize, pageIndex,charset); // 有参数进行分割传入
					} else {
						String[] strArr = { values };
						json = dao.excuteSqlSearch(sql, strArr, pageSize,
								pageIndex,charset); // 有参数进行分割传入
					}
				} else {
					json = dao.excuteSqlSearch(sql, null, pageSize, pageIndex,charset); // 无参数直接传null
				}

			} else if ("2".equals(mode)) {// 确定模式
				json = dao.excuteSql(sql, values.split(divide)); // 传入dao执行
			}
		} catch (JdbcRuntimeException e) { // 发生异常
			qrs.setMsg(e.getMessage()); // 将异常信息写入要返回的结果码里
			json = gson.toJson(result); // 转换成json形式的字符串
		} catch (SQLException e) {
			if((e.getMessage()).indexOf("12519")!=-1 || (e.getMessage()).indexOf("12518")!=-1){ //数据库连接上限导致的错误
				qrs.setMsg("服务繁忙，请稍后再试！");
			}else{
				qrs.setMsg(e.getMessage());
			}
			json = gson.toJson(result); // 转换成json形式的字符串
		} finally {
			PrintWriter out = response.getWriter();
			String jsoncallback = request.getParameter("jsoncallback"); // 获取jsoncallback
			if (ServletUtil.validate(jsoncallback)) { // 如果jsoncallback参数有效，则进行jsonp包装
				out.print(jsoncallback + "(" + json + ")");
			} else {
				out.print(json);
			}
			out.flush();
			out.close();
		}
	}

	/**
	 * blob数据处理
	 * 
	 * @param request
	 *            由客户端送来服务端的请求
	 * @param response
	 *            由服务端送到客户端的反馈
	 * @throws IOException
	 */
	private void blobUpload(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		// 参数获取和变量初始化，以下方法对应url传参或者普通表单传参
		QueryResult qrs = new QueryResult(); // 存储结果对象
		dao = DaoWebService.getDao(); // 实例化model层
		Map<String, String> parameters = new LinkedHashMap<String, String>(); // 因为存在两种获取参数的方式，因此创建一个集合，将参数和参数名存入
		parameters.put("g", request.getParameter("g"));// 操作类型addOrUpdate
		parameters.put("h", request.getParameter("h"));// 普通字段名cloumnName
		parameters.put("i", request.getParameter("i"));// 分隔符divide
		parameters.put("j", request.getParameter("j"));// 普通字段值
		parameters.put("k", request.getParameter("k"));// blob字段名
		parameters.put("l", request.getParameter("l"));// blob类型
		parameters.put("o", request.getParameter("o"));// 主键值
		String primaryKeyName = request.getParameter("p"); // 主键名
		if (ServletUtil.validate(primaryKeyName)) { // 检查主键是否未设置
			primaryKeyName = "id";
		}
		parameters.put("p", primaryKeyName);// 主键名
		parameters.put("q", request.getParameter("q"));// 表名
		parameters.put("r", request.getParameter("r"));// 文件类型字段名
		parameters.put("s", request.getParameter("s"));// 文件名字段名

		Map<String, Object> properties = new LinkedHashMap<String, Object>();// 用于存储要更新或新增的属性名和值

		String fileName = "";// 文件名变量
		String mimeType = "";// mime类型变量
		InputStream is = null;// 文件流变量
		try {
			// 1.以下方法对应 multipart/form-data 请求，无法直接获取对应参数，使用遍历取值，将值放入集合
			// 2.完成文件上传和文字转流上传
			String contentType = request.getContentType(); // 获取Content-Type
			if ((contentType != null)
					&& (contentType.toLowerCase().startsWith("multipart/"))) { // 如果上传的是文件上传表单
				File tmpFile = new File(this.getServletContext().getRealPath(
						tempPath)); // 打开文件操作
				if (!tmpFile.exists()) { // 如果目录不存在
					tmpFile.mkdir(); // 创建一个新的目录
				}
				DiskFileItemFactory factory = new DiskFileItemFactory(); // 创建一个DiskFileItemFactory工厂
				factory.setSizeThreshold(sizeThreshold);// 设置缓冲区大小
				ServletFileUpload upload = new ServletFileUpload(factory); // 创建一个文件上传解析器
				upload.setHeaderEncoding(charset); // 解决上传文件名的中文乱码
				upload.setFileSizeMax(fileSizeMax);// 设置单个文件最大值
				upload.setSizeMax(sizeMax);// 设置总文件最大值
				if (!ServletFileUpload.isMultipartContent(request)) { // 判断提交上来的数据是否是上传表单的数据
					qrs.setMsg("提交的并非带文件的表单数据。"); // 返回结果信息
				} else {
					List<FileItem> list = upload.parseRequest(request); // 使用ServletFileUpload解析器解析上传数据
					for (FileItem item : list) {
						if ("n-file".equals(item.getFieldName())) {
							if (!item.isFormField()) { // 如果fileitem中封装的是否是文件
								fileName = item.getName(); // 获取文件名
								fileName = fileName.substring(fileName
										.lastIndexOf("\\") + 1); // 去除文件路径只保留文件名
								// String[] fileNameAndType =
								// fileName.split("\\.");// 分割文件名和类型
								mimeType = item.getContentType();// 获取mimetype
								is = item.getInputStream();// 获取文件输入流
								item.delete();// 删除临时文件
							} else {
								qrs.setMsg("提交的提交的数据类型中没有文件"); // 返回结果信息
							}
						} else {
							parameters.put(item.getFieldName(),
									item.getString(charset)); // 文件表单参数获取方式
						}
					}
					properties.put(parameters.get("k"), is); // 将读取文件的输入流存入集合
					properties.put(parameters.get("s"), fileName); // 将文件名加入集合
					properties.put(parameters.get("r"), mimeType); // 将文件mime类型加入集合
				}
			} else { // 如果提交的只是普通表单
				String blobContant = request.getParameter("n-file"); // 获取blob内容
				properties.put(parameters.get("k"), new ByteArrayInputStream(
						blobContant.getBytes(charset))); // 存入blob字段名和转换成流的blob内容
			}

			// 普通字段处理
			String cloumnName = parameters.get("h"); // 普通字段名
			String divide = parameters.get("i"); // 分隔符
			String cloumnValues = parameters.get("j"); // 普通字段值
			String[] cloumnsName = cloumnName.split(","); // 分割数据
			String[] cloumnsValue = cloumnValues.split(divide); // 分割数据
			int length = cloumnsName.length; // 获取数组长度
			for (int i = 0; i < length; i++) {
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd/HH:mm:ss");
				Date date = null;
				try{
					date = format.parse(cloumnsValue[i]);//转换成日期
					properties.put(cloumnsName[i], date);
				}
				catch(Exception e){
					properties.put(cloumnsName[i], cloumnsValue[i]);// 存入普通字段名和值
				}
			}

			// 判断操作，调用对应的dao层方法
			if ("1".equals(parameters.get("g"))) { // 判断是增加操作还是修改操作
				if (ServletUtil.validate(parameters.get("o"))) { // 如果主键值存在且有效
					properties.put(parameters.get("p"), parameters.get("o")); // 将主键字段名和值放入
				}
				if (dao.add(parameters.get("q"), properties)) { // 执行新增数据操作
					qrs.setCode(1); // 返回结果码
					qrs.setMsg(DataAccessObjects.SUCCESS_MESSAGE); // 返回结果信息
				}
			} else {
				Map<String, Object> keyword = new LinkedHashMap<String, Object>(); // 用于存放主键，执行更新操作所需
				keyword.put(parameters.get("p"), parameters.get("o")); // 将主键字段名和值放入
				if (dao.update(parameters.get("q"), properties, keyword)) { // 执行修改数据操作
					qrs.setCode(1); // 返回结果码
					qrs.setMsg(DataAccessObjects.SUCCESS_MESSAGE); // 返回结果信息
				}

			}
		} catch (FileUploadBase.FileSizeLimitExceededException e) {// 文件超长异常
			qrs.setMsg("单个文件超出最大值！");
		} catch (FileUploadBase.SizeLimitExceededException e) {// 临时文件夹满异常
			qrs.setMsg("上传文件的总的大小超出限制的最大值！");
		} catch (FileUploadException e) {// 文件上传异常
			qrs.setMsg(e.getMessage());
		} catch (JdbcRuntimeException e) {// 数据库语句参数异常
			qrs.setMsg(e.getMessage());
		} catch (SQLException e) {// 数据库语句参数异常
			if((e.getMessage()).indexOf("12519")!=-1 || (e.getMessage()).indexOf("12518")!=-1){ //数据库连接上限导致的错误
				qrs.setMsg("服务繁忙，请稍后再试！");
			}else{
				qrs.setMsg(e.getMessage());
			}
		} catch (Exception e) {
			qrs.setMsg(e.getMessage());
		}
		Map<String, Object> result = new LinkedHashMap<String, Object>();// 用于存储最终结果
		result.put("Result", qrs);// 存放Result
		Gson gson = new Gson(); // 实例化json转换工具
		String json = gson.toJson(result); // 转化成json格式
		PrintWriter out = response.getWriter();
		out.print(json);
		out.flush();
		out.close();
	}
	
	private void describeTable(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String pageSize = request.getParameter("k"); // 获取每页大小
		String pageIndex = request.getParameter("l"); // 获取需要查询的是第几页
		String tableName = request.getParameter("j"); // 获取用户名
		QueryResult qrs = new QueryResult(); // 存储结果对象
		Map<String, Object> result = new LinkedHashMap<String, Object>();// 用于存储最终结果
		result.put("Result", qrs);// 存放Result
		Gson gson = new Gson(); // 实例化json转换工具
		String json = null;
		dao = DaoWebService.getDao(); // 实例化model层
		try {
			String[] tableNames = new String[2];
			if(tableName.indexOf(".")==-1){
				qrs.setMsg("查询信息不完整： 表空间.表名——"+tableName);
			} else {
				tableNames = tableName.split("\\.");
				Tables tables = dao.describeTable(tableNames);	//取出表信息
				if(tables.getRecords().size()!=0){	//不为0，则开始分页
					int pageSizeInt = Integer.parseInt(pageSize);// 转换参数
					int pageIndexInt = Integer.parseInt(pageIndex);// 转换参数
					int sum = tables.getRecords().size();
					int start = (pageIndexInt-1)*pageSizeInt+1;
					int end = pageIndexInt*pageSizeInt;
					Tables[] tableRs = new Tables[2];// 存放表信息
					tableRs[0] = new Tables();// 第一个表信息用于存放分页情况
					tableRs[0].setTableName("tb1");
					List<String> fields = new ArrayList<String>();// 记录总条数，每页大小，页码
					fields.add("sum"); // 总计记录条数
					fields.add("size"); // 每页大小
					fields.add("indx"); // 当前页码
					tableRs[0].setFields(fields);// 放入表中
					List<List<Object>> records1 = new ArrayList<List<Object>>(); // 存储记录，每次加入一整条记录
					List<Object> splitData = new ArrayList<Object>();// 存放分页数据
					splitData.add(sum);// 获取该查询总记录数
					splitData.add(pageSize);
					splitData.add(pageIndex);
					records1.add(splitData);// 组装
					tableRs[0].setRecords(records1);// 放入表中
					if(sum<end){
						end=sum;
					}
					if(sum>=start){	//分页
						List<List<Object>> records2 = new ArrayList<List<Object>>();
						for(int i=start-1;i<end;i++){
							records2.add(tables.getRecords().get(i));
						}
						tables.setRecords(records2);
						tableRs[1] = tables;
					} else {
						tableRs[1] = null;
					}

					result.put("Tables", tableRs);
					qrs.setCode(1);
					qrs.setMsg(DataAccessObjects.SUCCESS_MESSAGE);
				} else {
					qrs.setMsg("查无数据，请检查参数- pageSize:" + pageSize + "，pageIndex:"
						+ pageIndex + "，tableName:"
						+ tableName);
				}
			}
			json = gson.toJson(result);
		} catch (NumberFormatException e) {
			qrs.setMsg("输入参数错误- pageSize:" + pageSize + "，pageIndex:"
					+ pageIndex);// 存储出错原因
			json = gson.toJson(result);
		}
		PrintWriter out = response.getWriter();
		String jsoncallback = request.getParameter("jsoncallback"); // 获取jsoncallback
		if (ServletUtil.validate(jsoncallback)) { // 如果jsoncallback参数有效，则进行jsonp包装
			out.print(jsoncallback + "(" + json + ")");
		} else {
			out.print(json);
		}
		out.flush();
		out.close();
	}
}
