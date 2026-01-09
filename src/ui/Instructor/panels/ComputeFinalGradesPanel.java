package ui.Instructor.panels;

import erp.data.SectionLabelDAO;
import erp.domain.Section;
import erp.domain.SectionLabel;
import erp.service.InstructorService;
import erp.domain.Grade;
import erp.data.GradeDAO;
import erp.data.EnrollmentDAO;
import erp.domain.Enrollment;
import erp.service.AuthService;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class ComputeFinalGradesPanel extends JPanel {

    private JComboBox<SectionLabel> sectionDropdown;
    private JButton computeBtn;
    private JButton exportBtn;

    private InstructorService instructorService;
    private SectionLabelDAO labelDAO;

    public ComputeFinalGradesPanel(int instructorId) {

        this.instructorService = new InstructorService();
        this.labelDAO = new SectionLabelDAO();

        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel title = new JLabel("Compute Final Grades");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setBorder(BorderFactory.createEmptyBorder(40, 150, 20, 0));
        titlePanel.add(title);

        add(titlePanel, BorderLayout.NORTH);

        JPanel controlsPanel = new JPanel(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.gridwidth = 1;

        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        controlsPanel.add(new JLabel("Select Section:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        sectionDropdown = new JComboBox<>();
        loadSectionsForInstructor(instructorId);
        controlsPanel.add(sectionDropdown, gbc);

        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        computeBtn = new JButton("Compute Final Grades");
        computeBtn.setPreferredSize(new Dimension(350, 50));
        controlsPanel.add(computeBtn, gbc);

        computeBtn.addActionListener(e -> runCompute());

        gbc.gridy = 2;
        exportBtn = new JButton("Export Grades CSV");
        exportBtn.setPreferredSize(new Dimension(350, 50));
        controlsPanel.add(exportBtn, gbc);

        exportBtn.addActionListener(e -> exportCSV());

        JPanel centerWrapper = new JPanel(new GridBagLayout());

        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.anchor = GridBagConstraints.WEST;
        wrapperGbc.weightx = 1.0;
        wrapperGbc.insets = new Insets(0, 150, 0, 0);

        centerWrapper.add(controlsPanel, wrapperGbc);

        GridBagConstraints fillerGbc = new GridBagConstraints();
        fillerGbc.gridy = 1;
        fillerGbc.weighty = 1.0;
        centerWrapper.add(new JPanel(), fillerGbc);

        add(centerWrapper, BorderLayout.CENTER);
    }

    private String convertToLetterGrade(Double finalGrade) {
        if (finalGrade == null) return "";

        double g = finalGrade;

        if (g >= 90) return "A";
        else if (g >= 80) return "B";
        else if (g >= 70) return "C";
        else if (g >= 60) return "D";
        else return "F";
    }

    private void loadSectionsForInstructor(int instructorId) {
        List<Section> mySections = instructorService.getMySections(instructorId);

        for (Section s : mySections) {

            SectionLabel labelObj = labelDAO.getBySectionId(s.getId());

            if (labelObj != null) {
                sectionDropdown.addItem(labelObj);
            } else {
                sectionDropdown.addItem(
                        new SectionLabel(
                                0,
                                s.getId(),
                                s.getCourseId(),
                                "Section " + s.getId()
                        )
                );
            }
        }
    }

    private void runCompute() {
        try {
            SectionLabel selected = (SectionLabel) sectionDropdown.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a section!");
                return;
            }

            int sectionId = selected.getSectionId();
            String result = instructorService.computeFinalGrades(sectionId);

            JOptionPane.showMessageDialog(this, result);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error computing grades: " + ex.getMessage());
        }
    }

    private void exportCSV() {
        try {
            SectionLabel selected = (SectionLabel) sectionDropdown.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a section!");
                return;
            }

            int sectionId = selected.getSectionId();

            String fileName = JOptionPane.showInputDialog(this, "Enter CSV file name (without extension or with .csv):");
            if (fileName == null || fileName.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Export cancelled. No file name entered.");
                return;
            }

            fileName = fileName.trim();
            if (!fileName.toLowerCase().endsWith(".csv")) {
                fileName += ".csv";
            }

            String filePath = System.getProperty("user.dir") + "/" + fileName;

            GradeDAO gradeDAO = new GradeDAO();
            EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
            AuthService authService = new AuthService();

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

                // Updated CSV Header
                writer.write("username,component,score,finalGrade,letterGrade");
                writer.newLine();

                List<Grade> grades = gradeDAO.getGradesBySection(sectionId);
                if (grades == null || grades.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "INFO: No grades found for section " + sectionId + ". File not created.");
                    return;
                }

                for (Grade g : grades) {
                    int studentId = -1;
                    Enrollment en = enrollmentDAO.getEnrollmentById(g.getEnrollmentId());
                    if (en != null) studentId = en.getStudentId();

                    String username = (studentId > 0) ? authService.getUsernameByUserId(studentId) : "";

                    Double fg = g.getFinalGrade();
                    String letter = convertToLetterGrade(fg);

                    String line = username + "," +
                            (g.getComponent() != null ? g.getComponent() : "") + "," +
                            g.getScore() + "," +
                            (fg != null ? fg : "") + "," +
                            letter;

                    writer.write(line);
                    writer.newLine();
                }

                JOptionPane.showMessageDialog(this, "SUCCESS: Grades exported to " + filePath);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "ERROR: Failed to write CSV: " + e.getMessage());
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage());
        }
    }
}