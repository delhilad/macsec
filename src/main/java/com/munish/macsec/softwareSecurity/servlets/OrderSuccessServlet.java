package com.munish.macsec.softwareSecurity.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class OrderSuccessServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null && session.getAttribute("order") != null) {
            String username = (String) session.getAttribute("username");
            String orderNumber = (String) session.getAttribute("order");

            response.setContentType("text/html");
            response.getWriter().println("<!DOCTYPE html>");
            response.getWriter().println("<html>");
            response.getWriter().println("<head>");
            response.getWriter().println("<meta charset=\"UTF-8\">");
            response.getWriter().println("<title>Order Success</title>");
            response.getWriter().println("</head>");
            response.getWriter().println("<body>");
            response.getWriter().println("<h1>Order Successful</h1>");
            response.getWriter().println("<p>Hello, " + username + "!</p>");
            response.getWriter().println("<p>Your order has been successfully placed. Your order number is: " + orderNumber + "</p>");
            response.getWriter().println("<p><a href=\"" + request.getContextPath() + "/userHome\">Resume Shopping</a></p>");
            response.getWriter().println("<br><a href=logout.jsp>Logout</a>\n");
            response.getWriter().println(("<br><a href=ChangePassword>Change Password</a>\n"));
            response.getWriter().println("</body>");
            response.getWriter().println("</html>");
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
