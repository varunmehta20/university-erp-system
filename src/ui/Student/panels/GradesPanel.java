package ui.Student.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.HashSet;

import erp.service.StudentService;
import erp.dto.CourseGradeView;
import erp.dto.AssessmentScore;

public class GradesPanel extends JPanel {

    private StudentService studentService;
    private JTable gradeTable;
    private DefaultTableModel model;
    private JComboBox<String> courseDropdown;
    private List<CourseGradeView> allGrades;

    private JLabel finalGradeLabel;

    public GradesPanel(int studentId) {
        setLayout(null);
        studentService = new StudentService();

        JLabel title = new JLabel(" My Grades");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setBounds(250, 20, 300, 30);
        add(title);

        courseDropdown = new JComboBox<>();
        courseDropdown.setBounds(40, 80, 200, 35);
        add(courseDropdown);

        JButton loadBtn = new JButton("Load");
        loadBtn.setBounds(260, 80, 120, 35);
        add(loadBtn);

        finalGradeLabel = new JLabel("Final Grade: -");
        finalGradeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        finalGradeLabel.setForeground(new Color(0, 80, 160));
        finalGradeLabel.setBounds(410, 80, 350, 35);
        add(finalGradeLabel);

        model = new DefaultTableModel(
                new String[]{"Component", "Score", "Weight"}, 0
        );

        gradeTable = new JTable(model);
        gradeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        JScrollPane scrollPane = new JScrollPane(gradeTable);
        scrollPane.setBounds(40, 140, 400, 350);
        gradeTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        gradeTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        gradeTable.getColumnModel().getColumn(1).setPreferredWidth(80);
        gradeTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        add(scrollPane);

        JButton refreshBtn = new JButton("Refresh All");
        refreshBtn.setBounds(250, 510, 140, 35);
        add(refreshBtn);

        JButton exportBtn = new JButton("Download CSV");
        exportBtn.setBounds(410, 510, 150, 35);
        add(exportBtn);

        loadAllGrades();
        fillDropdown();

        loadBtn.addActionListener(e -> loadSelectedCourseGrades());
        refreshBtn.addActionListener(e -> {
            loadAllGrades();
            fillDropdown();
            finalGradeLabel.setText("Final Grade: -");
        });

        exportBtn.addActionListener(e -> exportCSV());
    }

    private void loadAllGrades() {
        model.setRowCount(0);
        allGrades = studentService.getGradesView();
    }

    private void fillDropdown() {
        courseDropdown.removeAllItems();
        HashSet<String> uniqueCourses = new HashSet<>();

        for (CourseGradeView g : allGrades)
            uniqueCourses.add(g.getCourseCode());

        for (String code : uniqueCourses)
            courseDropdown.addItem(code);
    }

    private void loadSelectedCourseGrades() {
        model.setRowCount(0);
        String selectedCourse = (String) courseDropdown.getSelectedItem();
        if (selectedCourse == null) return;

        for (CourseGradeView view : allGrades) {
            if (view.getCourseCode().equals(selectedCourse)) {

                Double fg = Double.valueOf(view.getFinalGrade());
                String letter = convertToLetterGrade(fg);

                finalGradeLabel.setText("Final Grade: " + fg + " (" + letter + ")");

                List<AssessmentScore> scores = view.getComponentScores();

                if (scores != null && !scores.isEmpty()) {
                    for (AssessmentScore s : scores) {
                        model.addRow(new Object[]{
                                s.getComponentName(),
                                s.getScore(),
                                s.getWeight()
                        });
                    }
                } else {
                    model.addRow(new Object[]{"N/A", "-", "-"});
                }
            }
        }
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

    private void exportCSV() {
        try {
            String filePath = "Transcript.csv";

            String message = studentService.exportTranscriptCSV(filePath);

            JOptionPane.showMessageDialog(this,
                    message + "\nSaved as: " + new File(filePath).getAbsolutePath());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error exporting CSV: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}