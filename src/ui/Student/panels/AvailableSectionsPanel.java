package ui.Student.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import erp.service.StudentService;
import erp.data.SectionLabelDAO;
import erp.domain.SectionLabel;
import erp.domain.Course;
import erp.domain.Section;

public class AvailableSectionsPanel extends JPanel {

    private StudentService studentService;
    private JTable sectionTable;
    private DefaultTableModel model;
    private JComboBox<Integer> semesterDropdown;

    public AvailableSectionsPanel(int studentId) {
        setLayout(null);
        studentService = new StudentService();

        JLabel title = new JLabel("📘 Available Sections");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(230, 20, 400, 30);
        add(title);

        JLabel semLbl = new JLabel("Semester:");
        semLbl.setBounds(40, 70, 80, 30);
        add(semLbl);

        semesterDropdown = new JComboBox<>();
        semesterDropdown.setBounds(120, 70, 120, 30);
        add(semesterDropdown);

        model = new DefaultTableModel(
                new String[]{"Section", "Course Code", "Title", "Capacity", "Semester", "Year", "Status", "HID"}, 0
        );

        sectionTable = new JTable(model);
        sectionTable.getColumnModel().getColumn(7).setMinWidth(0);
        sectionTable.getColumnModel().getColumn(7).setMaxWidth(0);
        sectionTable.getColumnModel().getColumn(7).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(sectionTable);
        scrollPane.setBounds(40, 120, 600, 350);
        add(scrollPane);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(120, 500, 150, 35);
        add(refreshBtn);

        JButton registerBtn = new JButton("Register");
        registerBtn.setBounds(330, 500, 150, 35);
        add(registerBtn);

        loadSemesterDropdown();

        loadSectionsBySelectedSemester();

        refreshBtn.addActionListener(e -> loadSectionsBySelectedSemester());

        semesterDropdown.addActionListener(e -> loadSectionsBySelectedSemester());

        registerBtn.addActionListener(e -> {
            int row = sectionTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a section first!");
                return;
            }

            int sectionId = (int) model.getValueAt(row, 7); // hidden ID
            String result = studentService.registerForSection(sectionId);

            JOptionPane.showMessageDialog(this, result);
            loadSectionsBySelectedSemester();
        });
    }

    private void loadSemesterDropdown() {
        semesterDropdown.removeAllItems();

        List<Integer> semesters = studentService.getAvailableSemesters();
        for (int sem : semesters) {
            semesterDropdown.addItem(sem);
        }
    }

    private void loadSectionsBySelectedSemester() {
        model.setRowCount(0);

        Integer selectedSemester = (Integer) semesterDropdown.getSelectedItem();
        if (selectedSemester == null) return;

        SectionLabelDAO labelDAO = new SectionLabelDAO();

        List<Section> sections = studentService.getSectionsBySemester(selectedSemester);

        for (Section s : sections) {
            Course course = studentService.getCourseById(s.getCourseId());
            if (course == null) continue;

            SectionLabel lbl = labelDAO.getBySectionId(s.getId());
            String sectionName = (lbl != null ? lbl.getLabel() : "Section " + s.getId());

            model.addRow(new Object[]{
                    sectionName,
                    course.getCode(),
                    course.getTitle(),
                    s.getCapacity(),
                    s.getSemester(),
                    s.getYear(),
                    s.getStatus(),
                    s.getId()
            });
        }
    }
}
