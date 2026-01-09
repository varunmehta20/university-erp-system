package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;

public class CourseManagerPanel extends JPanel {

    public CourseManagerPanel(int adminId, CardLayout cardLayout, JPanel parentPanel) {
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Manage Courses", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 30, 880, 40);
        add(title);

        JButton addCourseBtn = new JButton("Add Course");
        addCourseBtn.setBounds(300, 150, 250, 45);
        add(addCourseBtn);

        JButton editCourseBtn = new JButton("Edit Course");
        editCourseBtn.setBounds(300, 220, 250, 45);
        add(editCourseBtn);

        addCourseBtn.addActionListener(e -> cardLayout.show(parentPanel, "ADD_COURSE"));
        editCourseBtn.addActionListener(e -> cardLayout.show(parentPanel, "EDIT_COURSE"));
    }
}
