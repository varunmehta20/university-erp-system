package authen;

import java.sql.*;
import java.time.LocalDateTime;

public class Login {

    public static void resetFailedAttemptsOnStartup() {
        String sql = "UPDATE users_auth SET failed_attempts = 0, status = 'ACTIVE', lock_time = NULL WHERE failed_attempts > 0";

        try (Connection conn = Connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
            System.out.println(" Reset failed attempts for all users at startup");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean loginUser(String username, String password) {
        String query = "SELECT user_id, username, role, password_hash, status, failed_attempts, lock_time FROM users_auth WHERE username = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println(" User not found!");
                return false;
            }

            int userId = rs.getInt("user_id");
            String role = rs.getString("role");
            String hashedPassword = rs.getString("password_hash");
            String status = rs.getString("status");
            int failedAttempts = rs.getInt("failed_attempts");
            Timestamp lockTime = rs.getTimestamp("lock_time");

            if (status.equalsIgnoreCase("LOCKED")) {

                if (lockTime != null) {
                    long secondsPassed = java.time.Duration.between(
                            lockTime.toLocalDateTime(), LocalDateTime.now()).getSeconds();

                    if (secondsPassed >= 30) {
                        String unlockSQL = "UPDATE users_auth SET status = 'ACTIVE', failed_attempts = 0, lock_time = NULL WHERE user_id = ?";
                        try (PreparedStatement ps2 = conn.prepareStatement(unlockSQL)) {
                            ps2.setInt(1, userId);
                            ps2.executeUpdate();
                        }
                        System.out.println(" Account auto-unlocked! Try again.");
                    } else {
                        System.out.println(" Account locked. Try again after " + (30 - secondsPassed) + " seconds.");
                        return false;
                    }
                }
            }

            if (PasswordUtils.checkPassword(password, hashedPassword)) {

                String updateQuery = "UPDATE users_auth SET failed_attempts = 0, last_login = ?, lock_time = NULL, status = 'ACTIVE' WHERE user_id = ?";
                try (PreparedStatement ps2 = conn.prepareStatement(updateQuery)) {
                    ps2.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    ps2.setInt(2, userId);
                    ps2.executeUpdate();
                }

                SetSession.startSession(userId, username, role);
                System.out.println(" Login successful!");
                return true;

            } else {
                failedAttempts++;
                System.out.println(" Wrong password! Attempt " + failedAttempts);

                if (failedAttempts >= 5) {
                    // Lock account + set lock time
                    String lockQuery = "UPDATE users_auth SET failed_attempts = ?, status = 'LOCKED', lock_time = ? WHERE user_id = ?";
                    try (PreparedStatement ps3 = conn.prepareStatement(lockQuery)) {
                        ps3.setInt(1, failedAttempts);
                        ps3.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                        ps3.setInt(3, userId);
                        ps3.executeUpdate();
                    }
                    System.out.println(" Account locked for 30 seconds due to too many attempts!");
                } else {
                    // Update only attempts
                    String updateAttempts = "UPDATE users_auth SET failed_attempts = ? WHERE user_id = ?";
                    try (PreparedStatement ps4 = conn.prepareStatement(updateAttempts)) {
                        ps4.setInt(1, failedAttempts);
                        ps4.setInt(2, userId);
                        ps4.executeUpdate();
                    }
                }

                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}