<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>servlet3.0+ async 支持</title>
</head>
<body>
	<pre>
	<!-- Maven的页面一般放在src/main/webapp/WEB-INF下，此处是按照SpringBoot的放置习惯来放置页面的 -->
	</pre>
	
	<script type="text/javascript" src="assets/js/jquery-1.3.2.js"></script>
	
	<!-- 
		1.页面打开后就向后台发送请求
		2.使用jQuery的get请求请求后台(请求的路径名为"defer")，在控制台输出服务端推送的数据
		3.一次请求完成后再次向后台发送请求
	 -->
	<script type="text/javascript">
	
		deferred();// 1
	
		function deferred(){
			$.get('defer', function(data){
				console.log(data);// 2
				deferred();// 3
			});
		}
	</script>
</body>
</html>