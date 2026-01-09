package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;
import erp.service.AdminService;

public class AddCoursePanel extends JPanel {

    private AdminService adminService = new AdminService();

    public AddCoursePanel(int adminId, CardLayout cardLayout, JPanel parentPanel) {

        setLayout(null);
        setBackground(Color.WHITE);

        JButton backBtn = new JButton("← Back");
        backBtn.setBounds(20, 20, 100, 30);
        add(backBtn);

        backBtn.addActionListener(e ->
                cardLayout.show(parentPanel, "COURSE_MANAGER")
        );

        JLabel title = new JLabel("Add New Course", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 30, 880, 40);
        add(title);

        JLabel codeLabel = new JLabel("Course Code:");
        codeLabel.setBounds(200, 120, 120, 25);
        add(codeLabel);

        JTextField codeField = new JTextField();
        codeField.setBounds(320, 120, 200, 30);
        add(codeField);

        JLabel nameLabel = new JLabel("Course Title:");
        nameLabel.setBounds(200, 170, 120, 25);
        add(nameLabel);

        JTextField nameField = new JTextField();
        nameField.setBounds(320, 170, 200, 30);
        add(nameField);

        JLabel creditsLabel = new JLabel("Credits:");
        creditsLabel.setBounds(200, 220, 120, 25);
        add(creditsLabel);

        JTextField creditsField = new JTextField();
        creditsField.setBounds(320, 220, 200, 30);
        add(creditsField);

        JButton addBtn = new JButton("Add Course");
        addBtn.setBounds(320, 280, 150, 35);
        add(addBtn);

        addBtn.addActionListener(e -> {
            try {
                String code = codeField.getText().trim();
                String title2 = nameField.getText().trim();
                int credits = Integer.parseInt(creditsField.getText().trim());

                String result = adminService.createCourse(code, title2, credits);
                JOptionPane.showMessageDialog(this, result);

                codeField.setText("");
                nameField.setText("");
                creditsField.setText("");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}
