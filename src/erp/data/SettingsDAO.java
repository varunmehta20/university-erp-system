package erp.data;

import erp.domain.Setting;
import erp.ERPConnector;
import java.sql.*;

public class SettingsDAO {

    public String getValue(String key) {
        final String SQL = "SELECT v FROM settings WHERE k = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, key);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("v");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getValue): Failed to retrieve setting for key '" + key + "'. " + e.getMessage());
        }
        return null;
    }

    public boolean setValue(String key, String value) {
        final String SQL_UPDATE = "UPDATE settings SET v = ? WHERE k = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_UPDATE)) {

            stmt.setString(1, value);
            stmt.setString(2, key);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 1) {
                return true;
            } else if (affectedRows == 0) {
                final String SQL_INSERT = "INSERT INTO settings (k, v) VALUES (?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(SQL_INSERT)) {
                    insertStmt.setString(1, key);
                    insertStmt.setString(2, value);
                    return insertStmt.executeUpdate() == 1;
                }
            }

        } catch (SQLException e) {
            System.err.println("Database Error (setValue): Failed to set value for key '" + key + "'. " + e.getMessage());
        }
        return false;
    }

    public boolean isMaintenanceOn() {
        String value = getValue("maintenance");
        return "true".equalsIgnoreCase(value);
    }

    public void toggleMaintenance(boolean status) {
        String value = status ? "true" : "false";
        if (!setValue("maintenance", value)) {
            System.err.println("Error: Failed to set maintenance status to " + value);
        }
    }

    public void setDropDeadline(String deadlineString) {
        if (!setValue("dropDeadline", deadlineString)) {
            System.err.println("Error: Failed to set drop deadline to " + deadlineString);
        }
    }
}
