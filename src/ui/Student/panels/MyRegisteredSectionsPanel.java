package ui.Student.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import erp.service.StudentService;
import erp.domain.Enrollment;
import erp.domain.Section;
import erp.domain.Course;
import erp.data.CourseDAO;
import erp.data.SectionDAO;
import erp.data.SectionLabelDAO;
import erp.domain.SectionLabel;

public class MyRegisteredSectionsPanel extends JPanel {

    private StudentService studentService;
    private JTable table;
    private DefaultTableModel model;
    private SectionDAO sectionDAO = new SectionDAO();
    private CourseDAO courseDAO = new CourseDAO();
    private SectionLabelDAO labelDAO = new SectionLabelDAO();

    public MyRegisteredSectionsPanel(int studentId) {
        setLayout(null);
        studentService = new StudentService();

        JLabel title = new JLabel(" My Registered Sections");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(210, 20, 350, 30);
        add(title);

        model = new DefaultTableModel(
                new String[]{"Section", "Course Code", "Title", "Semester", "Year", "HID"}, 0
        );
        table = new JTable(model);

        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(40, 80, 600, 380);
        add(scrollPane);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(130, 480, 140, 35);
        add(refreshBtn);

        JButton dropBtn = new JButton("Drop");
        dropBtn.setBounds(360, 480, 140, 35);
        add(dropBtn);

        loadRegistered(studentId);

        refreshBtn.addActionListener(e -> loadRegistered(studentId));

        dropBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a section first!");
                return;
            }

            int sectionId = (int) model.getValueAt(row, 5); // hidden ID
            String result = studentService.dropSection(sectionId);

            JOptionPane.showMessageDialog(this, result);
            loadRegistered(studentId);
        });
    }

    private void loadRegistered(int studentId) {
        model.setRowCount(0);
        List<Enrollment> list = studentService.getMyEnrollments(studentId);

        for (Enrollment e : list) {
            if (!"ENROLLED".equalsIgnoreCase(e.getStatus())) continue;

            Section s = sectionDAO.getById(e.getSectionId());
            Course c = courseDAO.getById(s.getCourseId());
            SectionLabel lbl = labelDAO.getBySectionId(s.getId());

            String sectionName = (lbl != null ? lbl.getLabel() : "Section " + s.getId());

            model.addRow(new Object[]{
                    sectionName,
                    c.getCode(),
                    c.getTitle(),
                    s.getSemester(),
                    s.getYear(),
                    s.getId()
            });
        }
    }
}
