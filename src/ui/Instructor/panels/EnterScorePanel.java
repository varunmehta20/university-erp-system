package ui.Instructor.panels;

import erp.data.AssessmentDAO;
import erp.data.EnrollmentDAO;
import erp.data.GradeDAO;
import erp.data.SectionDAO;
import erp.data.SectionLabelDAO;
import erp.domain.AssessmentComponent;
import erp.domain.Enrollment;
import erp.domain.Grade;
import erp.domain.Section;
import erp.domain.SectionLabel;
import erp.service.AuthService;
import erp.data.NotificationDAO;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnterScorePanel extends JPanel {

    private final int instructorId;

    private final JComboBox<String> sectionCombo;
    private final JComboBox<String> componentCombo;
    private final JComboBox<String> studentCombo;
    private final JTextField scoreField;
    private final JButton submitBtn;

    private final SectionDAO sectionDAO = new SectionDAO();
    private final SectionLabelDAO sectionLabelDAO = new SectionLabelDAO();
    private final AssessmentDAO assessmentDAO = new AssessmentDAO();
    private final EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
    private final GradeDAO gradeDAO = new GradeDAO();
    private final AuthService authService = new AuthService();
    private final NotificationDAO notificationDAO= new NotificationDAO();

    private final Map<String, Integer> sectionLabelToSectionId = new HashMap<>();
    private final Map<String, Integer> studentNameToEnrollmentId = new HashMap<>();

    public EnterScorePanel(int instructorId) {
        this.instructorId = instructorId;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        JLabel title = new JLabel("Enter Student Score", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);
        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Section:"), gbc);
        sectionCombo = new JComboBox<>();
        gbc.gridx = 1;
        add(sectionCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Component:"), gbc);
        componentCombo = new JComboBox<>();
        gbc.gridx = 1;
        add(componentCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Student:"), gbc);
        studentCombo = new JComboBox<>();
        gbc.gridx = 1;
        add(studentCombo, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Score (0 - 100):"), gbc);
        scoreField = new JTextField();
        gbc.gridx = 1;
        add(scoreField, gbc);

        submitBtn = new JButton("Submit Score");
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(submitBtn, gbc);

        JButton importBtn = new JButton("Import Grades CSV");
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(importBtn, gbc);

        importBtn.addActionListener(e -> {
            String fileName = JOptionPane.showInputDialog(this, "Enter CSV file name (full path or relative):");
            if (fileName == null || fileName.isBlank()) return;

            String msg = importGradesCSV(fileName);
            JOptionPane.showMessageDialog(this, msg);
        });

        loadSectionsForInstructor();

        sectionCombo.addActionListener(e -> {
            String selectedLabel = (String) sectionCombo.getSelectedItem();
            if (selectedLabel != null) {
                Integer sectionId = sectionLabelToSectionId.get(selectedLabel);
                if (sectionId != null) {
                    loadComponentsForSection(sectionId);
                    loadStudentsForSection(sectionId);
                }
            }
        });

        submitBtn.addActionListener(e -> handleSubmit());
    }

    private void loadSectionsForInstructor() {
        sectionCombo.removeAllItems();
        sectionLabelToSectionId.clear();

        List<Section> sections = sectionDAO.getSectionsByInstructor(instructorId);
        if (sections == null || sections.isEmpty()) {
            sectionCombo.addItem("No sections assigned");
            sectionCombo.setEnabled(false);
            componentCombo.setEnabled(false);
            studentCombo.setEnabled(false);
            submitBtn.setEnabled(false);
            return;
        }

        for (Section s : sections) {
            SectionLabel sl = sectionLabelDAO.getBySectionId(s.getId());
            String label = (sl != null && sl.getLabel() != null && !sl.getLabel().isBlank())
                    ? sl.getLabel() + " (id:" + s.getId() + ")"
                    : "Section " + s.getId() + " (Course " + s.getCourseId() + ")";
            sectionCombo.addItem(label);
            sectionLabelToSectionId.put(label, s.getId());
        }

        sectionCombo.setEnabled(true);
        if (sectionCombo.getItemCount() > 0) sectionCombo.setSelectedIndex(0);

    }

    private void loadComponentsForSection(int sectionId) {
        componentCombo.removeAllItems();
        List<AssessmentComponent> comps = assessmentDAO.getComponents(sectionId);
        if (comps == null || comps.isEmpty()) {
            componentCombo.addItem("No components defined");
            componentCombo.setEnabled(false);
            return;
        }
        for (AssessmentComponent c : comps) componentCombo.addItem(c.getName());
        componentCombo.setEnabled(true);
    }

    private void loadStudentsForSection(int sectionId) {
        studentCombo.removeAllItems();
        studentNameToEnrollmentId.clear();

        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsBySection(sectionId);
        if (enrollments == null || enrollments.isEmpty()) {
            studentCombo.addItem("No students enrolled");
            studentCombo.setEnabled(false);
            return;
        }

        for (Enrollment en : enrollments) {
            if ("DROPPED".equalsIgnoreCase(en.getStatus())) continue;
            String username = authService.getUsernameByUserId(en.getStudentId());
            String displayName = (username != null && !username.isBlank())
                    ? username + " (sid:" + en.getStudentId() + ")"
                    : "Student " + en.getStudentId();
            studentCombo.addItem(displayName);
            studentNameToEnrollmentId.put(displayName, en.getId());
        }

        if (studentCombo.getItemCount() == 0) {
            studentCombo.addItem("No active students");
            studentCombo.setEnabled(false);
        } else {
            studentCombo.setEnabled(true);
            studentCombo.setSelectedIndex(0);
        }
    }

    private void handleSubmit() {
        try {
            String sectionLabel = (String) sectionCombo.getSelectedItem();
            if (sectionLabel == null || !sectionLabelToSectionId.containsKey(sectionLabel)) {
                JOptionPane.showMessageDialog(this, "Select a valid section.");
                return;
            }
            int sectionId = sectionLabelToSectionId.get(sectionLabel);

            String component = (String) componentCombo.getSelectedItem();
            if (component == null || component.startsWith("No components")) {
                JOptionPane.showMessageDialog(this, "Select a component.");
                return;
            }

            String studentDisplay = (String) studentCombo.getSelectedItem();
            if (studentDisplay == null || !studentNameToEnrollmentId.containsKey(studentDisplay)) {
                JOptionPane.showMessageDialog(this, "Select a student.");
                return;
            }
            int enrollmentId = studentNameToEnrollmentId.get(studentDisplay);

            double score;
            try {
                score = Double.parseDouble(scoreField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Enter valid score (0-100).");
                return;
            }
            if (score < 0 || score > 100) {
                JOptionPane.showMessageDialog(this, "Score must be 0-100.");
                return;
            }

            Grade g = new Grade();
            Enrollment en = enrollmentDAO.getEnrollmentById(enrollmentId);
            g.setEnrollmentId(enrollmentId);
            g.setComponent(component);
            g.setScore(score);

            boolean ok = gradeDAO.insertOrUpdateScore(g);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Score saved successfully.");
                scoreField.setText("");
                notificationDAO.sendNotification(
                        en.getStudentId(),
                        "Your score for '" + component + "' has been updated in Section " + sectionId + "."
                );
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save score.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String importGradesCSV(String filePath) {
        StringBuilder msg = new StringBuilder();
        int inserted = 0, skippedComp = 0, skippedStudent = 0, skippedSection = 0;

        try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(filePath))) {
            String line;
            int lineNo = 0;

            while ((line = br.readLine()) != null) {
                lineNo++;
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", -1);
                if (parts.length != 4) {
                    msg.append("Line ").append(lineNo).append(" skipped: Invalid format (need 4 fields)\n");
                    continue;
                }

                String sectionLabel = parts[0].trim();
                String compName = parts[1].trim();
                String username = parts[2].trim();
                String scoreStr = parts[3].trim();

                SectionLabel sl = sectionLabelDAO.getByLabel(sectionLabel);
                if (sl == null) {
                    msg.append("Line ").append(lineNo).append(" skipped: Section '").append(sectionLabel)
                            .append("' not found.\n");
                    skippedSection++;
                    continue;
                }
                int sectionId = sl.getSectionId();

                double weight = assessmentDAO.getWeightForComponent(sectionId, compName);
                if (weight <= 0.0) {
                    msg.append("Line ").append(lineNo).append(" skipped: Component '")
                            .append(compName).append("' not found in section ").append(sectionLabel).append("\n");
                    skippedComp++;
                    continue;
                }

                int studentId = authService.getUserIdByUsername(username);
                if (studentId <= 0) {
                    msg.append("Line ").append(lineNo).append(" skipped: Student '").append(username)
                            .append("' not found.\n");
                    skippedStudent++;
                    continue;
                }

                Enrollment en = enrollmentDAO.getByStudentAndSection(studentId, sectionId);
                if (en == null) {
                    msg.append("Line ").append(lineNo).append(" skipped: Student '")
                            .append(username).append("' not enrolled in section ").append(sectionLabel).append("\n");
                    skippedStudent++;
                    continue;
                }

                double score;
                try {
                    score = Double.parseDouble(scoreStr);
                    if (score < 0 || score > 100) throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    msg.append("Line ").append(lineNo).append(" skipped: Invalid score '").append(scoreStr).append("'\n");
                    continue;
                }

                Grade g = new Grade();
                g.setEnrollmentId(en.getId());
                g.setComponent(compName);
                g.setScore(score);

                if (gradeDAO.insertOrUpdateScore(g)) {
                    inserted++;
                    notificationDAO.sendNotification(studentId,
                            "Your score for '" + compName + "' has been updated in Section " + sectionLabel + ".");
                } else {
                    msg.append("Line ").append(lineNo).append(" failed to save grade for '")
                            .append(username).append("'\n");
                }
            }
        } catch (Exception e) {
            return "CSV import failed: " + e.getMessage();
        }

        msg.insert(0, "CSV import completed: " + inserted + " scores inserted/updated, "
                + skippedSection + " skipped (missing section), "
                + skippedComp + " skipped (missing component), "
                + skippedStudent + " skipped (missing student/enrollment)\n");

        return msg.toString();
    }

}

