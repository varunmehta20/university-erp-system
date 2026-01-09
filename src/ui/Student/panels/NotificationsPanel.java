package ui.Student.panels;

import erp.domain.Notification;
import erp.service.NotificationService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class NotificationsPanel extends JPanel {

    private int userId;
    private NotificationService notificationService;
    private DefaultListModel<String> model;
    private JList<String> list;

    public NotificationsPanel(int userId) {

        this.userId = userId;
        this.notificationService = new NotificationService();

        setLayout(new BorderLayout());

        model = new DefaultListModel<>();
        list = new JList<>(model);

        add(new JScrollPane(list), BorderLayout.CENTER);

        refreshNotifications();
    }

    public void refreshNotifications() {
        model.clear();

        List<Notification> notifications = notificationService.getNotifications(userId);

        for (Notification n : notifications) {
            model.addElement("• " + n.getMessage() + "     (" + n.getCreatedAt() + ")");
        }
    }
}
