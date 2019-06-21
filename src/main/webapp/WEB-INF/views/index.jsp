<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=utf-8" %>
<!DOCTYPE html>
<html>
<head>
	<title>所有课程</title>
	<link rel="stylesheet" href="bootstrap.min.css">
</head>
<body>

<table class="table table-light">
	<thead>
		<tr>
			<th scope="col">#</th>
			<th scope="col">课程名</th>
			<th scope="col">学期</th>
			<th scope="col">年份</th>
			<th scope="col">类型</th>
			<th scope="col">课容量</th>
			<th scope="col">抢课</th>
		</tr>
	</thead>
	<c:forEach items="${courses}" begin="0" var="course">
		<tr>
			<th scope="row"><c:out value="${course.cid}"></c:out></th>
			<td><c:out value="${course.cname}"></c:out></td>
			<td><c:out value="${course.courseterm}"></c:out></td>
			<td><c:out value="${course.courseyear}"></c:out></td>
			<td><c:out value="${course.coursetype}"></c:out></td>
			<td><c:out value="${course.coursevolume}"></c:out></td>
			<td><a href="/distributedCourse/ZookeeperSEKILL?cid=<c:out value="${course.cid}"></c:out>">LET'S GO!!</a>
			</td>
		</tr>
	</c:forEach>
</table>
<script src="bootstrap.min.js"></script>
</body>
</html>