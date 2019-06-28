<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>抢课结果</title>
	<link rel="stylesheet" href="bootstrap.min.css">
</head>
<body>
	<h1>您的学号是:<c:out value="${sid}"></c:out>
		<span class="badge badge-primary"><c:out value="${result}">!</c:out></span>
	</h1>
<script src="bootstrap.min.js"></script>
</body>
</html>