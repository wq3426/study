<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!-- 有些版本的jsp默认会不支持EL表达式，如果el表达式不能使用，可添加如下标签设置启用EL表达式 -->
<%@ page isELIgnored="false" %>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>@ControllerAdvice Demo</title>
</head>
<body>
	<pre>
	<!-- Maven的页面一般放在src/main/webapp/WEB-INF下，此处是按照SpringBoot的放置习惯来放置页面的 -->
		${errorMessage}
	</pre>
</body>
</html>