package authen;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RemoveUser {

    public static boolean removeUser(String usernameToDelete) {
        if (!SetSession.isLoggedIn()) {
            System.out.println(" You must be logged in to remove a user.");
            return false;
        }

        if (!"ADMIN".equalsIgnoreCase(SetSession.getRole())) {
            System.out.println(" Only ADMINs can remove users.");
            return false;
        }

        if (usernameToDelete.equalsIgnoreCase(SetSession.getUsername())) {
            System.out.println(" You cannot delete your own account.");
            return false;
        }

        String deleteSQL = "DELETE FROM users_auth WHERE username = ?";

        try (Connection conn = Connector.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSQL)) {

            ps.setString(1, usernameToDelete);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println(" User '" + usernameToDelete + "' removed successfully.");
                return true;
            } else {
                System.out.println(" No user found with username: " + usernameToDelete);
                return false;
            }

        } catch (SQLException e) {
            System.out.println(" Database error: " + e.getMessage());
            return false;
        }
    }
}
