package ui.Instructor;

import javax.swing.*;
import java.awt.*;

import ui.ERPMain;

import ui.Instructor.panels.*;

import erp.service.NotificationService;

public class InstructorDashboardFrame extends JPanel {

    private int instructorId;
    private JPanel rightPanel;
    private CardLayout cardLayout;

    private JLabel badgeLabel;
    private NotificationService notificationService;
    private InstructorNotificationsPanel notificationsPanel;

    public InstructorDashboardFrame(int instructorId) {

        this.instructorId = instructorId;
        this.notificationService = new NotificationService();

        setLayout(null);
        setBounds(0, 0, 1100, 700);

        JPanel sidebar = new JPanel(null);
        sidebar.setBounds(0, 0, 220, 700);
        sidebar.setBackground(new Color(40, 45, 60));
        add(sidebar);

        JButton mySectionsBtn = new JButton("My Sections");
        JButton gradingBtn = new JButton("Grading");
        JButton computeFinalBtn = new JButton("Compute Final Grades");
        JButton statsBtn = new JButton("Class Statistics");
        JButton notifBtn = new JButton("Notifications");
        JButton logoutBtn = new JButton("Logout");

        mySectionsBtn.setBounds(20, 110, 180, 35);
        gradingBtn.setBounds(20, 155, 180, 35);
        computeFinalBtn.setBounds(20, 200, 180, 35);
        statsBtn.setBounds(20, 245, 180, 35);
        notifBtn.setBounds(20, 290, 180, 35);
        logoutBtn.setBounds(20, 500, 180, 35);

        sidebar.add(mySectionsBtn);
        sidebar.add(gradingBtn);
        sidebar.add(computeFinalBtn);
        sidebar.add(statsBtn);
        sidebar.add(notifBtn);
        sidebar.add(logoutBtn);


        badgeLabel = new JLabel("");
        badgeLabel.setForeground(Color.WHITE);
        badgeLabel.setOpaque(true);
        badgeLabel.setBackground(Color.RED);
        badgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        badgeLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        badgeLabel.setBounds(165, 290, 30, 20);
        badgeLabel.setVisible(false);
        sidebar.add(badgeLabel);

        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);
        rightPanel.setBounds(220, 0, 880, 700);
        add(rightPanel);

        rightPanel.add(new MySectionsPanel(instructorId), "MY_SECTIONS");
        rightPanel.add(new GradingPanel(instructorId), "GRADING");
        rightPanel.add(new EnterScorePanel(instructorId), "ENTER_SCORES");
        rightPanel.add(new DefineComponentPanel(), "DEFINE_COMPONENTS");
        rightPanel.add(new ComputeFinalGradesPanel(instructorId), "COMPUTE_FINAL");
        rightPanel.add(new ClassStatsPanel(instructorId), "CLASS_STATS");

        notificationsPanel = new InstructorNotificationsPanel(instructorId);
        rightPanel.add(notificationsPanel, "NOTIFICATIONS");
        JButton profileBtn = new JButton("My Profile");
        profileBtn.setBounds(20, 60, 180, 35);
        sidebar.add(profileBtn);
        rightPanel.add(new InstructorProfilePanel(instructorId), "PROFILE");
        profileBtn.addActionListener(e -> cardLayout.show(rightPanel, "PROFILE"));

        cardLayout.show(rightPanel, "PROFILE");

        mySectionsBtn.addActionListener(e -> cardLayout.show(rightPanel, "MY_SECTIONS"));
        gradingBtn.addActionListener(e -> cardLayout.show(rightPanel, "GRADING"));
        computeFinalBtn.addActionListener(e -> cardLayout.show(rightPanel, "COMPUTE_FINAL"));
        statsBtn.addActionListener(e -> cardLayout.show(rightPanel, "CLASS_STATS"));

        notifBtn.addActionListener(e -> {
            notificationsPanel.refreshNotifications();
            notificationService.markAllAsRead(instructorId);
            updateUnreadCount();
            cardLayout.show(rightPanel, "NOTIFICATIONS");
        });

        logoutBtn.addActionListener(e -> ERPMain.showLogin());

        updateUnreadCount();
    }


    private void updateUnreadCount() {
        int count = notificationService.getUnreadCount(instructorId);
        badgeLabel.setVisible(count > 0);
        if (count > 0) {
            badgeLabel.setText(String.valueOf(count));
        }
    }

}
