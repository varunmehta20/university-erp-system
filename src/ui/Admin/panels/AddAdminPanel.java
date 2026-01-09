
package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;
import erp.service.AdminService;

public class AddAdminPanel extends JPanel {
    private AdminService adminService = new AdminService();

    public AddAdminPanel(int adminId) {
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Add New Admin", JLabel.CENTER);
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

        JButton createUserBtn = new JButton("Create Admin");
        createUserBtn.setBounds(150, 220, 200, 35);
        add(createUserBtn);

        createUserBtn.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.");
                    return;
                }

                adminService.createAuthUser(username, password, "ADMIN");
                JOptionPane.showMessageDialog(this, "Admin created successfully!");

                usernameField.setText("");
                passwordField.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}
