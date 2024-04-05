package com.munish.macsec.softwareSecurity.servlets;

import com.munish.macsec.softwareSecurity.db.DatabaseUtil;
import com.munish.macsec.softwareSecurity.db.PendingRequest;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PendingRequestsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            List<PendingRequest> pendingRequests = DatabaseUtil.getPendingRequests();

            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<!DOCTYPE html>\n");
            htmlResponse.append("<html>\n");
            htmlResponse.append("<head>\n");
            htmlResponse.append("    <meta charset=\"UTF-8\">\n");
            htmlResponse.append("    <title>Pending Requests</title>\n");
            htmlResponse.append("</head>\n");
            htmlResponse.append("<body>\n");
            htmlResponse.append("<table align=\"center\" border=\"1\">\n");
            htmlResponse.append("    <tr>\n");
            htmlResponse.append("        <th colspan=\"3\">Pending Requests</th>\n");
            htmlResponse.append("    </tr>\n");
            htmlResponse.append("    <tr>\n");
            htmlResponse.append("        <th>Email</th>\n");
            htmlResponse.append("        <th>Requested On</th>\n");
            htmlResponse.append("        <th>Action</th>\n");
            htmlResponse.append("    </tr>\n");
            for (PendingRequest req : pendingRequests) {
                htmlResponse.append("    <tr>\n");
                htmlResponse.append("        <td>").append(req.getEmail()).append("</td>\n");
                htmlResponse.append("        <td>").append(req.getRequestedOn()).append("</td>\n");
                htmlResponse.append("        <td>\n");
                htmlResponse.append("            <form action=\"PendingRequests\" method=\"post\">\n");
                htmlResponse.append("                <input type=\"hidden\" name=\"email\" value=\"").append(req.getEmail()).append("\">\n");
                htmlResponse.append("                <input type=\"hidden\" name=\"action\" value=\"approve\">\n");
                htmlResponse.append("                <input type=\"submit\" value=\"Approve\">\n");
                htmlResponse.append("            </form>\n");
                htmlResponse.append("            <form action=\"PendingRequests\" method=\"post\">\n");
                htmlResponse.append("                <input type=\"hidden\" name=\"email\" value=\"").append(req.getEmail()).append("\">\n");
                htmlResponse.append("                <input type=\"hidden\" name=\"action\" value=\"reject\">\n");
                htmlResponse.append("                <input type=\"submit\" value=\"Reject\">\n");
                htmlResponse.append("            </form>\n");
                htmlResponse.append("        </td>\n");
                htmlResponse.append("    </tr>\n");
            }
            htmlResponse.append("    <tr>\n");
            htmlResponse.append("        <td><a href=Admin>Home</a></td>\n");
            htmlResponse.append("        <td><a href=ChangePassword>Change Password</a></td>\n");
            htmlResponse.append("        <td><a href=Feedback>Feedback</a></td>\n");
            htmlResponse.append("    </tr>\n");
            htmlResponse.append("</table>\n");
            htmlResponse.append("</body>\n");
            htmlResponse.append("</html>");

            response.setContentType("text/html");
            response.getWriter().println(htmlResponse.toString());
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action != null) {
            if (action.equals("approve")) {
                String email = request.getParameter("email");
                try {
                    DatabaseUtil.promote(email);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else if (action.equals("reject")) {
                String email = request.getParameter("email");
                try {
                    DatabaseUtil.reject(email);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/PendingRequests");
    }
}
