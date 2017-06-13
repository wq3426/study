<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- 有些版本的jsp默认会不支持EL表达式，如果el表达式不能使用，可添加如下标签设置启用EL表达式 -->
<%@ page isELIgnored="false" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SSE Demo页面</title>
</head>
<body>
	<pre>
	<!-- Maven的页面一般放在src/main/webapp/WEB-INF下，此处是按照SpringBoot的放置习惯来放置页面的 -->
	</pre>
	
	<div id="msgFromPush"></div>
	
	<script type="text/javascript" src="assets/js/jquery-1.3.2.js"></script>
	
	<!-- 
		1.EventSource 对象只有新式浏览器(Chrome、Firefox等)才有，EventSource是SSE的客户端
		2.SSE客户端向服务器端发送请求，请求的路径名为"push"：http://localhost:8080/mvc/push
		3.添加SSE客户端监听，在此获得服务器端推送的消息
	 -->
	<script type="text/javascript">
		if(!!window.EventSource){// 1
			var source = new EventSource('push');// 2
			s = '';
			source.addEventListener('message', function(e){// 3
				s += e.data + "<br/>";
				$("#msgFromPush").html(s);
			});
			
			source.addEventListener('open', function(e){
				console.log("连接打开.");
			}, false);
			
			source.addEventListener('error', function(e){
				if(e.readyState == EventSource.CLOSED){
					console.log("连接关闭");
				}else{
					console.log(e.readyState);
				}
			}, false);
		}else{
			console.log("你的浏览器不支持SSE");
		}
	</script>
</body>
</html>