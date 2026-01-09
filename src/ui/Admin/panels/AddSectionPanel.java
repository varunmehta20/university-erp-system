package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import erp.service.AdminService;
import erp.service.AuthService;
import erp.data.InstructorDAO;
import erp.domain.Course;
import erp.domain.Instructor;

public class AddSectionPanel extends JPanel {

    private final AdminService adminService = new AdminService();
    private final AuthService authService = new AuthService();
    private final InstructorDAO instructorDAO = new InstructorDAO();

    public AddSectionPanel(int adminId, CardLayout cardLayout, JPanel parentPanel) {

        setLayout(null);
        setBackground(Color.WHITE);

        JButton backBtn = new JButton("<- Back");
        backBtn.setBounds(20, 20, 100, 30);
        add(backBtn);
        backBtn.addActionListener(e -> cardLayout.show(parentPanel, "SECTION_MANAGER"));

        JLabel title = new JLabel("Add New Section", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 10, 880, 40);
        add(title);

        JLabel courseLabel = new JLabel("Course Code:");
        courseLabel.setBounds(100, 80, 100, 25);
        add(courseLabel);

        JComboBox<String> courseCodeDropdown = new JComboBox<>();
        courseCodeDropdown.setBounds(220, 80, 200, 30);
        add(courseCodeDropdown);

        List<Course> allCourses = adminService.getAllCourses();
        if (allCourses != null) {
            for (Course c : allCourses) {
                courseCodeDropdown.addItem(c.getCode());
            }
        }

        JLabel sectionNameLabel = new JLabel("Section Name:");
        sectionNameLabel.setBounds(100, 120, 120, 25);
        add(sectionNameLabel);

        JTextField sectionNameField = new JTextField();
        sectionNameField.setBounds(220, 120, 200, 30);
        add(sectionNameField);

        JLabel instructorLabel = new JLabel("Instructor:");
        instructorLabel.setBounds(100, 170, 100, 25);
        add(instructorLabel);

        JComboBox<String> instructorDropdown = new JComboBox<>();
        instructorDropdown.setBounds(220, 170, 250, 30);
        add(instructorDropdown);

        Map<String, Integer> instructorMap = new HashMap<>();

        List<Instructor> instructors = instructorDAO.listAll();
        if (instructors != null) {
            for (Instructor inst : instructors) {
                String username = authService.getUsernameByUserId(inst.getUserId());
                if (username == null) username = "unknown";
                String display = String.format("%s (ID: %d)", username, inst.getUserId());
                instructorDropdown.addItem(display);
                instructorMap.put(display, inst.getUserId());
            }
        }

        JLabel dayTimeLabel = new JLabel("Day/Time:");
        dayTimeLabel.setBounds(100, 220, 100, 25);
        add(dayTimeLabel);

        JTextField dayTimeField = new JTextField();
        dayTimeField.setBounds(220, 220, 200, 30);
        add(dayTimeField);

        JLabel roomLabel = new JLabel("Room:");
        roomLabel.setBounds(100, 270, 100, 25);
        add(roomLabel);

        JTextField roomField = new JTextField();
        roomField.setBounds(220, 270, 200, 30);
        add(roomField);

        JLabel capacityLabel = new JLabel("Capacity:");
        capacityLabel.setBounds(100, 320, 100, 25);
        add(capacityLabel);

        JTextField capacityField = new JTextField();
        capacityField.setBounds(220, 320, 200, 30);
        add(capacityField);

        JLabel semesterLabel = new JLabel("Semester:");
        semesterLabel.setBounds(100, 370, 100, 25);
        add(semesterLabel);

        JTextField semesterField = new JTextField();
        semesterField.setBounds(220, 370, 200, 30);
        add(semesterField);

        JLabel yearLabel = new JLabel("Year:");
        yearLabel.setBounds(100, 420, 100, 25);
        add(yearLabel);

        JTextField yearField = new JTextField();
        yearField.setBounds(220, 420, 200, 30);
        add(yearField);

        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setBounds(100, 470, 100, 25);
        add(statusLabel);

        JTextField statusField = new JTextField("OPEN");
        statusField.setBounds(220, 470, 200, 30);
        add(statusField);

        JButton createBtn = new JButton("Create Section");
        createBtn.setBounds(180, 520, 200, 40);
        add(createBtn);

        java.util.function.Function<String, Integer> safeParseInt = s -> {
            if (s == null) return null;
            s = s.replace("\u00A0", "");
            s = s.trim();
            if (s.isEmpty()) return null;
            try { return Integer.parseInt(s); }
            catch (Exception ex) { return null; }
        };

        createBtn.addActionListener(e -> {
            try {
                Object cs = courseCodeDropdown.getSelectedItem();
                if (cs == null) {
                    JOptionPane.showMessageDialog(this, "Please select a course code.");
                    return;
                }
                String code = cs.toString();
                int courseId = adminService.getCourseIdFromCode(code);
                if (courseId == -1) {
                    JOptionPane.showMessageDialog(this, "Invalid course code.");
                    return;
                }

                String sectionLabel = sectionNameField.getText().trim();
                if (sectionLabel.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter section name.");
                    return;
                }

                Object inst = instructorDropdown.getSelectedItem();
                if (inst == null) {
                    JOptionPane.showMessageDialog(this, "Please select an instructor.");
                    return;
                }
                Integer instructorId = instructorMap.get(inst.toString());
                if (instructorId == null) {
                    JOptionPane.showMessageDialog(this, "Cannot determine instructor ID.");
                    return;
                }

                String dayTime = dayTimeField.getText().trim();
                String room = roomField.getText().trim();
                Integer capacity = safeParseInt.apply(capacityField.getText());
                String semester = semesterField.getText().trim();
                Integer year = safeParseInt.apply(yearField.getText());
                String status = statusField.getText().trim();

                if (dayTime.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter Day/Time."); return; }
                if (room.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter Room."); return; }
                if (capacity == null) {
                    JOptionPane.showMessageDialog(this, "Capacity must be numeric.");
                    return;
                }
                if (capacity < 0) {
                    JOptionPane.showMessageDialog(this, "Capacity cannot be negative.");
                    return;
                }

                if (semester.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter Semester."); return; }
                if (year == null) { JOptionPane.showMessageDialog(this, "Year must be numeric."); return; }

                String result = adminService.createSection(
                        courseId,
                        instructorId,
                        dayTime,
                        room,
                        capacity,
                        semester,
                        year,
                        status,
                        sectionLabel
                );

                JOptionPane.showMessageDialog(this, result);

                sectionNameField.setText("");
                dayTimeField.setText("");
                roomField.setText("");
                capacityField.setText("");
                semesterField.setText("");
                yearField.setText("");
                statusField.setText("OPEN");

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage());
            }
        });
    }
}
