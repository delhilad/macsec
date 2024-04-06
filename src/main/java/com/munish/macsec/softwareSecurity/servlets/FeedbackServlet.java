package com.munish.macsec.softwareSecurity.servlets;

import com.munish.macsec.softwareSecurity.db.Feedback;
import com.munish.macsec.softwareSecurity.db.DatabaseUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FeedbackServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<!DOCTYPE html>\n");
            htmlResponse.append("<html>\n");
            htmlResponse.append("<head>\n");
            htmlResponse.append("    <meta charset=\"UTF-8\">\n");
            htmlResponse.append("    <title>Feedback</title>\n");
            htmlResponse.append("</head>\n");
            htmlResponse.append("<body>\n");
            htmlResponse.append("<table align=\"center\" border=\"1\">\n");
            htmlResponse.append("    <tr>\n");
            htmlResponse.append("        <th colspan=\"2\">Feedback</th>\n");
            htmlResponse.append("    </tr>\n");
            htmlResponse.append("    <tr>\n");
            htmlResponse.append("        <td colspan=\"2\">\n");
            htmlResponse.append("            <form action=\"Feedback\" method=\"post\">\n");
            htmlResponse.append("                <textarea name=\"feedback\" rows=\"4\" cols=\"50\" maxlength=\"255\"></textarea><br>\n");
            htmlResponse.append("                <center><input type=\"submit\" value=\"Submit Feedback\"></center>\n");
            htmlResponse.append("            </form>\n");
            htmlResponse.append("        </td>\n");
            htmlResponse.append("    </tr>\n");
            List<Feedback> feedbackEntries = null;
            try {
                feedbackEntries = DatabaseUtil.getAllFeedback();
            } catch (SQLException |IOException e) {
                e.printStackTrace();
            }
            htmlResponse.append("    <tr>\n");
            htmlResponse.append("        <th>Username</th>\n");
            htmlResponse.append("        <th>Feedback</th>\n");
            htmlResponse.append("    </tr>\n");
            if (feedbackEntries != null) {
                for (Feedback entry : feedbackEntries) {
                    htmlResponse.append("    <tr>\n");
                    htmlResponse.append("        <td>").append(entry.getUsername()).append("</td>\n");
                    htmlResponse.append("        <td>").append(entry.getFeedback()).append("</td>\n");
                    htmlResponse.append("    </tr>\n");
                }
            }
            htmlResponse.append("    <tr>\n");
            htmlResponse.append("        <td><a href=\"logout.jsp\">Logout</a></td>\n");
            htmlResponse.append("        <td><a href=\"FindUserHome\">Home</a></td>\n");
            htmlResponse.append("    </tr>\n");
            htmlResponse.append("</table>\n");
            htmlResponse.append("</body>\n");
            htmlResponse.append("</html>");
            response.setContentType("text/html");
            try {
                response.getWriter().println(htmlResponse);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            String feedback = request.getParameter("feedback");
            try {
                DatabaseUtil.saveFeedback(username, feedback);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect(request.getContextPath() + "/feedback");
    }
}
