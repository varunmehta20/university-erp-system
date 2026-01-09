package ui;

import authen.Login;
import ui.Admin.AdminDashboardFrame;
import ui.Instructor.InstructorDashboardFrame;
import ui.Student.StudentDashboardFrame;

import javax.swing.*;

public class ERPMain {

    public static JFrame frame;

    public static void main(String[] args) {
        Login.resetFailedAttemptsOnStartup();

        frame = new JFrame("University ERP System");
        frame.setSize(900, 600);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        showLogin();
    }

    public static void showLogin() {
        frame.getContentPane().removeAll();
        frame.add(new LoginFrame());
        frame.revalidate();
        frame.repaint();
    }

    public static void showStudentDashboard(int studentId) {
        frame.getContentPane().removeAll();
        frame.add(new StudentDashboardFrame(studentId));
        frame.revalidate();
        frame.repaint();
    }

    public static void showInstructorDashboard(int instructorId) {
        frame.getContentPane().removeAll();
        frame.add(new InstructorDashboardFrame(instructorId));
        frame.revalidate();
        frame.repaint();
    }

    public static void showAdminDashboard(int adminId) {
        frame.getContentPane().removeAll();
        frame.add(new AdminDashboardFrame(adminId));
        frame.revalidate();
        frame.repaint();
    }
}
