package com.munish.macsec.softwareSecurity.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AdminServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            response.setContentType("text/html");
            response.getWriter().println("<!DOCTYPE html>");
            response.getWriter().println("<html>");
            response.getWriter().println("<head>");
            response.getWriter().println("<meta charset=\"UTF-8\">");
            response.getWriter().println("<title>Admin Dashboard</title>");
            response.getWriter().println("<style>");
            response.getWriter().println("body {");
            response.getWriter().println("    display: flex;");
            response.getWriter().println("    justify-content: center;");
            response.getWriter().println("    align-items: center;");
            response.getWriter().println("    height: 100vh;");
            response.getWriter().println("    margin: 0;");
            response.getWriter().println("}");
            response.getWriter().println("table {");
            response.getWriter().println("    margin-top: 50px; /* Adjust margin as needed */");
            response.getWriter().println("    border-collapse: collapse;");
            response.getWriter().println("    text-align: center;");
            response.getWriter().println("}");
            response.getWriter().println("th, td {");
            response.getWriter().println("    padding: 10px;");
            response.getWriter().println("    border: 1px solid #ddd;");
            response.getWriter().println("}");
            response.getWriter().println("</style>");
            response.getWriter().println("</head>");
            response.getWriter().println("<body>");
            response.getWriter().println("<table>");
            response.getWriter().println("<tr>");
            response.getWriter().println("<th colspan=\"2\">Welcome Admin!</th>");
            response.getWriter().println("</tr>");
            response.getWriter().println("<tr>");
            response.getWriter().println("<td><a href=\"AddUserServlet\">Add User</a></td>");
            response.getWriter().println("<td><a href=\"PendingRequests\">Pending Requests</a></td>");
            response.getWriter().println("</tr>");
            response.getWriter().println("<tr>");
            response.getWriter().println("<td><a href=\"logout.jsp\">Logout</a></td>");
            response.getWriter().println("<td><a href=\"ChangePassword\">Change Password</a></td>");
            response.getWriter().println("</tr>");
            response.getWriter().println("</tr>");
            response.getWriter().println("<tr>");
            response.getWriter().println("<td colspan=\"2\"><a href=\"Feedback\">Feedback</a></td>");
            response.getWriter().println("</tr>");
            response.getWriter().println("</table>");
            response.getWriter().println("</body>");
            response.getWriter().println("</html>");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
