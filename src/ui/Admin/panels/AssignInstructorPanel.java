package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;

import erp.service.AdminService;
import erp.service.AuthService;

import erp.domain.Section;
import erp.domain.SectionLabel;
import erp.domain.Instructor;

import erp.data.SectionDAO;
import erp.data.SectionLabelDAO;
import erp.data.CourseDAO;
import erp.data.InstructorDAO;

import java.util.List;

public class AssignInstructorPanel extends JPanel {

    private AdminService adminService = new AdminService();
    private AuthService authService = new AuthService();   // <-- ADDED
    private SectionDAO sectionDAO = new SectionDAO();
    private SectionLabelDAO labelDAO = new SectionLabelDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private InstructorDAO instructorDAO = new InstructorDAO();

    public AssignInstructorPanel(int adminId) {

        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Assign Instructor to Section", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 10, 880, 40);
        add(title);

        JLabel sectionLbl = new JLabel("Select Section:");
        sectionLbl.setBounds(100, 120, 150, 25);
        add(sectionLbl);

        JComboBox<String> sectionDropdown = new JComboBox<>();
        sectionDropdown.setBounds(250, 120, 350, 30);
        add(sectionDropdown);

        List<Section> sections = sectionDAO.listAllSections();
        for (Section s : sections) {

            SectionLabel sl = labelDAO.getBySectionId(s.getId());
            String label = (sl != null) ? sl.getLabel() : "(No Label)";

            String courseCode = "UnknownCourse";
            try {
                courseCode = courseDAO.getById(s.getCourseId()).getCode();
            } catch (Exception ignored) {}

            String display = courseCode + " - " + label + " (ID: " + s.getId() + ")";
            sectionDropdown.addItem(display);
        }

        JLabel instructorLbl = new JLabel("Select Instructor:");
        instructorLbl.setBounds(100, 180, 150, 25);
        add(instructorLbl);

        JComboBox<String> instructorDropdown = new JComboBox<>();
        instructorDropdown.setBounds(250, 180, 350, 30);
        add(instructorDropdown);

        List<Instructor> instructors = instructorDAO.listAll();
        for (Instructor inst : instructors) {

            String username = authService.getUsernameByUserId(inst.getUserId());
            if (username == null) username = "(Unknown)";

            String display = username + " (ID: " + inst.getUserId() + ")";
            instructorDropdown.addItem(display);
        }

        JButton assignBtn = new JButton("Assign");
        assignBtn.setBounds(250, 250, 180, 40);
        add(assignBtn);

        assignBtn.addActionListener(e -> {
            try {
                if (sectionDropdown.getSelectedIndex() == -1 ||
                        instructorDropdown.getSelectedIndex() == -1) {

                    JOptionPane.showMessageDialog(this, "Please select BOTH fields.");
                    return;
                }

                String selected = (String) sectionDropdown.getSelectedItem();
                int sectionId = Integer.parseInt(
                        selected.substring(selected.indexOf("ID: ") + 4, selected.indexOf(")"))
                );

                String instText = (String) instructorDropdown.getSelectedItem();
                int instructorId = Integer.parseInt(
                        instText.substring(instText.indexOf("ID: ") + 4, instText.indexOf(")"))
                );

                String result = adminService.assignInstructorToSection(sectionId, instructorId);
                JOptionPane.showMessageDialog(this, result);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

    }
}

