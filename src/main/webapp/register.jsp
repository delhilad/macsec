<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Registration</title>
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        form {
            text-align: center;
        }
    </style>
</head>
<body>
        <h1>User Registration</h1><br>
        <form action="RegisterServlet" method="post">
            <input type="hidden" name="action" value="register">
            First Name: <input type="text" name="firstName" required><br>
            Last Name: <input type="text" name="lastName" required><br>
            Email: <input type="email" name="email" required><br>
            Password: <input type="password" name="password" required><br>
            <input type="submit" value="Register">
        </form><br>
        <div>
        <% Object error = request.getAttribute("error");
         System.out.println(error);%>
        <% if (error != null) { %>
        <div style="color: red;">
            <%= error %>
        </div>
        <% } %>    </div>
</body>
</html>
