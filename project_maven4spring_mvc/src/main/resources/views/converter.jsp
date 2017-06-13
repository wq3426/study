<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>自定义converter页面</title>
</head>
<body>
	<pre>
	<!-- Maven的页面一般放在src/main/webapp/WEB-INF下，此处是按照SpringBoot的放置习惯来放置页面的 -->
	</pre>
	
	<div id="resp"></div>
	<input type="button" onclick="req2();" value="请求"/>
	
	<script type="text/javascript" src="assets/js/jquery-1.3.2.js"></script>
	
	<script type="text/javascript">
		function req(){
			$.ajax({
				url : "convert",
				type : "POST",
				data : "1-wq",
				contentType : "application/x-wq",
				success : function(data){
					$("#resp").html(data);
				},
				error : function(){
					alert("error");
				}
			});
		}
		
		var data = {id : "111", name : "wq"};
		
		function req2(){
			$.ajax({
				url : "anno/obj2",
				type : "POST",
				//data : JSON.stringify(data),
				data : data,
				//contentType : "application/x-www-form-urlencoded",
				success : function(data){
					$("#resp").html(data);
				},
				error : function(){
					alert("error");
				}
			});
		}
	</script>
</body>
</html>