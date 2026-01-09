package ui.Instructor.panels;

import erp.domain.Notification;
import erp.service.NotificationService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InstructorNotificationsPanel extends JPanel {

    private int instructorId;
    private NotificationService notificationService;
    private DefaultListModel<String> model;

    public InstructorNotificationsPanel(int instructorId) {
        this.instructorId = instructorId;
        this.notificationService = new NotificationService();

        setLayout(new BorderLayout());

        model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);

        add(new JScrollPane(list), BorderLayout.CENTER);

        refreshNotifications();
    }

    public void refreshNotifications() {
        model.clear();
        List<Notification> all = notificationService.getNotifications(instructorId);

        for (Notification n : all) {
            model.addElement("• " + n.getMessage() + "   (" + n.getCreatedAt() + ")");
        }
    }
}
