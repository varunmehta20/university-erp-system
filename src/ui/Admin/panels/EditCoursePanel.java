package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;
import erp.service.AdminService;
import erp.domain.Course;

public class EditCoursePanel extends JPanel {

    private AdminService adminService = new AdminService();

    public EditCoursePanel(int adminId, CardLayout cardLayout, JPanel parentPanel) {

        setLayout(null);
        setBackground(Color.WHITE);

        JButton backBtn = new JButton("← Back");
        backBtn.setBounds(20, 20, 100, 30);
        add(backBtn);
        backBtn.addActionListener(e -> cardLayout.show(parentPanel, "COURSE_MANAGER"));

        JLabel title = new JLabel("Edit Existing Course", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 30, 880, 40);
        add(title);

        JLabel codeSelectLabel = new JLabel("Select Course:");
        codeSelectLabel.setBounds(200, 120, 120, 25);
        add(codeSelectLabel);

        JComboBox<String> courseDropdown = new JComboBox<>();
        courseDropdown.setBounds(320, 120, 200, 30);
        add(courseDropdown);

        adminService.getAllCourses().forEach(c -> courseDropdown.addItem(c.getCode()));

        JLabel codeLabel = new JLabel("New Code:");
        codeLabel.setBounds(200, 170, 120, 25);
        add(codeLabel);

        JTextField codeField = new JTextField();
        codeField.setBounds(320, 170, 200, 30);
        add(codeField);

        JLabel titleLabel = new JLabel("New Title:");
        titleLabel.setBounds(200, 220, 120, 25);
        add(titleLabel);

        JTextField titleField = new JTextField();
        titleField.setBounds(320, 220, 200, 30);
        add(titleField);

        JLabel creditsLabel = new JLabel("New Credits:");
        creditsLabel.setBounds(200, 270, 120, 25);
        add(creditsLabel);

        JTextField creditsField = new JTextField();
        creditsField.setBounds(320, 270, 200, 30);
        add(creditsField);

        courseDropdown.addActionListener(e -> {
            String selectedCode = (String) courseDropdown.getSelectedItem();
            if (selectedCode == null) return;

            Course c = adminService.getCourseByCode(selectedCode);
            if (c == null) return;

            codeField.setText(c.getCode());
            titleField.setText(c.getTitle());
            creditsField.setText(String.valueOf(c.getCredits()));

            courseDropdown.putClientProperty("selectedCourseId", c.getId());
        });

        JButton editBtn = new JButton("Update Course");
        editBtn.setBounds(320, 330, 150, 35);
        add(editBtn);

        editBtn.addActionListener(e -> {
            try {
                int selectedId = (int) courseDropdown.getClientProperty("selectedCourseId");
                String newCode = codeField.getText().trim();
                String newTitle = titleField.getText().trim();
                int newCredits = Integer.parseInt(creditsField.getText().trim());

                String result = adminService.editCourse(selectedId, newCode, newTitle, newCredits);
                JOptionPane.showMessageDialog(this, result);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}

