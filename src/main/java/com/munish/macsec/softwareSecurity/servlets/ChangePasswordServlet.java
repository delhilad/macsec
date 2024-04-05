package com.munish.macsec.softwareSecurity.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import com.munish.macsec.softwareSecurity.db.DatabaseUtil;

public class ChangePasswordServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<!DOCTYPE html>\n");
            htmlResponse.append("<html>\n");
            htmlResponse.append("<head>\n");
            htmlResponse.append("    <meta charset=\"UTF-8\">\n");
            htmlResponse.append("    <title>Change Password</title>\n");
            htmlResponse.append("</head>\n");
            htmlResponse.append("<body>\n");
            htmlResponse.append("    <table align=\"center\">\n");
            htmlResponse.append("        <tr><td colspan=\"2\"><h2>Change Password</h2></td></tr>\n");
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
                htmlResponse.append("        <tr><td colspan=\"2\"><p style=\"color: red;\">").append(errorMessage).append("</p></td></tr>\n");
            }
            htmlResponse.append("        <tr><td colspan=\"2\">\n");
            htmlResponse.append("            <form action=\"changePassword\" method=\"post\">\n");
            htmlResponse.append("                <label for=\"currentPassword\">Current Password:</label><br>\n");
            htmlResponse.append("                <input type=\"password\" id=\"currentPassword\" name=\"currentPassword\" required><br>\n");
            htmlResponse.append("                <label for=\"newPassword\">New Password:</label><br>\n");
            htmlResponse.append("                <input type=\"password\" id=\"newPassword\" name=\"newPassword\" required><br>\n");
            htmlResponse.append("                <input type=\"submit\" value=\"Change Password\">\n");
            htmlResponse.append("            </form>\n");
            htmlResponse.append("        </td></tr>\n");
            htmlResponse.append("        <tr><td colspan=\"2\"><a href=\"Feedback\">Feedback</a></td></tr>\n");
            htmlResponse.append("        <tr><td><a href=\"logout.jsp\">Logout</a></td><td><a href=\"#\" onclick=\"window.location.href='FindUserHome'\">Cancel</a></td></tr>\n");
            htmlResponse.append("    </table>\n");
            htmlResponse.append("</body>\n");
            htmlResponse.append("</html>");

            // Send HTML response
            response.setContentType("text/html");
            response.getWriter().println(htmlResponse);
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");

            boolean passwordChanged = false;
            try {
                passwordChanged = DatabaseUtil.changePassword(username, currentPassword, newPassword);
            } catch (Exception e) {
                passwordChanged = false;
            }
            if (passwordChanged) {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            } else {
                request.setAttribute("errorMessage", "Incorrect current password");
                doGet(request, response);
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
