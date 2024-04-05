package com.munish.macsec.softwareSecurity.db;

import com.munish.macsec.softwareSecurity.Common;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseUtil {
    private static final String CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String AES_ALGORITHM = "AES";

    public static Properties loadEncryptedProperties(InputStream is, byte[] hexKey) throws IOException {
        Properties properties = new Properties();
        properties.load(decryptedBytes(is, hexKey));
        return properties;
    }

    public static Connection getConnection() throws SQLException, IOException {
        InputStream is = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties.enc");
        Properties props = loadEncryptedProperties(is, Common.getKey());

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new SQLException("MySQL JDBC Driver not found", e);
        }

        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
        return connection;
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void closeResources(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        close(resultSet);
        close(preparedStatement);
        close(connection);
    }

    private static InputStream decryptedBytes(InputStream is, byte[] keyHex) {
        try {
            StringBuffer sb = new StringBuffer();
            byte[] ivBytes = new byte[16];
            is.read(ivBytes);

            SecretKey secretKey = new SecretKeySpec(keyHex, AES_ALGORITHM);

            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytes));

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                byte[] decryptedBytes = cipher.update(buffer, 0, bytesRead);
                sb.append(new String(decryptedBytes));
            }
            byte[] decryptedBytes = cipher.doFinal();
            sb.append(new String(decryptedBytes));
            return new ByteArrayInputStream(sb.toString().getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updatePassword(String username, String newPassword) throws SQLException, IOException {
        String query = "UPDATE project.user SET password = ? WHERE email = ?";

        try (Connection connection = DatabaseUtil.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setString(2, username);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected == 1) {
                System.out.println("Password updated successfully for user: " + username);
            } else {
                System.out.println("Failed to update password for user: " + username);
            }
        }
    }


    public static boolean isLPU(String username) {
        String query = "SELECT is_lpu FROM project.user WHERE email = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("is_lpu");
                }
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean isRequested(String username) {
        String query = "SELECT id FROM project.promote WHERE email = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean isSeller(String username) {
        String query = "SELECT is_seller FROM project.user WHERE email = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("is_seller");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public static boolean isAdmin(String username) {
        String query = "SELECT is_admin FROM project.user WHERE email = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("is_admin");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public static boolean saveUserToDatabase(String email, String firstName, String lastName, String password) {
        String query = "INSERT INTO project.user (email, first_name, last_name, is_lpu, is_admin, is_seller, locked, unlock_at, password_expired, is_registered, password) " +
                "VALUES (?, ?, ?, 1, 0, 0, 0, null, 1, 0, ?)";

        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setString(4, Common.encrypt(password));

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static boolean saveUserToDatabase(String email, String firstName, String lastName) {
        String query = "INSERT INTO project.user (email, first_name, last_name, is_lpu, is_admin, is_seller, locked, unlock_at, password_expired, is_registered, password) " + "VALUES (?, ?, ?, 1, 0, 0, 0, null, 1, 1, ?)";

        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setString(4, Common.encrypt("fornewuser" + lastName));

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static List<PendingRequest> getPendingRequests() {
        List<PendingRequest> pendingRequests = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String sql = "SELECT email, requested_on FROM promote WHERE approved_on IS NULL";
            preparedStatement = connection.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String requestedOn = resultSet.getString("requested_on");
                PendingRequest request = new PendingRequest(email, requestedOn);
                pendingRequests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            closeResources(connection, preparedStatement, resultSet);
        }

        return pendingRequests;
    }
    public static List<Product> getAllProducts() throws IOException {
        List<Product> products = new ArrayList<>();

        try (Connection connection = getConnection()) {
            String query = "SELECT * FROM products";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Product product = new Product();
                        product.setId(resultSet.getString("id"));
                        product.setName(resultSet.getString("name"));
                        product.setDescription(resultSet.getString("description"));
                        product.setPrice(resultSet.getString("price"));
                        product.setQty(resultSet.getInt("quantity"));
                        products.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public static String createOrder(String productId, String qty, String creditCardNumber, String cvv) throws Exception {
        try (Connection conn = getConnection()) {
            String orderId = getLastInsertedOrderId(conn);
            String insertOrderQuery = "INSERT INTO orders (order_id,product_id, quantity, encrypted_credit_card, cvv) " + "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(insertOrderQuery);
            stmt.setString(1, orderId);
            stmt.setString(2, productId);
            stmt.setInt(3, Integer.parseInt(qty));
            stmt.setString(4, Common.encrypt(creditCardNumber));
            stmt.setString(5, cvv);
            int rowsAffected = stmt.executeUpdate();
            return orderId;
        } catch (SQLException e) {
            throw new Exception("Error creating order", e);
        }
    }

    private static String getLastInsertedOrderId(Connection conn) throws SQLException {
        String query = "SELECT max(order_id) from orders";
        PreparedStatement stmt = conn.prepareStatement(query);
        String id = null;

        try {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                id = String.valueOf(rs.getLong(1) + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;

    }

    public static void addProduct(Product product) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String insertQuery = "INSERT INTO products (name, description, price, quantity) VALUES (?, ?, ?, ?)";
            stmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setDouble(3, Double.parseDouble(product.getPrice()));
            stmt.setInt(4, product.getQty());
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int productId = rs.getInt(1);
                product.setId(String.valueOf(productId));
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static void updateQty(int productId, int updatedQuantity) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            String updateQuery = "UPDATE products SET quantity = ? WHERE id = ?";
            stmt = conn.prepareStatement(updateQuery);
            stmt.setInt(1, updatedQuantity);
            stmt.setInt(2, productId);
            int i = stmt.executeUpdate();
            System.out.println(i);
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static void updatePrice(int productId, String price) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            String updateQuery = "UPDATE products SET price = ? WHERE id = ?";
            stmt = conn.prepareStatement(updateQuery);
            stmt.setDouble(1, Double.parseDouble(price));
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        } finally {
            // Close resources
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static void requestPromo(String username) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = getConnection();
            String sql = "INSERT INTO promote (email) VALUES (?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.executeUpdate();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static void promote(String email) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement pstmtPromote = null;
        PreparedStatement pstmtUser = null;

        try {
            conn = getConnection();

            String sql = "UPDATE promote SET approved_on = ? WHERE email = ?";
            pstmtPromote = conn.prepareStatement(sql);
            pstmtPromote.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmtPromote.setString(2, email);
            pstmtPromote.executeUpdate();

            sql = "UPDATE user SET is_seller = 1, is_lpu = 0 WHERE email = ?";
            pstmtUser = conn.prepareStatement(sql);
            pstmtUser.setString(1, email);
            pstmtUser.executeUpdate();
        } finally {
            if (pstmtPromote != null) {
                pstmtPromote.close();
            }
            if (pstmtUser != null) {
                pstmtUser.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static void reject(String email) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "DELETE FROM promote WHERE email = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.executeUpdate();
        } finally {
            if (pstmt != null) {
                pstmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static boolean changePassword(String username, String currentPassword, String newPassword) throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        if (currentPassword.equals(newPassword)) return false;
        newPassword = Common.sanitizeInput(newPassword);
        if (!Common.isValidPassword(newPassword)) return false;
        try {
            conn = getConnection();
            currentPassword = Common.encrypt(currentPassword);
            String query = "SELECT password FROM user WHERE email = ? AND password = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, username);
            stmt.setString(2, currentPassword);
            rs = stmt.executeQuery();

            newPassword = Common.encrypt(newPassword);
            if (rs.next()) {
                String updateQuery = "UPDATE user SET password = ?, is_registered = ? WHERE email = ?";
                stmt = conn.prepareStatement(updateQuery);
                stmt.setString(1, newPassword);
                stmt.setBoolean(2, true);
                stmt.setString(3, username);
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }

        return false;
    }

    public static boolean isRegistered(String username) {
        String query = "SELECT is_registered FROM project.user WHERE email = ?";
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBoolean("is_registered");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    public static List<Feedback> getAllFeedback() throws SQLException, IOException {
        List<Feedback> feedbackList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String sql = "SELECT email, message FROM feedback";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            while (rs.next()) {
                String email = rs.getString("email");
                String feedback = rs.getString("message");
                Feedback fb = new Feedback();
                fb.setFeedback(feedback);
                fb.setUsername(email);
                feedbackList.add(fb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
        return feedbackList;
    }

    public static void saveFeedback(String username, String feedback) throws SQLException, IOException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = getConnection();
            String sql = "INSERT INTO feedback (email, message) VALUES (?, ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, feedback);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        } finally {
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        }
    }

    public static int validate(String username, String password) {
        try {
            try (Connection connection = DatabaseUtil.getConnection()) {
                if (connection != null) {
                    String query = "SELECT password,login_attempts, last_login_attempt FROM user WHERE email = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, username);
                        try (ResultSet resultSet = preparedStatement.executeQuery()) {
                            if (resultSet.next()) {
                                String encryptedPassword = resultSet.getString("password");
                                String decryptedPassword = Common.decrypt(encryptedPassword);
                                int loginAttempts = resultSet.getInt("login_attempts");
                                long lastLoginAttempt = resultSet.getLong("last_login_attempt");
                                System.out.println(decryptedPassword);
                                System.out.println(password);
                                if (loginAttempts >= 3 && System.currentTimeMillis() - lastLoginAttempt < 2 * 60 * 60 * 1000) {
                                    return 3;
                                } else if (password.equals(decryptedPassword)) {
                                    resetAttempts(username);
                                    return 0;
                                } else {
                                    updateAttempts(username, ++loginAttempts);
                                    return loginAttempts;
                                }
                            }
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (SQLException | IOException | GeneralSecurityException e) {
            e.printStackTrace();
        }
        return 4;
    }

    private static void updateAttempts(String username, int attempts) throws SQLException, IOException {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "UPDATE user SET login_attempts = ?, last_login_attempt = ? WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, attempts);
                preparedStatement.setLong(2, System.currentTimeMillis());
                preparedStatement.setString(3, username);
                preparedStatement.executeUpdate();
            }
        }
    }

    private static void resetAttempts(String username) throws SQLException, IOException {
        try (Connection connection = DatabaseUtil.getConnection()) {
            String query = "UPDATE user SET login_attempts = 0, last_login_attempt = NULL WHERE email = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.executeUpdate();
            }
        }
    }
}

