package com.munish.macsec.softwareSecurity.servlets;

import com.munish.macsec.softwareSecurity.Common;
import com.munish.macsec.softwareSecurity.db.DatabaseUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


import static com.munish.macsec.softwareSecurity.db.DatabaseUtil.*;

public class LoginServ extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String username = Common.sanitizeInput(request.getParameter("username"));
        String password = Common.sanitizeInput(request.getParameter("password"));

        if (username == null || username.length() < 8 || password == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=true");
            return;
        }

        int isValidUser = DatabaseUtil.validate(username, password);
        boolean registered = DatabaseUtil.isRegistered(username);


        if (isValidUser == 0) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            session.setAttribute("cookie", cookie(username));
            session.removeAttribute("error");
            if (!registered) {
                response.sendRedirect(request.getContextPath() + "/changePassword");
            } else if (isLPU(username)) {
                if (isRequested(username)) {
                    session.setAttribute("Requested", "Seller");
                }
                response.sendRedirect(request.getContextPath() + "/UserHome");
            } else if (isSeller(username)) {
                response.sendRedirect(request.getContextPath() + "/Seller");
            } else if (isAdmin(username)) {
                response.sendRedirect(request.getContextPath() + "/Admin");
            }
        } else {
            HttpSession session = request.getSession();
            String error = "";
            if (isValidUser >= 1 || isValidUser <= 3) {
                error = "Failed attempts made: " + isValidUser;
            }
            if (isValidUser == 3) {
                error += ". Account locked for two hours.";
            }
            session.setAttribute("attempt", isValidUser);
            session.setAttribute("error", error);
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=true");
        }
    }

    public String cookie(String username) {
        try {
            long timestamp = System.currentTimeMillis();
            String data = username + timestamp + "sessioncookie";
            String encodedData = Common.encrypt(data);
            return encodedData;
        } catch (Exception e) {
            return null;
        }
    }


}
