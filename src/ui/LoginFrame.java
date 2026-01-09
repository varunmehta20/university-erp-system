package ui;

import javax.swing.*;

import erp.service.AuthService;

import java.awt.*;

public class LoginFrame extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private AuthService authService;
    private JButton loginBtn;
    private JLabel timerLabel;

    public LoginFrame() {
        setLayout(null);
        setBounds(0, 0, 900, 600);

        authService = new AuthService();

        JLabel title = new JLabel("Login", SwingConstants.CENTER);
        title.setBounds(0, 40, 900, 50);
        title.setFont(new Font("Arial", Font.BOLD, 32));
        add(title);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(280, 150, 100, 30);
        add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(380, 150, 280, 35);
        add(usernameField);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setBounds(280, 210, 100, 30);
        add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(380, 210, 280, 35);
        add(passwordField);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(380, 270, 280, 40);
        add(loginBtn);

        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.setBounds(380, 320, 280, 35);
        add(changePassBtn);

        timerLabel = new JLabel("");
        timerLabel.setBounds(380, 365, 300, 30);
        timerLabel.setForeground(Color.RED);
        timerLabel.setVisible(false);
        add(timerLabel);

        loginBtn.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in both fields!");
                return;
            }

            boolean success = authService.login(username, password);
            if (success) {
                String role = authService.getCurrentRole();
                int userId = authService.getCurrentUserId();
                JOptionPane.showMessageDialog(this,
                        "Login successful! Welcome " + role);

                RoleRouter.routeToDashboard(role, userId);

            } else {
                if (authService.isLockedOut(username)) {
                    JOptionPane.showMessageDialog(this,
                            "Your account has been locked due to 3 failed attempts.",
                            "Account Locked",
                            JOptionPane.WARNING_MESSAGE);
                    startLockCountdown(username);
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid username or password!");
                }
            }
        });



        changePassBtn.addActionListener(e -> {

            String username = usernameField.getText().trim();

            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Enter your username first!");
                return;
            }

            if (!authService.userExists(username)) {
                JOptionPane.showMessageDialog(this,
                        "No such user exists!");
                return;
            }

            JPasswordField oldPassField = new JPasswordField();
            JPasswordField newPassField = new JPasswordField();

            Object[] message = {
                    "Old Password:", oldPassField,
                    "New Password:", newPassField
            };

            int option = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Change Password",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {

                String oldPass = new String(oldPassField.getPassword());
                String newPass = new String(newPassField.getPassword());

                if (oldPass.isEmpty() || newPass.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Both fields must be filled!");
                    return;
                }

                boolean changed = authService.changePassword(username, oldPass, newPass);

                if (changed) {
                    JOptionPane.showMessageDialog(this,
                            "Password changed successfully!");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Incorrect old password OR username invalid.");
                }
            }
        });
    }

    private void startLockCountdown(String username) {
        loginBtn.setEnabled(false);

        final int[] remaining = {30};
        timerLabel.setText("Locked. Try again in " + remaining[0] + " seconds");
        timerLabel.setVisible(true);

        javax.swing.Timer t = new javax.swing.Timer(1000, ev -> {
            remaining[0]--;

            if (remaining[0] > 0) {
                timerLabel.setText("Locked. Try again in " + remaining[0] + " seconds");
            } else {
                ((javax.swing.Timer) ev.getSource()).stop();

                authService.resetLock(username);

                loginBtn.setEnabled(true);
                timerLabel.setVisible(false);

                JOptionPane.showMessageDialog(this, " You can now try logging in again.");
            }
        });

        t.start();
    }
}
