package authen;

import java.sql.*;

public class AddUser {

    public static int addNewUser(String username, String password, String role) {
        if (!SetSession.isLoggedIn()) {
            System.out.println(" No one is logged in. Only admin can add new users.");
            return -1;
        }

        if (!SetSession.getRole().equalsIgnoreCase("ADMIN")) {
            System.out.println(" Only admin can add new users!");
            return -1;
        }

        String hashedPassword = PasswordUtils.hashPassword(password);

        String sql = "INSERT INTO users_auth (username, role, password_hash, status, failed_attempts) VALUES (?, ?, ?, 'ACTIVE', 0)";
        int userId = -1;

        try (Connection conn = Connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, role.toUpperCase());
            ps.setString(3, hashedPassword);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                        System.out.println(" New user added successfully: " + username + " (" + role + "), ID = " + userId);
                    }
                }
            } else {
                System.out.println(" Failed to add user.");
            }

        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
        }

        return userId;
    }
}
