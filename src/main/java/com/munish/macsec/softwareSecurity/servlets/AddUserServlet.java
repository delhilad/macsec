package com.munish.macsec.softwareSecurity.servlets;

import com.munish.macsec.softwareSecurity.Common;
import com.munish.macsec.softwareSecurity.db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
public class AddUserServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String email = request.getParameter("email");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String msg = "";

        if (!Common.isValidEmail(email) || !Common.isValidFirstName(firstName) || !Common.isValidLastName(lastName)) {
            msg = "Invalid input data";
        } else {
            boolean success = DatabaseUtil.saveUserToDatabase(email, firstName, lastName);
            if (success) {
                msg = "User added successfully with email: " + email;
            } else {
                msg = "Failed to save user record, please try again or contact support";
            }
        }

        String htmlResponse = generateHtml(msg);
        response.setContentType("text/html");
        response.getWriter().println(htmlResponse);
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String htmlResponse = generateHtml("");
        response.getWriter().println(htmlResponse);
    }
    private String generateHtml(String msg) {
        String html= "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Add User</title>\n" +
                "    <style>\n" +
                "        body {\n" +
                "            display: flex;\n" +
                "            justify-content: center;\n" +
                "            align-items: center;\n" +
                "            height: 100vh;\n" +
                "            margin: 0;\n" +
                "        }\n" +
                "        table {\n" +
                "            margin-top: 50px; /* Adjust margin as needed */\n" +
                "            border-collapse: collapse;\n" +
                "            text-align: center;\n" +
                "            width: 50%;\n" +
                "        }\n" +
                "        th, td {\n" +
                "            padding: 5px; /* Reduced padding */\n" +
                "            border: 1px solid #ddd;\n" +
                "        }\n" +
                "        /* Center the nested table */\n" +
                "        #innerTable {\n" +
                "            margin: 0 auto; /* Center the table horizontally */\n" +
                "            text-align: left; /* Align contents of the table to the left */\n" +
                "        }\n" +
                "    </style>\n" +
                "    <script>\n" +
                "        function validateFields() {\n" +
                "            var email = document.getElementById('email').value;\n" +
                "            var firstName = document.getElementById('firstName').value;\n" +
                "            var lastName = document.getElementById('lastName').value;\n" +
                "            var submitButton = document.getElementById('addUserButton');\n" +
                "            if (email.trim() !== '' && firstName.trim() !== '' && lastName.trim() !== '') {\n" +
                "                submitButton.disabled = false;\n" +
                "            } else {\n" +
                "                submitButton.disabled = true;\n" +
                "            }\n" +
                "        }\n" +
                "    </script>\n" +
                "</head>\n" +
                "<body>\n" +
                "<table>\n" +
                "    <tr><th colspan=\"2\">Add User</th></tr>\n" +
                "    <tr><td colspan=\"2\">\n" +
                "        <form action=\"AddUserServlet\" method=\"post\">\n" +
                "            <table id=\"innerTable\">\n" +
                "                <tr>\n" +
                "                    <td>Email:</td>\n" +
                "                    <td><input type=\"text\" name=\"email\" id=\"email\" onchange=\"validateFields()\"></td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td>First Name:</td>\n" +
                "                    <td><input type=\"text\" name=\"firstName\" id=\"firstName\" onchange=\"validateFields()\"></td>\n" +
                "                </tr>\n" +
                "                <tr>\n" +
                "                    <td>Last Name:</td>\n" +
                "                    <td><input type=\"text\" name=\"lastName\" id=\"lastName\" onchange=\"validateFields()\"></td>\n" +
                "                </tr>\n" +
                "            </table>\n" +
                "            <input type=\"submit\" value=\"Add User\" id=\"addUserButton\" disabled>\n" +
                "        </form>\n" +
                "    </td></tr>\n" +
                "    <tr><td colspan=\"2\"><a href=Admin>Home</a><br></td></tr>\n" +
                "    <tr><td colspan=\"2\"><a href=logout.jsp>Logout</a><br></td></tr>\n" +
                "    <tr><td colspan=\"2\"><a href=ChangePassword>Change Password</a></td></tr>\n";
        if(!msg.equalsIgnoreCase(""))
            html+="    <tr><td colspan=\"2\"><h2>" + msg + "</h2></td></tr>\n";
        html+="</table>\n" +
                "</body>\n" +
                "</html>";
        if(!msg.equalsIgnoreCase(""))
            html+="    <tr><td colspan=\"2\"><h2>" + msg + "</h2></td></tr>\n";
        html+="</table>\n" +
                "</body>\n" +
                "</html>";
        return html;
    }

}
