package com.munish.macsec.softwareSecurity.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.munish.macsec.softwareSecurity.db.DatabaseUtil;

public class FindUserHomeServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            if (username != null) {
                boolean isLpu = false;
                boolean isSeller = false;
                boolean isAdmin = false;

                try (Connection connection = DatabaseUtil.getConnection()) {
                    String query = "SELECT is_lpu, is_seller, is_admin FROM user WHERE email = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, username);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            if (resultSet.next()) {
                                isLpu = resultSet.getBoolean("is_lpu");
                                isSeller = resultSet.getBoolean("is_seller");
                                isAdmin = resultSet.getBoolean("is_admin");
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (isLpu) {
                    response.sendRedirect(request.getContextPath() + "/UserHome");
                } else if (isSeller) {
                    response.sendRedirect(request.getContextPath() + "/Seller");
                } else if (isAdmin) {
                    response.sendRedirect(request.getContextPath() + "/Admin");
                } else {
                    response.sendRedirect(request.getContextPath() + "/logout.jsp");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/logout.jsp");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }
}
