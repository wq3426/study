<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>文件上传页面</title>
</head>
<body>
	<pre>
	<!-- Maven的页面一般放在src/main/webapp/WEB-INF下，此处是按照SpringBoot的放置习惯来放置页面的 -->
	</pre>
	
	<!-- enctype="multipart/form-data" 文件上传必须添加的属性 -->
	<div class="upload">
		<form action="upload" enctype="multipart/form-data" method="post">
			<input type="file" name="file"/>
			<input type="submit" value="上传"/>
		</form>
	</div>
</body>
</html>