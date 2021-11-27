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
    <form action="/users/create" method="post">
        <label for="firstName">First name:</label>
        <input type="text" name="firstName" id="firstName"><br>

        <label for="lastName">Last name:</label>
        <input type="text" name="lastName" id="lastName"><br>

        <label for="phone">Phone:</label>
        <input type="text" name="phone" id="phone"><br>

        <label for="email">E-mail:</label>
        <input type="text" name="email" id="email"><br>

        <label for="password">Password:</label>
        <input type="password" name="password" id="password"><br>

        <input type="submit" name="Send">
    </form>
</body>
</html>