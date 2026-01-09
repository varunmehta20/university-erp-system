package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;
import erp.service.AdminService;

public class BackupRestorePanel extends JPanel {

    private final AdminService adminService = new AdminService();

    public BackupRestorePanel(int adminId) {
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Database Backup / Restore", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 20, 880, 40);
        add(title);

        JLabel info = new JLabel("Backup file location: C:/erp_backups/erp_backup.sql", JLabel.CENTER);
        info.setBounds(0, 80, 880, 30);
        add(info);

        JButton backupBtn = new JButton("Backup Database");
        backupBtn.setBounds(320, 150, 220, 40);
        add(backupBtn);

        JButton restoreBtn = new JButton("Restore Database");
        restoreBtn.setBounds(320, 220, 220, 40);
        add(restoreBtn);

        backupBtn.addActionListener(e -> {
            String msg = adminService.backupDatabase();
            JOptionPane.showMessageDialog(this, msg);
        });

        restoreBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "This will overwrite current data with the backup.\nAre you sure?",
                    "Confirm Restore",
                    JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                String msg = adminService.restoreDatabase();
                JOptionPane.showMessageDialog(this, msg);
            }
        });
    }
}
