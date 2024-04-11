package com.munish.macsec.softwareSecurity.servlets;

import com.munish.macsec.softwareSecurity.db.Product;
import com.munish.macsec.softwareSecurity.db.ProductComparator;
import com.munish.macsec.softwareSecurity.db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class UserHomeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            if (session.getAttribute("invalidCreditCard") != null && session.getAttribute("invalidCreditCard").equals("1")) {
                response.getWriter().println("<p style=\"color: red;\">Invalid credit card details. Please try again.</p>");
                session.removeAttribute("invalidCreditCard");
            }
            String lastName = (String) session.getAttribute("username");
            List<Product> products = DatabaseUtil.getAllProducts();

            Collections.sort(products, new ProductComparator());

            StringBuilder htmlResponse = new StringBuilder();
            htmlResponse.append("<!DOCTYPE html>\n");
            htmlResponse.append("<html>\n");
            htmlResponse.append("<head>\n");
            htmlResponse.append("    <meta charset=\"UTF-8\">\n");
            htmlResponse.append("    <title>Hello ").append(lastName).append("</title>\n");
            htmlResponse.append("    <script>\n");
            htmlResponse.append("        function buy(productId) {\n");
            htmlResponse.append("            var creditCardNumber = prompt('Enter Credit Card Number');\n");
            htmlResponse.append("            var cvv = prompt('Enter CVV');\n");
            htmlResponse.append("            if (creditCardNumber && cvv) {\n");
            htmlResponse.append("                document.getElementById('creditCardNumber').value = creditCardNumber;\n");
            htmlResponse.append("                document.getElementById('cvv').value = cvv;\n");
            htmlResponse.append("                document.getElementById('productId').value = productId;\n");
            htmlResponse.append("                document.getElementById('quantity').value = document.getElementById('quantity_' + productId).value;\n");
            htmlResponse.append("                document.getElementById('buyForm').submit();\n");
            htmlResponse.append("            }\n");
            htmlResponse.append("        }\n");
            htmlResponse.append("        function requestSeller() {\n");
            htmlResponse.append("            var form = document.createElement('form');\n");
            htmlResponse.append("            form.method = 'post';\n");
            htmlResponse.append("            form.action = 'userHome';\n");
            htmlResponse.append("            var input = document.createElement('input');\n");
            htmlResponse.append("            input.type = 'hidden';\n");
            htmlResponse.append("            input.name = 'action';\n");
            htmlResponse.append("            input.value = 'requestSeller';\n");
            htmlResponse.append("            form.appendChild(input);\n");
            htmlResponse.append("            document.body.appendChild(form);\n");
            htmlResponse.append("            form.submit();\n");
            htmlResponse.append("        }\n");            htmlResponse.append("    </script>\n");
            htmlResponse.append("</head>\n");
            htmlResponse.append("<body>\n");
            htmlResponse.append("    <h1>Hello ").append(lastName).append("</h1>\n");
            if(session.getAttribute("Requested")==null) {
                htmlResponse.append("    <button onclick=\"requestSeller()\">Request Seller</button><br><br>\n");
            }else{
                htmlResponse.append("    <h2>Seller Requested<h2><br><br>\n");

            }
            htmlResponse.append("    <table border=\"1\">\n");
            htmlResponse.append("        <tr>\n");
            htmlResponse.append("            <th>Product Name</th>\n");
            htmlResponse.append("            <th>Description</th>\n");
            htmlResponse.append("            <th>Price</th>\n");
            htmlResponse.append("            <th>Add to Cart</th>\n");
            htmlResponse.append("        </tr>\n");
            for (Product product : products) {
                htmlResponse.append("        <tr>\n");
                htmlResponse.append("            <td>").append(product.getName()).append("</td>\n");
                htmlResponse.append("            <td>").append(product.getDescription()).append("</td>\n");
                htmlResponse.append("            <td>").append(product.getPrice()).append("</td>\n");
                htmlResponse.append("            <td>\n");
                htmlResponse.append("                <input id=\"quantity_").append(product.getId()).append("\" type=\"number\" min=\"1\" max=\"").append(product.getQty()).append("\" placeholder=\"Quantity\" required>\n");
                htmlResponse.append("                <button onclick=\"buy('").append(product.getId()).append("')\">Buy</button>\n");
                htmlResponse.append("            </td>\n");
                htmlResponse.append("        </tr>\n");
            }
            htmlResponse.append("    </table>\n");
            htmlResponse.append("    <form id=\"buyForm\" action=\"UserHome\" method=\"post\" style=\"display: none;\">\n");
            htmlResponse.append("        <input type=\"hidden\" name=\"creditCardNumber\" id=\"creditCardNumber\">\n");
            htmlResponse.append("        <input type=\"hidden\" name=\"cvv\" id=\"cvv\">\n");
            htmlResponse.append("        <input type=\"hidden\" name=\"productId\" id=\"productId\">\n");
            htmlResponse.append("        <input type=\"hidden\" name=\"quantity\" id=\"quantity\">\n");
            htmlResponse.append("    </form>\n");
            htmlResponse.append("<h2><a href=Feedback>Feedback</a>");
            htmlResponse.append("<br><a href=logout.jsp>Logout</a>\n");
            htmlResponse.append(("<br><a href=ChangePassword>Change Password</a>\n"));
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
        if (session != null && session.getAttribute("username") != null) {
            String promoRequested=request.getParameter("action");
            if(promoRequested!=null){
                try {
                    DatabaseUtil.requestPromo((String) session.getAttribute("username"));
                    session.setAttribute("Requested","Seller");
                } catch (SQLException e) {
                    session.setAttribute("Requested","Seller");
                }
                response.sendRedirect(request.getContextPath() + "/userHome");
            }else {
                String creditCardNumber = request.getParameter("creditCardNumber");
                String cvv = request.getParameter("cvv");
                String prod = request.getParameter("productId");
                String qty = request.getParameter("quantity");
                if(Integer.parseInt(qty)<=0){
                    return;
                }

                if (isValidCreditCard(creditCardNumber) && isValidCVV(cvv)) {
                    String orderCreated = "0";
                    try {
                        orderCreated = DatabaseUtil.createOrder(prod, qty, creditCardNumber, cvv);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    if (!orderCreated.equals("0")) {
                        session.setAttribute("order", orderCreated);
                        response.sendRedirect(request.getContextPath() + "/OrderSuccess");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/OrderError.jsp");
                    }
                } else {
                    session.setAttribute("invalidCreditCard", "1");
                    response.sendRedirect(request.getContextPath() + "/userHome");
                }
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    private boolean isValidCreditCard(String creditCardNumber) {
        creditCardNumber = creditCardNumber.trim();

        if (creditCardNumber.matches("^\\d{16}$")) {
            return true;
        }
        return false;
    }


    private boolean isValidCVV(String cvv) {
        cvv = cvv.trim();

        if (cvv.matches("^\\d{3}$")) {
            return true;
        }
        return false;
    }

}
