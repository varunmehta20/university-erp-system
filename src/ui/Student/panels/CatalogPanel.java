package ui.Student.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import erp.service.StudentService;
import erp.domain.Course;
import erp.domain.Section;

public class CatalogPanel extends JPanel {

    private StudentService studentService;
    private JTable catalogTable;
    private DefaultTableModel model;

    public CatalogPanel(int studentId) {
        setLayout(null);
        studentService = new StudentService();

        JLabel title = new JLabel("📘 Browse Course Catalog");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(220, 20, 300, 30);
        add(title);

        model = new DefaultTableModel(
                new String[]{"Course Code", "Title", "Credits", "Instructor"}, 0
        );



        catalogTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(catalogTable);
        scrollPane.setBounds(40, 80, 600, 400);
        add(scrollPane);

        loadCatalogData();
    }

    private void loadCatalogData() {
        model.setRowCount(0);

        List<Course> courses = studentService.getAllCourses();

        for (Course course : courses) {

            String instructor = studentService.getInstructorForCourse(course.getId());

            model.addRow(new Object[]{
                    course.getCode(),
                    course.getTitle(),
                    course.getCredits(),
                    instructor
            });
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No courses found.");
        }
    }




}