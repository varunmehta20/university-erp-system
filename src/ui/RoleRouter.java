package ui;

import javax.swing.*;

public class RoleRouter {

    public static void routeToDashboard(String role, int userId) {
        if (role == null) {
            JOptionPane.showMessageDialog(null, "Error: No role found for this user!");
            ERPMain.showLogin();
            return;
        }

        switch (role.toUpperCase()) {
            case "STUDENT":
                ERPMain.showStudentDashboard(userId);
                break;

            case "INSTRUCTOR":
                ERPMain.showInstructorDashboard(userId);
                break;

            case "ADMIN":
                ERPMain.showAdminDashboard(userId);
                break;

            default:
                JOptionPane.showMessageDialog(null, "Unknown role: " + role);
                ERPMain.showLogin();
        }
    }
}

