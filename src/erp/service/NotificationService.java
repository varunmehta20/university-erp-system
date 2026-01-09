package erp.service;

import erp.data.NotificationDAO;
import erp.domain.Notification;

import java.util.List;

public class NotificationService {

    private NotificationDAO notificationDAO = new NotificationDAO();
    public void notifyUser(int userId, String message) {
        notificationDAO.sendNotification(userId, message);
    }
    public void notifyAllStudentsInSection(int sectionId, String message) {
        List<Integer> studentIds = notificationDAO.getStudentIdsInSection(sectionId);
        for (Integer sid : studentIds) {
            notificationDAO.sendNotification(sid, message);
        }
    }

    public void notifyInstructorOfSection(int sectionId, String message) {
        Integer instructorId = notificationDAO.getInstructorIdOfSection(sectionId);
        if (instructorId != null) {
            notificationDAO.sendNotification(instructorId, message);
        }
    }

    public void notifyUsers(List<Integer> userIds, String message) {
        for (int id : userIds) {
            notificationDAO.sendNotification(id, message);
        }
    }

    public void notifyEntireSection(int sectionId, String message) {
        notifyInstructorOfSection(sectionId, message);
        notifyAllStudentsInSection(sectionId, message);
    }

    public void broadcastMaintenance(List<Integer> allUserIds, boolean isOn) {
        String msg = isOn
                ? "System is now in MAINTENANCE mode."
                : "System is now OUT of maintenance mode.";

        notifyUsers(allUserIds, msg);
    }

    public void notifyDeadline(int sectionId, String deadlineText) {
        String msg = "New deadline: " + deadlineText;
        notifyAllStudentsInSection(sectionId, msg);
    }

    public void notifyFinalGrade(int studentId, String grade) {
        String msg = "Your final grade has been calculated: " + grade;
        notifyUser(studentId, msg);
    }



    public List<Notification> getNotifications(int userId) {
        return notificationDAO.getNotifications(userId);
    }

    public void markAllAsRead(int userId) {
        notificationDAO.markAllAsRead(userId);
    }

    public int getUnreadCount(int userId) {
        return notificationDAO.getUnreadCount(userId);
    }
}
