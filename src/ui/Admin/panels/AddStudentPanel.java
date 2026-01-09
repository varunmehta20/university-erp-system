
package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;
import erp.service.AdminService;

public class AddStudentPanel extends JPanel {
    private AdminService adminService = new AdminService();

    public AddStudentPanel(int adminId) {
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Add New Student", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 7, 880, 40);
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

        JLabel rollLabel = new JLabel("Roll No:");
        rollLabel.setBounds(80, 180, 120, 25);
        add(rollLabel);

        JTextField rollField = new JTextField();
        rollField.setBounds(220, 180, 220, 30);
        add(rollField);

        JLabel programLabel = new JLabel("Program:");
        programLabel.setBounds(80, 230, 120, 25);
        add(programLabel);

        JTextField programField = new JTextField();
        programField.setBounds(220, 230, 220, 30);
        add(programField);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setBounds(80, 280, 120, 25);
        add(yearLabel);

        JTextField yearField = new JTextField();
        yearField.setBounds(220, 280, 220, 30);
        add(yearField);

        JButton createUserBtn = new JButton("Create Student");
        createUserBtn.setBounds(150, 340, 200, 35);
        add(createUserBtn);

        createUserBtn.addActionListener(e -> {
            try {
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String roll = rollField.getText().trim();
                String program = programField.getText().trim();
                String yearText = yearField.getText().trim();

                if (username.isEmpty() || password.isEmpty() || roll.isEmpty() || program.isEmpty() || yearText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.");
                    return;
                }

                int year = Integer.parseInt(yearText);
                int userId = adminService.createAuthUser(username, password, "STUDENT");
                String result = adminService.createStudentProfile(userId, roll, program, year);

                JOptionPane.showMessageDialog(this, result);

                usernameField.setText("");
                passwordField.setText("");
                rollField.setText("");
                programField.setText("");
                yearField.setText("");

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Year must be a number.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}
