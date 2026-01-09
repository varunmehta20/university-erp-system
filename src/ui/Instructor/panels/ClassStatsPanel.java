package ui.Instructor.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import erp.service.InstructorService;
import erp.data.SectionLabelDAO;
import erp.data.CourseDAO;
import erp.domain.SectionLabel;
import erp.domain.Course;

public class ClassStatsPanel extends JPanel {

    private JComboBox<String> sectionDropdown;
    private JTable statsTable;
    private DefaultTableModel model;
    private JButton refreshBtn;

    private InstructorService instructorService = new InstructorService();
    private SectionLabelDAO sectionLabelDAO = new SectionLabelDAO();
    private CourseDAO courseDAO = new CourseDAO();

    private int instructorId;
    private JLabel lblFinalAvg;
    private JLabel lblFinalMax;
    private JLabel lblFinalMin;
    private JLabel lblTotalStudents;

    public ClassStatsPanel(int instructorId) {
        this.instructorId = instructorId;
        setLayout(new BorderLayout(8, 8));
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Class Statistics", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        top.add(new JLabel("Select Section:"));
        sectionDropdown = new JComboBox<>();
        sectionDropdown.setPreferredSize(new Dimension(360, 28));
        top.add(sectionDropdown);

        refreshBtn = new JButton("Load Statistics");
        top.add(refreshBtn);

        add(top, BorderLayout.PAGE_START);

        model = new DefaultTableModel(new String[]{"Component", "Avg", "High", "Low", "Count"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        statsTable = new JTable(model);
        statsTable.setRowHeight(24);
        add(new JScrollPane(statsTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 8));
        lblFinalAvg = new JLabel("Final Avg: N/A");
        lblFinalMax = new JLabel("Final Max: N/A");
        lblFinalMin = new JLabel("Final Min: N/A");
        lblTotalStudents = new JLabel("Students (final): 0");

        bottom.add(lblFinalAvg);
        bottom.add(lblFinalMax);
        bottom.add(lblFinalMin);
        bottom.add(lblTotalStudents);

        add(bottom, BorderLayout.SOUTH);

        loadSectionsDropdown();

        refreshBtn.addActionListener(e -> loadStats());
        sectionDropdown.addActionListener(e -> {

            if (sectionDropdown.getItemCount() > 0 && sectionDropdown.getSelectedItem() != null) {
                loadStats();
            }
        });

        if (sectionDropdown.getItemCount() > 0) {
            sectionDropdown.setSelectedIndex(0);
            loadStats();
        }
    }

    private void loadSectionsDropdown() {
        sectionDropdown.removeAllItems();
        instructorService.getMySections(instructorId).forEach(section -> {
            int sectionId = section.getId();
            int courseId = section.getCourseId();

            SectionLabel sl = sectionLabelDAO.getBySectionId(sectionId);
            String label = (sl != null && sl.getLabel() != null && !sl.getLabel().isBlank())
                    ? sl.getLabel() : ("Section " + sectionId);

            Course course = courseDAO.getById(courseId);
            String code = (course != null) ? course.getCode() : ("Course-" + courseId);

            String display = sectionId + " | " + label + " | " + code;
            sectionDropdown.addItem(display);
        });
    }

    private void loadStats() {
        try {
            if (sectionDropdown.getSelectedItem() == null) return;
            String sel = sectionDropdown.getSelectedItem().toString();
            int sectionId = Integer.parseInt(sel.split("\\|")[0].trim());

            Map<String, Object> stats = instructorService.getClassStats(sectionId, instructorId);

            if (stats.containsKey("ERROR")) {
                JOptionPane.showMessageDialog(this, stats.get("ERROR"));
                return;
            }
            if (stats.containsKey("WARNING")) {
                JOptionPane.showMessageDialog(this, stats.get("WARNING"));
                model.setRowCount(0);
                clearSummary();
                return;
            }

            model.setRowCount(0);
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> compStats = (List<Map<String, Object>>) stats.get("component_stats");
            if (compStats != null) {
                for (Map<String, Object> cmap : compStats) {
                    String comp = (String) cmap.getOrDefault("component", "Unknown");
                    Object avg = cmap.getOrDefault("avg", "N/A");
                    Object max = cmap.getOrDefault("max", "N/A");
                    Object min = cmap.getOrDefault("min", "N/A");
                    Object count = cmap.getOrDefault("count", 0);

                    model.addRow(new Object[]{ comp, avg, max, min, count });
                }
            }


            Object finalAvg = stats.getOrDefault("final_avg", "N/A");
            Object finalMax = stats.getOrDefault("final_max", "N/A");
            Object finalMin = stats.getOrDefault("final_min", "N/A");
            Object total = stats.getOrDefault("total_students", 0);

            lblFinalAvg.setText("Final Avg: " + finalAvg);
            lblFinalMax.setText("Final Max: " + finalMax);
            lblFinalMin.setText("Final Min: " + finalMin);
            lblTotalStudents.setText("Students (final): " + total);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading stats: " + ex.getMessage());
        }
    }

    private void clearSummary() {
        lblFinalAvg.setText("Final Avg: N/A");
        lblFinalMax.setText("Final Max: N/A");
        lblFinalMin.setText("Final Min: N/A");
        lblTotalStudents.setText("Students (final): 0");
    }
}
