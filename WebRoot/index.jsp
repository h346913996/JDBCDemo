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
	<a>查询用户表操作操作</a>
	<form
		action="${pageContext.request.contextPath}/sc/metadatam/MetaDataM8367?ea7=5c5"
		method="post">
		操作类型：<input type="checkbox" name="jsoncallback" value="?">JSONP<br />
		操作类型：<input type="radio" name="g" value="1">查询<input type="radio" name="g" value="2">执行<br />
		操作口令：<input type="text" name="h"><br />
		分割符号：<input type="text" name="i"><br />
		查询参数：<input type="text" name="j"><br />
		每页几条：<input type="text" name="k"><br />
		第几页：<input type="text" name="l"><br />
			<input type="submit" value="查询">
	</form>
	
	
	<button id="testAjax" type="button">Ajax</button>
	<button id="test2" type="button">Ajax2</button>

	
</body>
</html>
