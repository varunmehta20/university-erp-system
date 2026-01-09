package erp.service;

import authen.*;
import erp.ERPConnector;


import java.sql.*;
public class AuthService {
    public boolean login(String username, String password) {
        boolean success = Login.loginUser(username, password);
        if (success) {
            System.out.println("[AuthService]  Login successful for " + username);
        } else {
            System.out.println("[AuthService]  Login failed for " + username);
        }
        return success;
    }

    public void logout() {
        if (SetSession.isLoggedIn()) {
            System.out.println("[AuthService] Logging out: " + SetSession.getUsername());
            SetSession.endSession();
        } else {
            System.out.println("[AuthService] No user is currently logged in.");
        }
    }

    public int addUser(String username, String password, String role) {
        if (usernameExistsForRole(username, role)) {
            throw new RuntimeException("A " + role + " with this username already exists.");
        }

        final String SQL = "INSERT INTO users_auth (username, role, password_hash) VALUES (?, ?, ?)";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {
            String hashed = PasswordUtils.hashPassword(password);

            stmt.setString(1, username);
            stmt.setString(2, role);
            stmt.setString(3, hashed);

            int rows = stmt.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Insert failed. No rows affected.");
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add user: " + e.getMessage());
        }

        return -1;
    }

    public boolean removeUser(String usernameToDelete) {
        boolean success = RemoveUser.removeUser(usernameToDelete);
        if (success) {
            System.out.println("[AuthService]  User removed: " + usernameToDelete);
        } else {
            System.out.println("[AuthService]  Failed to remove user: " + usernameToDelete);
        }
        return success;
    }

    public boolean changePassword(String username, String oldPassword, String newPassword) {
        boolean success = ChangePassword.changePassword(username, oldPassword, newPassword);

        if (success) {
            System.out.println("[AuthService] Password changed successfully for username: " + username);
        } else {
            System.out.println("[AuthService] Password change failed for username: " + username);
        }

        return success;
    }

    public boolean isLockedOut(String username) {
        String sql = "SELECT status FROM users_auth WHERE username = ?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "LOCKED".equalsIgnoreCase(rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("[AuthService]  DB error checking lockout: " + e.getMessage());
        }
        return false;
    }

    public boolean resetLock(String username) {
        String sql = "UPDATE users_auth SET status='ACTIVE', failed_attempts=0 WHERE username=?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("[AuthService]  Lock reset for user: " + username);
                return true;
            }
        } catch (SQLException e) {
            System.out.println("[AuthService]  Error resetting lock: " + e.getMessage());
        }
        return false;
    }

    public boolean userExists(String username) {
        String query = "SELECT user_id FROM users_auth WHERE username = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            return rs.next();
        } catch (Exception e) {
            System.out.println("DB error: " + e.getMessage());
            return false;
        }
    }

    public String getCurrentRole() {
        return SetSession.isLoggedIn() ? SetSession.getRole() : null;
    }

    public String getCurrentUsername() {
        return SetSession.isLoggedIn() ? SetSession.getUsername() : null;
    }

    public int getCurrentUserId() {
        return SetSession.isLoggedIn() ? SetSession.getUserId() : -1;
    }

    public boolean isLoggedIn() {
        return SetSession.isLoggedIn();
    }

    public String getUsernameByUserId(int userId) {
        final String SQL = "SELECT username FROM users_auth WHERE user_id = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }

        } catch (SQLException e) {
            System.err.println("Database Error (getUsernameByUserId): " + e.getMessage());
        }

        return null;
    }

    public int getUserIdByUsername(String username) {
        final String SQL = "SELECT user_id FROM users_auth WHERE username = ?";
        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("user_id");
            }

        } catch (SQLException e) {
            System.err.println("Database Error (getUserIdByUsername): " + e.getMessage());
        }
        return -1;
    }

    public boolean usernameExistsForRole(String username, String role) {
        final String SQL = "SELECT COUNT(*) FROM users_auth WHERE username = ? AND role = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, username);
            stmt.setString(2, role);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking username: " + e.getMessage());
        }
        return false;
    }
}
