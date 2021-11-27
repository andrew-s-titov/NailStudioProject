<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 2021
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Profile</title>
</head>
<body>
Hello, ${user.firstName}!

<c:if test="${exists}">
    <form action="/users/get/${user.userId}" method="get">
        <button>Show personal information</button>
    </form>
</c:if>

<c:if test="${isClient}">
    <form action="/records/get/for_client/${user.userId}" method="get">
        <button>Show appointments</button>
    </form>
</c:if>

<c:if test="${isStaff}">
    <form action="/records/get/for_staff/${user.userId}" method="get">
        <button>Show tasks</button>
    </form>
</c:if>

<c:if test="${isAdmin}">
    <form action="/admin" method="get">
        <button>Admin options</button>
    </form>
</c:if>

</body>
</html>
