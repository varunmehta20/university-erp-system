package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;
import erp.service.AdminService;

public class MaintenanceModePanel extends JPanel {

    private AdminService adminService = new AdminService();
    private JLabel statusLabel;

    public MaintenanceModePanel(int adminId) {
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Maintenance Mode", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 10, 880, 40);
        add(title);

        statusLabel = new JLabel("Checking status...", JLabel.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        statusLabel.setBounds(250, 100, 400, 30);
        add(statusLabel);

        JButton enableBtn = new JButton("Enable (ON)");
        enableBtn.setBounds(250, 180, 150, 40);
        add(enableBtn);

        JButton disableBtn = new JButton("Disable (OFF)");
        disableBtn.setBounds(420, 180, 150, 40);
        add(disableBtn);

        enableBtn.addActionListener(e -> {
            String result = adminService.toggleMaintenance(true);
            JOptionPane.showMessageDialog(this, result);
            updateStatus();
        });

        disableBtn.addActionListener(e -> {
            String result = adminService.toggleMaintenance(false);
            JOptionPane.showMessageDialog(this, result);
            updateStatus();
        });

        updateStatus();
    }

    private void updateStatus() {
        String status = adminService.getMaintenanceStatus();
        if (status.contains("ON")) {
            statusLabel.setText(" Maintenance Mode: ON");
        } else {
            statusLabel.setText(" Maintenance Mode: OFF");
        }
    }
}
