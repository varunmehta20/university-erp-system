package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;

public class AddUserPanel extends JPanel {

    public AddUserPanel(int adminId) {

        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Add New User", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(0, 10, 880, 40);
        add(title);

        JButton studentBtn = new JButton("Student");
        JButton instructorBtn = new JButton("Instructor");
        JButton adminBtn = new JButton("Admin");

        studentBtn.setBounds(80, 70, 150, 35);
        instructorBtn.setBounds(260, 70, 150, 35);
        adminBtn.setBounds(440, 70, 150, 35);

        add(studentBtn);
        add(instructorBtn);
        add(adminBtn);

        JPanel cardPanel = new JPanel(new CardLayout());
        cardPanel.setBounds(0, 120, 880, 430);

        AddStudentPanel studentPanel = new AddStudentPanel(adminId);
        AddInstructorPanel instructorPanel = new AddInstructorPanel(adminId);
        AddAdminPanel adminPanel = new AddAdminPanel(adminId);

        cardPanel.add(studentPanel, "STUDENT");
        cardPanel.add(instructorPanel, "INSTRUCTOR");
        cardPanel.add(adminPanel, "ADMIN");

        add(cardPanel);

        CardLayout cl = (CardLayout) cardPanel.getLayout();

        studentBtn.addActionListener(e -> cl.show(cardPanel, "STUDENT"));
        instructorBtn.addActionListener(e -> cl.show(cardPanel, "INSTRUCTOR"));
        adminBtn.addActionListener(e -> cl.show(cardPanel, "ADMIN"));
    }
}
