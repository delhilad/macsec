<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Login</title>
    <style>
        body {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        table {
            margin-top: 50px; /* Adjust margin as needed */
            border-collapse: collapse;
            text-align: center;
        }
        th, td {
            padding: 10px;
            border: 1px solid #ddd;
        }
        form {
            text-align: center;
        }
        #errorMessage {
            color: red;
        }
    </style>
</head>
<body>
    <table>
        <tr>
            <td colspan="2"><h1>User Login</h1></td>
        </tr>
        <tr>
            <td colspan="2">
                <%
                Object error = session.getAttribute("error");
                if (error != null) {
                %>
                <div id="errorMessage">
                    <%= error %>
                </div>
                <%}%>
            </td>
        </tr>
        <tr>
            <td colspan="2">
                <form action="LoginServ" method="post">
                    <input type="hidden" name="action" value="login">
                    Username: <input type="text" name="username"><br>
                    Password: <input type="password" name="password"><br>
                    <input type="submit" value="Login">
                </form>
            </td>
        </tr>
        <tr>
            <td colspan="2"><p><a href="register.jsp">Register here</a></p></td>
        </tr>
    </table>
</body>
</html>
