package web;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.DaoWebService;
import web.util.ServletUtil;

import com.google.gson.Gson;

import dao.DataAccessObjects;
import dao.bean.QueryResult;
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
		if ("63".equals(request.getParameter("ccu70"))) {// 如果不为空则调用上传方法
//			blobUpload(request, response);
		} else if ("39".equals(request.getParameter("dm8it"))) {// 如果不为空则使用下载方法
//			blobDownload(request, response);
		} else if ("38".equals(request.getParameter("xzu47"))) {// 如果不为空则使用执行语句方法
			excuteSql(request, response);
		} else if ("5c5".equals(request.getParameter("ea7"))) {// 如果不为空则使用执行语句方法
			excuteSql(request, response);
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
		} catch (JdbcRuntimeException e) {
			qrs.setMsg(e.getMessage()); // 将异常信息写入要返回的结果码里
			json = gson.toJson(result); // 转换成json形式的字符串
		} catch (SQLException e) {
			qrs.setMsg(e.getMessage()); // 将异常信息写入要返回的结果码里
			json = gson.toJson(result); // 转换成json形式的字符串
		} finally {
			PrintWriter out = response.getWriter();
			String jsoncallback = request.getParameter("jsoncallback"); // 获取jsoncallback
			if (ServletUtil.validate(jsoncallback)) { // 如果jsoncallback参数有效，则进行jsonp包装
				response.setContentType("application/javascript");
				out.print(jsoncallback + "(" + json + ")");
			} else {
				response.setContentType("application/json");
				out.print(json);
			}
			out.flush();
			out.close();
		}
	}
	
}
