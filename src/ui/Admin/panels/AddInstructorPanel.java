
package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;
import erp.service.AdminService;

public class AddInstructorPanel extends JPanel {
    private AdminService adminService = new AdminService();

    public AddInstructorPanel(int adminId) {
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Add New Instructor", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 10, 880, 40);
        add(title);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(80, 80, 120, 25);
        add(usernameLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(220, 80, 220, 30);
        add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(80, 130, 120, 25);
        add(passwordLabel);

        JPasswordField passwordField = new JPasswordField();
        passwordField.setBounds(220, 130, 220, 30);
        add(passwordField);

        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setBounds(80, 180, 120, 25);
        add(deptLabel);

        JTextField deptField = new JTextField();
        deptField.setBounds(220, 180, 220, 30);
        add(deptField);

        JLabel desigLabel = new JLabel("Designation:");
        desigLabel.setBounds(80, 230, 120, 25);
        add(desigLabel);

        JTextField desigField = new JTextField();
        desigField.setBounds(220, 230, 220, 30);
        add(desigField);

        JButton createUserBtn = new JButton("Create Instructor");
        createUserBtn.setBounds(150, 300, 200, 35);
        add(createUserBtn);

        createUserBtn.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String dept = deptField.getText().trim();
                String desig = desigField.getText().trim();

                if (username.isEmpty() || password.isEmpty() || dept.isEmpty() || desig.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.");
                    return;
                }

                int userId = adminService.createAuthUser(username, password, "INSTRUCTOR");
                String result = adminService.createInstructorProfile(userId, dept, desig);

                JOptionPane.showMessageDialog(this, result);

                usernameField.setText("");
                passwordField.setText("");
                deptField.setText("");
                desigField.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}
