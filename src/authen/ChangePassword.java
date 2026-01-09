package authen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ChangePassword {

    public static boolean changePassword(String username, String oldPassword, String newPassword) {

        String getUser = "SELECT user_id, password_hash FROM users_auth WHERE username = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(getUser)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println(" Username not found!");
                return false;
            }

            int userId = rs.getInt("user_id");
            String currentHash = rs.getString("password_hash");

            if (!PasswordUtils.checkPassword(oldPassword, currentHash)) {
                System.out.println(" Old password is incorrect.");
                return false;
            }

            String newHash = PasswordUtils.hashPassword(newPassword);

            String update = "UPDATE users_auth SET password_hash = ?, failed_attempts = 0 WHERE user_id = ?";
            try (PreparedStatement ps2 = conn.prepareStatement(update)) {
                ps2.setString(1, newHash);
                ps2.setInt(2, userId);

                int rows = ps2.executeUpdate();
                if (rows > 0) {
                    System.out.println(" Password updated successfully for username: " + username);
                    return true;
                } else {
                    System.out.println(" Password update failed.");
                    return false;
                }
            }

        } catch (SQLException e) {
            System.out.println(" DB Error: " + e.getMessage());
            return false;
        }
    }
}
