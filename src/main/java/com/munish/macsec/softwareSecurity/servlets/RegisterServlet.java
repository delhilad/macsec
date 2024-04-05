package com.munish.macsec.softwareSecurity.servlets;

import com.munish.macsec.softwareSecurity.Common;
import com.munish.macsec.softwareSecurity.db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);
        if (action != null && action.equals("register")) {
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            int resp = isValidUser(firstName, lastName, email, password);
            System.out.println(resp);
            if (0 != resp) {
                switch (resp) {
                    case 1:
                        request.setAttribute("error", "Invalid first name");
                        break;
                    case 2:
                        request.setAttribute("error", "Invalid last name");
                        break;
                    case 3:
                        request.setAttribute("error", "Invalid email id");
                        break;
                    case 4:
                    case 7:
                        request.setAttribute("error", "Invalid password. Required: <br>16 chars<br>Atleast one capital letter<br>Atleast one small letter<br>atlease one digit");
                        break;
                }
                request.getRequestDispatcher("/register.jsp").forward(request, response);
                return;
            }
            DatabaseUtil.saveUserToDatabase(email, firstName, lastName, password);
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
            response.sendRedirect(request.getContextPath() + "/register.jsp");
        }
    }

    private int isValidUser(String firstName, String lastName, String email, String password) {
        int name = (firstName != null && !firstName.isEmpty()) ? 0 : 1;
        int lname = (lastName != null && !lastName.isEmpty()) ? 0 : 2;
        int id = (email.matches("^\\w+@[a-zA-Z_]+?\\.[a-zA-Z]{2,3}$")) ? 0 : 3;
        int pass = Common.isValidPassword(password) ? 0 : 4;
        return name + lname + id + pass;
    }
}
