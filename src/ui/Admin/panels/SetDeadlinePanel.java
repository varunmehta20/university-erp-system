package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;
import erp.service.AdminService;

public class SetDeadlinePanel extends JPanel {

    private AdminService adminService = new AdminService();

    public SetDeadlinePanel(int adminId, CardLayout layout, JPanel parent) {

        setLayout(null);
        setBackground(Color.WHITE);

        JButton backBtn = new JButton("← Back");
        backBtn.setBounds(20, 20, 100, 30);
        add(backBtn);

        backBtn.addActionListener(e -> layout.show(parent, "ADMIN_HOME"));

        JLabel title = new JLabel("Set Drop/Add Deadline", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 10, 880, 40);
        add(title);

        JLabel currentLabel = new JLabel("Current Deadline:");
        currentLabel.setBounds(100, 120, 150, 25);
        add(currentLabel);

        JLabel currentDeadline = new JLabel(adminService.getDropDeadline());
        currentDeadline.setBounds(260, 120, 500, 25);
        add(currentDeadline);

        JLabel newDeadlineLabel = new JLabel("New Deadline (yyyy-MM-dd HH:mm:ss):");
        newDeadlineLabel.setBounds(100, 180, 300, 25);
        add(newDeadlineLabel);

        JTextField newDeadlineField = new JTextField();
        newDeadlineField.setBounds(400, 180, 250, 30);
        add(newDeadlineField);

        JButton setBtn = new JButton("Set Deadline");
        setBtn.setBounds(300, 250, 180, 40);
        add(setBtn);

        setBtn.addActionListener(e -> {
            String newDeadline = newDeadlineField.getText().trim();

            if (newDeadline.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a deadline.");
                return;
            }

            String result = adminService.setDropDeadline(newDeadline);
            JOptionPane.showMessageDialog(this, result);

            currentDeadline.setText(adminService.getDropDeadline());
            newDeadlineField.setText("");
        });
    }
}