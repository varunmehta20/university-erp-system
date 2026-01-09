package ui.Instructor.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import erp.data.CourseDAO;
import erp.data.SectionLabelDAO;
import erp.domain.Section;
import erp.domain.SectionLabel;
import erp.service.InstructorService;

public class MySectionsPanel extends JPanel {

    private InstructorService instructorService;
    private JTable sectionsTable;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> semesterBox;
    private SectionLabelDAO sectionLabelDAO = new SectionLabelDAO();
    private CourseDAO courseDAO = new CourseDAO();



    public MySectionsPanel(int instructorId) {

        instructorService = new InstructorService();
        setLayout(null);

        JLabel titleLabel = new JLabel(" My Sections");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setBounds(220, 15, 300, 30);
        add(titleLabel);

        JLabel semLabel = new JLabel("Semester:");
        semLabel.setBounds(80, 60, 80, 25);
        add(semLabel);

        semesterBox = new JComboBox<>();
        semesterBox.setBounds(160, 60, 120, 25);
        add(semesterBox);

        loadSemesterOptions();

        JButton loadBtn = new JButton("Load");
        loadBtn.setBounds(310, 60, 100, 25);
        add(loadBtn);

        tableModel = new DefaultTableModel(
                new String[]{"Section Name", "Course Code", "Day/Time", "Room", "Semester", "Year", "Status"}
                ,
                0
        );

        sectionsTable = new JTable(tableModel);
        sectionsTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        scrollPane.setBounds(40, 110, 600, 350);
        add(scrollPane);

        loadBtn.addActionListener(e -> {
            Integer sem = (Integer) semesterBox.getSelectedItem();
            if (sem != null) {
                loadSectionsBySemester(instructorId, sem);
            }
        });
    }

    private void loadSemesterOptions() {
        List<Integer> sems = instructorService.getAvailableSemesters();
        for (Integer s : sems) {
            semesterBox.addItem(s);
        }
    }

    private void loadSectionsBySemester(int instructorId, int semester) {
        List<Section> sections = instructorService.getMySectionsBySemester(instructorId, semester);

        tableModel.setRowCount(0);

        for (Section s : sections) {
            SectionLabel lbl = sectionLabelDAO.getBySectionId(s.getId());
            String sectionName = (lbl != null) ? lbl.getLabel() : "Section " + s.getId();

            String courseCode = courseDAO.getCourseCodeById(s.getCourseId());
            if (courseCode == null) courseCode = "Course " + s.getCourseId();

            tableModel.addRow(new Object[]{
                    sectionName,
                    courseCode,
                    s.getDayTime(),
                    s.getRoom(),
                    s.getSemester(),
                    s.getYear(),
                    s.getStatus()
            });

        }
    }
}
