<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>My JSP 'index.jsp' starting page</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
</head>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/jquery.min.js"></script>
<script type="text/javascript">
    $(function(){
        //按钮单击时执行
        $("#test2").click(function(){
        	var a= $.ajax({
                //提交数据的类型 POST GET
                type: "POST",
                crossDomain: true,
                //提交的网址
                url:"http://193.100.100.57:8080/subsystem/sc/metadatam/MetaDataM8367?ea7=5c5&g=1&h=d31e5f5b-5700-4910-8dfc-8e79261d3d53&k=10&l=1&jsoncallback=?",
                //提交的数据
                data: {
                	i: 	'%E2%99%80',
                    j:	'STCD%E2%99%80STIME%E2%99%80sc_sediment_n%E2%99%8014%E2%99%801%E2%99%8020160401%E2%99%8020160731'
                },
                //返回数据的格式
                scriptCharset: 'gbk',
                datatype: "json",//"xml", "html", "script", "json", "jsonp", "text".
                //在请求之前调用的函数
                //成功返回之后调用的函数             
                success: function (data) {
                    alert(data);
                },
                //调用出错执行的函数
                error: function () {
                	 alert("数据加载失败！");
                }
            });

         });
    });
    $(function(){
        //按钮单击时执行
        $("#testAjax").click(function(){
        	var a= $.ajax({
                //提交数据的类型 POST GET
                type: "POST",
                crossDomain: true,
                //提交的网址
                url:"http://193.100.100.57:8080/subsystem/sc/metadatam/MetaDataM8367",
                //提交的数据
                data: {
                    ccu70:"63",
                    i: "★",
                    h: " title,sendtime, sendmechanism, sendstatus, author, authorip, createtime, pnum, sendway",
                    g: "1",
                    j: "555555★2018-01-09/10:05:57★即时发送★待审核★超级管理员★59.56.160.114★2018-01-09/10:05:57★5★增量发布",
                    k: "content",
                    l: "1",
                    o: "308",
                    p: "id",
                    q: "scsjzx.sc_informanage",
                    r: "f",
                    s: "d",
                    "n-file": "次会议的主要任务是表决市十五届人大财政经济委员会主任委员、副主任委员、委员正式人选名单，选举福州市的省十三届人大代表，选举市监察委员会济委员会主任委员、副主任委员、委员人选名单。代表们以热烈的掌声向当选的同志表示祝次会议的主要任务是表决市十五届人大财政经济委员会主任委员、副主任委员、委员正式人选名单，选"
                },
                //返回数据的格式
                datatype: "json",//"xml", "html", "script", "json", "jsonp", "text".
                //在请求之前调用的函数
                //成功返回之后调用的函数             
                success: function (data) {
                    alert(data);
                },
                //调用执行后调用的函数
                complete: function (XMLHttpRequest, textStatus) {
                    alert(textStatus);
                },
                //调用出错执行的函数
                error: function () {
                	
                }
            });

         });
    });
</script>    
<body>
	<form
		action="${pageContext.request.contextPath}/sc/metadatam/MetaDataM8367?ccu70=63&g=2&h=a,b&i=%FE%FE&j=do%FE%FEwhile&k=g&l=1&r=d&s=f"
		enctype="multipart/form-data" method="post">
		上传文件：<input type="file" name="n-file"><br />
		主键值：	<input type="text" name="o"><br />
		主键字段名：<input type="text" name="p"><br />
		表名：		<input type="text" name="q"><br />
			<input type="submit" value="提交">
	</form>
	<a href="${pageContext.request.contextPath}/sc/metadatam/MetaDataM8367?dm8it=39&g=testblob&h=g&i=id&j=1&k=d&m=f&p=1&o=gbk">下载提交的文件</a>
	<form
		action="${pageContext.request.contextPath}/sc/metadatam/MetaDataM8367?ccu70=63&g=1&h=AUTHOR,TITLE&i=%FE%FE&j=TEST%FE%FE2012-03-12/12:35:45&k=CONTENT&l=2"
		method="post">
		上传文字：<input type="text" name="n-file"><br />
		主键值：	<input type="text" name="o"><br />
		主键字段名：<input type="text" name="p"><br />
		表名：		<input type="text" name="q"><br />
			<input type="submit" value="提交">
	</form>
	
	<a href="http://193.100.100.57:8080/subsystem/sc/metadatam/MetaDataM8367?dm8it=39&g=WENZHANG&h=CONTENT&i=ID&j=2250&p=2&l=text/plain&n=text.txt&o=gbk&jsoncallback=null">直接查看提交的文字</a>
	<a href="${pageContext.request.contextPath}/sc/metadatam/MetaDataM8367?dm8it=39&g=WENZHANG&h=CONTENT&i=ID&j=3302&p=1&l=text/plain&n=text.txt&o=gbk">下载提交的文字</a><br><br><br>
	
	<a>查询用户表操作操作</a>
	<form
		action="${pageContext.request.contextPath}/sc/metadatam/MetaDataM8367?xzu47=38&g=1"
		method="post">
		sqlid：<input type="text" name="h"><br />
		分隔符号：<input type="text" name="i"><br />
		查询参数：<input type="text" name="j"><br />
		每页几条：<input type="text" name="k"><br />
		第几页：<input type="text" name="l"><br />
			<input type="submit" value="查询">
	</form>
	
	<a>新增操作</a>
	<form
		action="${pageContext.request.contextPath}/sc/metadatam/MetaDataM8367?xzu47=38&g=2"
		method="post">
		sqlid：<input type="text" name="h"><br />
		新增参数：<input type="text" name="j"><br />
		分隔符号：<input type="text" name="i"><br />
			<input type="submit" value="新增">
	</form>
	<a href="http://localhost:8080/subsystem/sc/metadatam/MetaDataM8367?aau68=61&k=100&l=1&j=FJSW">查询表信息</a><br>
	<a href=
	"http://193.100.100.65/subsystem/sc/metadatam/MetaDataM8367?ccu70=63&g=2&i=※&h=rid&j=13&k=configstr&n-file=1♂♂♂1♂12♂♂♂schyfj.SC_QAOBB_N♂pname♂♀1♂♂♂2♂1♂♂♂schyfj.SC_QAOBB_N♂sarea♂♀1♂♂♂2♂14♂♂♂schyfj.SC_QAOBB_N♂tasktime♂♀1♂♂♂3♂1♂♂♂schyfj.SC_QAOBB_N♂sunit♂♀1♂♂♂3♂8♂♂♂schyfj.SC_QAOBB_N♂ounit♂♀1♂♂♂3♂14♂♂♂schyfj.SC_QAOBB_N♂ftime♂♀2♂6♂0♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂section♂♀2♂6♂1♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂tidearea♂♀2♂6♂2♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂stcd♂♀2♂6♂3♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂plgtd♂♀2♂6♂4♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂plttd♂♀2♂6♂5♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂slgtd♂♀2♂6♂6♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂slttd♂♀2♂6♂7♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂locate♂♀2♂6♂8♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂stime♂♀2♂6♂9♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂btype♂♀2♂6♂10♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂snum♂♀2♂6♂11♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂acreage♂♀2♂6♂12♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂thickness♂♀2♂6♂13♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂chinanm♂♀2♂6♂14♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂latinnm♂♀2♂6♂15♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂num♂♀2♂6♂16♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂density♂♀2♂6♂17♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂weight♂♀2♂6♂18♂♂♂0♂填报人：♂schyfj.SC_QAOBB_N♂biomass♂♀3♂填报人：♂0♂0♂1♂♂♂schyfj.SC_QAOBB_N♂former♂♀3♂填报人：♂0♂0♂5♂♂♂schyfj.SC_QAOBB_N♂checker♂♀3♂填报人：♂0♂0♂9♂♂♂schyfj.SC_QAOBB_N♂auditer♂&l=2&o=2&p=id&q=schyfj.sc_excelimportconfig&jsoncallback=?"
	>GO</a><br>
	
	<button id="testAjax" type="button">Ajax</button>
	<button id="test2" type="button">go</button>
	
	<img alt="" src="http://193.100.100.57:8080/subsystem/sc/metadatam/MetaDataM8367?dm8it=39&g=fjsc.sc_finfo&h=content&i=id&j=209&k=mimetype&m=fname&p=1">
	
</body>
</html>
