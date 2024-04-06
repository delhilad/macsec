package com.munish.macsec.softwareSecurity.servlets;

import com.munish.macsec.softwareSecurity.db.Product;
import com.munish.macsec.softwareSecurity.db.DatabaseUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class SellerServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("username") == null) {
            try {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<Product> products = null;
        try {
            products = DatabaseUtil.getAllProducts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        StringBuilder htmlResponse = new StringBuilder();
        htmlResponse.append("<!DOCTYPE html>\n");
        htmlResponse.append("<html>\n");
        htmlResponse.append("<head>\n");
        htmlResponse.append("    <meta charset=\"UTF-8\">\n");
        htmlResponse.append("    <title>Seller Dashboard</title>\n");
        htmlResponse.append("</head>\n");
        htmlResponse.append("<body>\n");
        htmlResponse.append("<table align=\"center\" border=\"1\">\n");
        htmlResponse.append("    <tr>\n");
        htmlResponse.append("        <th colspan=\"6\">Seller Dashboard</th>\n");
        htmlResponse.append("    </tr>\n");
        htmlResponse.append("    <tr>\n");
        htmlResponse.append("        <th>Name</th>\n");
        htmlResponse.append("        <th>Description</th>\n");
        htmlResponse.append("        <th>Price</th>\n");
        htmlResponse.append("        <th>Quantity</th>\n");
        htmlResponse.append("        <th>Actions</th>\n");
        htmlResponse.append("    </tr>\n");
        for (Product product : products) {
            htmlResponse.append("    <tr>\n");
            htmlResponse.append("        <td>").append(product.getName()).append("</td>\n");
            htmlResponse.append("        <td>").append(product.getDescription()).append("</td>\n");
            htmlResponse.append("        <td>\n");
            htmlResponse.append("            <form action=\"Seller\" method=\"post\">\n");
            htmlResponse.append("                <input type=\"hidden\" name=\"action\" value=\"price\">\n");
            htmlResponse.append("                <input type=\"hidden\" name=\"productId\" value=\"").append(product.getId()).append("\">\n");
            htmlResponse.append("                <input type=\"number\" name=\"price\" step=\"0.01\" min=\"0\" value=\"").append(product.getPrice()).append("\">\n");
            htmlResponse.append("                <input type=\"submit\" value=\"Update\">\n");
            htmlResponse.append("            </form>\n");
            htmlResponse.append("        </td>\n");
            htmlResponse.append("        <td>\n");
            htmlResponse.append("            <form action=\"Seller\" method=\"post\">\n");
            htmlResponse.append("                <input type=\"hidden\" name=\"action\" value=\"qty\">\n");
            htmlResponse.append("                <input type=\"hidden\" name=\"productId\" value=\"").append(product.getId()).append("\">\n");
            htmlResponse.append("                <input type=\"number\" name=\"quantity\" min=\"0\" value=\"").append(product.getQty()).append("\">\n");
            htmlResponse.append("                <input type=\"submit\" value=\"Update\">\n");
            htmlResponse.append("            </form>\n");
            htmlResponse.append("        </td>\n");
            htmlResponse.append("    </tr>\n");
        }
        htmlResponse.append("</table>\n");

        htmlResponse.append("<h2>Add New Product</h2>\n");
        htmlResponse.append("<form action=\"Seller\" method=\"post\">\n");
        htmlResponse.append("<table align=\"center\" border=\"1\">\n");
        htmlResponse.append("    <tr>\n");
        htmlResponse.append("        <th>Name</th>\n");
        htmlResponse.append("        <th>Description</th>\n");
        htmlResponse.append("        <th>Price</th>\n");
        htmlResponse.append("        <th>Quantity</th>\n");
        htmlResponse.append("    </tr>\n");
        htmlResponse.append("    <tr>\n");
        htmlResponse.append("        <td><input type=\"text\" name=\"name\" required></td>\n");
        htmlResponse.append("        <td><textarea name=\"description\" required></textarea></td>\n");
        htmlResponse.append("        <td><input type=\"number\" name=\"price\" step=\"0.01\" min=\"0\" required></td>\n");
        htmlResponse.append("        <td><input type=\"number\" name=\"quantity\" min=\"0\" required></td>\n");
        htmlResponse.append("        <td><input type=\"submit\" value=\"Add Product\"></td>\n");
        htmlResponse.append("    </tr>\n");
        htmlResponse.append("</table>\n");
        htmlResponse.append("</form>\n");



        htmlResponse.append("<h2><a href=\"" + request.getContextPath() + "/Feedback\">Leave Feedback</a></h2>\n");
        htmlResponse.append("<br><a href=logout.jsp>Logout</a>\n");
        htmlResponse.append("<br><a href=changePassword>Change Password</a>\n");
        htmlResponse.append("</body>\n");
        htmlResponse.append("</html>");
        response.setContentType("text/html");
        try {
            response.getWriter().println(htmlResponse);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            String action = request.getParameter("action");
            if (action != null) {
                switch (action) {
                    case "add":
                        try {
                            addProduct(request, response);
                        } catch (SQLException e) {
                            session.setAttribute("duplicate","1");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "qty":
                        try {
                            updateQty(request, response);
                        } catch (SQLException | IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "price":
                        try {
                            updatePrice(request, response);
                        } catch (SQLException | IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                try {
                    response.sendRedirect(request.getContextPath() + "/seller");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    response.sendRedirect(request.getContextPath() + "/seller");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addProduct(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String price = request.getParameter("price");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQty(quantity);
        DatabaseUtil.addProduct(product);
    }

    private void updateQty(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        int updatedQuantity = Integer.parseInt(request.getParameter("quantity"));
        DatabaseUtil.updateQty(productId, updatedQuantity);
    }
    private void updatePrice(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        int productId = Integer.parseInt(request.getParameter("productId"));
        String price = request.getParameter("price");
        DatabaseUtil.updatePrice(productId, price);
    }
}
