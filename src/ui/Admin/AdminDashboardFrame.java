package ui.Admin;

import javax.swing.*;
import java.awt.*;
import ui.ERPMain;
import ui.Admin.panels.*;
import erp.service.AdminService;
import authen.*;

public class AdminDashboardFrame extends JPanel {

    private int adminId;
    private JPanel rightPanel;
    private CardLayout cardLayout;
    private AdminService adminService = new AdminService();

    public AdminDashboardFrame(int adminId) {
        this.adminId = adminId;
        setLayout(null);
        setBounds(0, 0, 1100, 700);

        JPanel sidebar = new JPanel(null);
        sidebar.setBounds(0, 0, 220, 700);
        sidebar.setBackground(new Color(40, 45, 60));
        add(sidebar);

        int y = 40;
        int gap = 50;

        JButton myProfileBtn = addButton(sidebar, "My Profile", y);          y += gap;
        JButton addUserBtn = addButton(sidebar, "Add User", y);              y += gap;
        JButton manageCoursesBtn = addButton(sidebar, "Manage Courses", y);  y += gap;
        JButton manageSectionsBtn = addButton(sidebar, "Manage Sections", y);y += gap;
        JButton assignInstructorBtn = addButton(sidebar, "Assign Instructor", y); y += gap;
        JButton maintenanceBtn = addButton(sidebar, "Maintenance Mode", y);  y += gap;
        JButton setDeadlineBtn = addButton(sidebar, "Set Deadline", y);      y += gap;
        JButton removeSectionBtn = addButton(sidebar, "Remove Section", y);  y += gap;
        JButton backupBtn = addButton(sidebar, "Backup / Restore", y);       y += gap;

        JButton logoutBtn = addButton(sidebar, "Logout", y);


        cardLayout = new CardLayout();
        rightPanel = new JPanel(cardLayout);
        rightPanel.setBounds(220, 0, 880, 700);
        add(rightPanel);

        rightPanel.add(new AddUserPanel(adminId), "ADD_USER");
        rightPanel.add(new CourseManagerPanel(adminId, cardLayout, rightPanel), "COURSE_MANAGER");
        rightPanel.add(new AddCoursePanel(adminId, cardLayout, rightPanel), "ADD_COURSE");
        rightPanel.add(new EditCoursePanel(adminId, cardLayout, rightPanel), "EDIT_COURSE");

        ManageSectionsPanel sm = new ManageSectionsPanel(adminId, rightPanel, cardLayout);
        rightPanel.add(sm, "SECTION_MANAGER");
        rightPanel.add(new AddSectionPanel(adminId, cardLayout, rightPanel), "ADD_SECTION");
        rightPanel.add(new EditSectionPanel(adminId, cardLayout, rightPanel), "EDIT_SECTION");

        rightPanel.add(new AssignInstructorPanel(adminId), "ASSIGN_INSTRUCTOR");
        rightPanel.add(new MaintenanceModePanel(adminId), "MAINTENANCE_MODE");
        rightPanel.add(new SetDeadlinePanel(adminId, cardLayout, rightPanel), "SET_DEADLINE");
        rightPanel.add(new BackupRestorePanel(adminId), "BACKUP_RESTORE");
        rightPanel.add(new RemoveSectionPanel(), "REMOVE_SECTION");

        String username = SetSession.getUsername();
        rightPanel.add(new AdminProfilePanel(username), "PROFILE");
        cardLayout.show(rightPanel, "PROFILE");

        myProfileBtn.addActionListener(e -> cardLayout.show(rightPanel, "PROFILE"));
        addUserBtn.addActionListener(e -> cardLayout.show(rightPanel, "ADD_USER"));
        manageCoursesBtn.addActionListener(e -> cardLayout.show(rightPanel, "COURSE_MANAGER"));
        manageSectionsBtn.addActionListener(e -> cardLayout.show(rightPanel, "SECTION_MANAGER"));
        assignInstructorBtn.addActionListener(e -> cardLayout.show(rightPanel, "ASSIGN_INSTRUCTOR"));
        maintenanceBtn.addActionListener(e -> cardLayout.show(rightPanel, "MAINTENANCE_MODE"));
        setDeadlineBtn.addActionListener(e -> cardLayout.show(rightPanel, "SET_DEADLINE"));
        backupBtn.addActionListener(e -> cardLayout.show(rightPanel, "BACKUP_RESTORE"));
        removeSectionBtn.addActionListener(e -> cardLayout.show(rightPanel, "REMOVE_SECTION"));

        logoutBtn.addActionListener(e -> ERPMain.showLogin());
    }

    private JButton addButton(JPanel panel, String text, int y) {
        JButton btn = new JButton(text);
        btn.setBounds(20, y, 180, 35);
        panel.add(btn);
        return btn;
    }
}
