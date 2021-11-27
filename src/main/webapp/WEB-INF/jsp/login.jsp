<%--
  Created by IntelliJ IDEA.
  User: User
  Date: 2021
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <form action="/login" method="post">
        <label for="login">Login (phone number):</label>
        <input type="text" name="phone" id="login"><br>
        <label for="password">Password:</label>
        <input type="password" name="password" id="password"><br>
        <input type="submit" name="Send">
        <input type="reset" name="Reset">
    </form>
</body>
</html>