package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import erp.service.AdminService;
import erp.service.AuthService;
import erp.data.CourseDAO;
import erp.data.SectionDAO;
import erp.data.SectionLabelDAO;
import erp.data.InstructorDAO;
import java.util.List;
import java.util.ArrayList;


import erp.domain.*;

public class EditSectionPanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private final CourseDAO courseDAO = new CourseDAO();
    private final SectionDAO sectionDAO = new SectionDAO();
    private final SectionLabelDAO labelDAO = new SectionLabelDAO();
    private final InstructorDAO instructorDAO = new InstructorDAO();
    private final AuthService authService = new AuthService();

    public EditSectionPanel(int adminId, CardLayout cardLayout, JPanel parentPanel) {

        setLayout(null);
        setBackground(Color.WHITE);

        JButton backBtn = new JButton("<- Back");
        backBtn.setBounds(20, 20, 100, 30);
        add(backBtn);
        backBtn.addActionListener(e -> cardLayout.show(parentPanel, "SECTION_MANAGER"));

        JLabel title = new JLabel("Edit Section", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 10, 880, 40);
        add(title);

        JLabel courseLabel = new JLabel("Course Code:");
        courseLabel.setBounds(100, 80, 120, 25);
        add(courseLabel);

        JComboBox<String> courseDropdown = new JComboBox<>();
        courseDropdown.setBounds(220, 80, 200, 30);
        add(courseDropdown);

        List<Course> courses = adminService.getAllCourses();
        if (courses != null) {
            for (Course c : courses) courseDropdown.addItem(c.getCode());
        }

        JLabel sectionLabel = new JLabel("Section:");
        sectionLabel.setBounds(100, 130, 120, 25);
        add(sectionLabel);

        JComboBox<String> sectionDropdown = new JComboBox<>();
        sectionDropdown.setBounds(220, 130, 200, 30);
        add(sectionDropdown);

        Map<String, Integer> labelToSectionId = new HashMap<>();

        courseDropdown.addActionListener(e -> {
            sectionDropdown.removeAllItems();
            labelToSectionId.clear();

            String code = (String) courseDropdown.getSelectedItem();
            if (code == null) return;

            int courseId = adminService.getCourseIdFromCode(code);
            List<SectionLabel> labels = labelDAO.listByCourseId(courseId);

            for (SectionLabel sl : labels) {
                String label = sl.getLabel();
                sectionDropdown.addItem(label);
                labelToSectionId.put(label, sl.getSectionId());
            }
        });


        JLabel instructorLBL = new JLabel("Instructor:");
        instructorLBL.setBounds(100, 180, 120, 25);
        add(instructorLBL);

        JComboBox<String> instructorDropdown = new JComboBox<>();
        instructorDropdown.setBounds(220, 180, 200, 30);
        add(instructorDropdown);

        Map<String, Integer> instructorMap = new HashMap<>();
        for (Instructor inst : instructorDAO.listAll()) {
            String username = authService.getUsernameByUserId(inst.getUserId());
            String display = username + " (ID: " + inst.getUserId() + ")";
            instructorDropdown.addItem(display);
            instructorMap.put(display, inst.getUserId());
        }

        JTextField dayTimeField = new JTextField();
        JLabel dtLBL = new JLabel("Day/Time:");
        dtLBL.setBounds(100, 230, 120, 25);
        dayTimeField.setBounds(220, 230, 200, 30);
        add(dtLBL);
        add(dayTimeField);

        JTextField roomField = new JTextField();
        JLabel roomLBL = new JLabel("Room:");
        roomLBL.setBounds(100, 280, 120, 25);
        roomField.setBounds(220, 280, 200, 30);
        add(roomLBL);
        add(roomField);

        JTextField capacityField = new JTextField();
        JLabel capLBL = new JLabel("Capacity:");
        capLBL.setBounds(100, 330, 120, 25);
        capacityField.setBounds(220, 330, 200, 30);
        add(capLBL);
        add(capacityField);

        JTextField semField = new JTextField();
        JLabel semLBL = new JLabel("Semester:");
        semLBL.setBounds(100, 380, 120, 25);
        semField.setBounds(220, 380, 200, 30);
        add(semLBL);
        add(semField);

        JTextField yearField = new JTextField();
        JLabel yearLBL = new JLabel("Year:");
        yearLBL.setBounds(100, 430, 120, 25);
        yearField.setBounds(220, 430, 200, 30);
        add(yearLBL);
        add(yearField);

        JTextField statusField = new JTextField();
        JLabel statusLBL = new JLabel("Status:");
        statusLBL.setBounds(100, 480, 120, 25);
        statusField.setBounds(220, 480, 200, 30);
        add(statusLBL);
        add(statusField);

        sectionDropdown.addActionListener(e -> {
            String label = (String) sectionDropdown.getSelectedItem();
            if (label == null) return;

            Integer sectionId = labelToSectionId.get(label);
            if (sectionId == null) return;

            Section section = sectionDAO.getById(sectionId);
            if (section == null) return;

            dayTimeField.setText(section.getDayTime());
            roomField.setText(section.getRoom());
            capacityField.setText(String.valueOf(section.getCapacity()));
            semField.setText(section.getSemester());
            yearField.setText(String.valueOf(section.getYear()));
            statusField.setText(section.getStatus());

            for (String key : instructorMap.keySet()) {
                if (instructorMap.get(key) == section.getInstructorId()) {
                    instructorDropdown.setSelectedItem(key);
                    break;
                }
            }
        });


        JButton updateBtn = new JButton("Update Section");
        updateBtn.setBounds(180, 540, 180, 40);
        add(updateBtn);

        updateBtn.addActionListener(e -> {

            String label = (String) sectionDropdown.getSelectedItem();
            if (label == null) {
                JOptionPane.showMessageDialog(this, "Select a section.");
                return;
            }

            int sectionId = labelToSectionId.get(label);
            int instructorId = instructorMap.get((String) instructorDropdown.getSelectedItem());

            try {
                int capacity = Integer.parseInt(capacityField.getText().trim());
                if (capacity < 0) {
                    JOptionPane.showMessageDialog(this, "Capacity cannot be negative.");
                    return;
                }

                int year = Integer.parseInt(yearField.getText().trim());

                String result = adminService.editSectionFull(
                        sectionId,
                        instructorId,
                        dayTimeField.getText().trim(),
                        roomField.getText().trim(),
                        capacity,
                        semField.getText().trim(),
                        year,
                        statusField.getText().trim()
                );

                JOptionPane.showMessageDialog(this, result);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Capacity and Year must be numbers.");
            }
        });
    }
}
