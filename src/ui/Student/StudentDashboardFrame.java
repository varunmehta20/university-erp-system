//
//package ui.Student;
//
//import javax.swing.*;
//import java.awt.*;
//
//import ui.ERPMain;
//import ui.Student.panels.*;
//
//import erp.service.NotificationService;
//import ui.Student.panels.NotificationsPanel;
//
//
//public class StudentDashboardFrame extends JPanel {
//
//    private int studentId;
//    private JPanel rightPanel;
//    private CardLayout cardLayout;
//    private NotificationsPanel notificationsPanel;
//    private JLabel badgeLabel;
//    private NotificationService notificationService;
//
//    public StudentDashboardFrame(int studentId) {
//
//        this.studentId = studentId;
//        this.notificationService = new NotificationService();
//
//        setLayout(null);
//        setBounds(0, 0, 1100, 700);
//
//        JPanel sidebar = new JPanel(null);
//        sidebar.setBounds(0, 0, 220, 700);
//        sidebar.setBackground(new Color(40, 45, 60));
//        add(sidebar);
//
//        int y = 80, gap = 50;
//
//        JButton profileBtn       = makeBtn("My Profile", sidebar, y);              y+=gap;
//        JButton catalogBtn       = makeBtn("Browse Catalog", sidebar, y);          y+=gap;
//        JButton availableBtn     = makeBtn("Available Sections", sidebar, y);      y+=gap;
//        JButton registeredBtn    = makeBtn("My Registered Sections", sidebar, y);  y+=gap;
//        JButton gradesBtn        = makeBtn("My Grades", sidebar, y);               y+=gap;
//        JButton timetableBtn     = makeBtn("My Timetable", sidebar, y);            y+=gap;
//        JButton notificationsBtn = makeBtn("Notifications", sidebar, y);           y+=gap;
//
//        JButton logoutBtn = makeBtn("Logout", sidebar, 500);
//
//        badgeLabel = new JLabel("");
//        badgeLabel.setForeground(Color.WHITE);
//        badgeLabel.setOpaque(true);
//        badgeLabel.setBackground(Color.RED);
//        badgeLabel.setFont(new Font("Dialog", Font.BOLD, 11));
//        badgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
//        badgeLabel.setBounds(165, 80 + gap*6, 30, 20);
//        badgeLabel.setVisible(false);
//        sidebar.add(badgeLabel);
//
//        cardLayout = new CardLayout();
//        rightPanel = new JPanel(cardLayout);
//        rightPanel.setBounds(220, 0, 880, 700);
//        add(rightPanel);
//
//        rightPanel.add(new StudentProfilePanel(studentId),      "PROFILE");
//        rightPanel.add(new CatalogPanel(studentId),             "CATALOG");
//        rightPanel.add(new AvailableSectionsPanel(studentId),   "AVAILABLE");
//        rightPanel.add(new MyRegisteredSectionsPanel(studentId),"REGISTERED");
//        rightPanel.add(new GradesPanel(studentId),              "GRADES");
//        rightPanel.add(new TimetablePanel(studentId),           "TIMETABLE");
//
//        notificationsPanel = new NotificationsPanel(studentId);
//        rightPanel.add(notificationsPanel, "NOTIFICATIONS");
//
//        cardLayout.show(rightPanel, "PROFILE");
//
//        profileBtn.addActionListener(e -> cardLayout.show(rightPanel, "PROFILE"));
//        catalogBtn.addActionListener(e -> cardLayout.show(rightPanel, "CATALOG"));
//        availableBtn.addActionListener(e -> cardLayout.show(rightPanel, "AVAILABLE"));
//        registeredBtn.addActionListener(e -> cardLayout.show(rightPanel, "REGISTERED"));
//        gradesBtn.addActionListener(e -> cardLayout.show(rightPanel, "GRADES"));
//        timetableBtn.addActionListener(e -> cardLayout.show(rightPanel, "TIMETABLE"));
//
//        notificationsBtn.addActionListener(e -> {
//            cardLayout.show(rightPanel, "NOTIFICATIONS");
//            notificationsPanel.refreshNotifications();
//            notificationService.markAllAsRead(studentId);
//            updateUnreadCount();
//        });
//
//        logoutBtn.addActionListener(e -> ERPMain.showLogin());
//
//        updateUnreadCount();
//    }
//
//    private JButton makeBtn(String name, JPanel panel, int y) {
//        JButton btn = new JButton(name);
//        btn.setBounds(20, y, 180, 35);
//        panel.add(btn);
//        return btn;
//    }
//
//    private void updateUnreadCount() {
//        int count = notificationService.getUnreadCount(studentId);
//
//        if (count > 0) {
//            badgeLabel.setText(String.valueOf(count));
//            badgeLabel.setVisible(true);
//        } else {
//            badgeLabel.setVisible(false);
//        }
//    }
//}


package ui.Student;

import javax.swing.*;
import java.awt.*;

import ui.ERPMain;
import ui.Student.panels.*;

import erp.service.NotificationService;
import ui.Student.panels.NotificationsPanel;

public class StudentDashboardFrame extends JPanel {

    private int studentId;
    private JPanel rightPanel;
    private CardLayout cardLayout;
    private NotificationsPanel notificationsPanel;
    private JLabel badgeLabel;
    private NotificationService notificationService;

    // *** Added: to trigger load only on click ***
    private TimetablePanel timetablePanel;

    public StudentDashboardFrame(int studentId) {

        this.studentId = studentId;
        this.notificationService = new NotificationService();

        setLayout(null);
        setBounds(0, 0, 1100, 700);

        JPanel sidebar = new JPanel(null);
        sidebar.setBounds(0, 0, 220, 700);
        sidebar.setBackground(new Color(40, 45, 60));
        add(sidebar);

        int y = 80, gap = 50;

        JButton profileBtn       = makeBtn("My Profile", sidebar, y);              y+=gap;
        JButton catalogBtn       = makeBtn("Browse Catalog", sidebar, y);          y+=gap;
        JButton availableBtn     = makeBtn("Available Sections", sidebar, y);      y+=gap;
        JButton registeredBtn    = makeBtn("My Registered Sections", sidebar, y);  y+=gap;
        JButton gradesBtn        = makeBtn("My Grades", sidebar, y);               y+=gap;
        JButton timetableBtn     = makeBtn("My Timetable", sidebar, y);            y+=gap;
        JButton notificationsBtn = makeBtn("Notifications", sidebar, y);           y+=gap;

        JButton logoutBtn = makeBtn("Logout", sidebar, 500);

        badgeLabel = new JLabel("");
        badgeLabel.setForeground(Color.WHITE);
        badgeLabel.setOpaque(true);
        badgeLabel.setBackground(Color.RED);
        badgeLabel.setFont(new Font("Dialog", Font.BOLD, 11));
        badgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        badgeLabel.setBounds(165, 80 + gap*6, 30, 20);
        badgeLabel.setVisible(false);
        sidebar.add(badgeLabel);

        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);
        rightPanel.setBounds(220, 0, 880, 700);
        add(rightPanel);

        rightPanel.add(new StudentProfilePanel(studentId),      "PROFILE");
        rightPanel.add(new CatalogPanel(studentId),             "CATALOG");
        rightPanel.add(new AvailableSectionsPanel(studentId),   "AVAILABLE");
        rightPanel.add(new MyRegisteredSectionsPanel(studentId),"REGISTERED");
        rightPanel.add(new GradesPanel(studentId),              "GRADES");

        // *** Store timetablePanel so we can call load() when clicked ***
        timetablePanel = new TimetablePanel(studentId);
        rightPanel.add(timetablePanel, "TIMETABLE");

        notificationsPanel = new NotificationsPanel(studentId);
        rightPanel.add(notificationsPanel, "NOTIFICATIONS");

        cardLayout.show(rightPanel, "PROFILE");

        profileBtn.addActionListener(e -> cardLayout.show(rightPanel, "PROFILE"));
        catalogBtn.addActionListener(e -> cardLayout.show(rightPanel, "CATALOG"));
        availableBtn.addActionListener(e -> cardLayout.show(rightPanel, "AVAILABLE"));
        registeredBtn.addActionListener(e -> cardLayout.show(rightPanel, "REGISTERED"));
        gradesBtn.addActionListener(e -> cardLayout.show(rightPanel, "GRADES"));

        // *** FIXED TIMETABLE BUTTON — loads only when clicked ***
        timetableBtn.addActionListener(e -> {
            cardLayout.show(rightPanel, "TIMETABLE");
            timetablePanel.loadTriggeredByUser = true;
            timetablePanel.load(studentId);
        });

        notificationsBtn.addActionListener(e -> {
            cardLayout.show(rightPanel, "NOTIFICATIONS");
            notificationsPanel.refreshNotifications();
            notificationService.markAllAsRead(studentId);
            updateUnreadCount();
        });

        logoutBtn.addActionListener(e -> ERPMain.showLogin());

        updateUnreadCount();
    }

    private JButton makeBtn(String name, JPanel panel, int y) {
        JButton btn = new JButton(name);
        btn.setBounds(20, y, 180, 35);
        panel.add(btn);
        return btn;
    }

    private void updateUnreadCount() {
        int count = notificationService.getUnreadCount(studentId);

        if (count > 0) {
            badgeLabel.setText(String.valueOf(count));
            badgeLabel.setVisible(true);
        } else {
            badgeLabel.setVisible(false);
        }
    }
}
