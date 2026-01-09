//package ui.Student.panels;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.util.List;
//
//import erp.service.StudentService;
//import erp.dto.TimetableEntry;
//import erp.domain.Section;
//
//public class TimetablePanel extends JPanel {
//
//    private final StudentService studentService;
//    private final JTable table;
//    private final DefaultTableModel model;
//
//    public TimetablePanel(int studentId) {
//
//        setLayout(null);
//        studentService = new StudentService();
//
//        JLabel title = new JLabel("My Timetable");
//        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        title.setBounds(250, 10, 300, 30);  // pulled inward
//        add(title);
//
//        model = new DefaultTableModel(
//                new String[]{"Section", "Course Code", "Course Name", "Day & Time", "Room", "Semester", "Year"},
//                0
//        );
//
//        table = new JTable(model);
//        table.setRowHeight(26);
//
//        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
//
//        JScrollPane scrollPane = new JScrollPane(table);
//
//        scrollPane.setBounds(40, 60, 620, 360);
//
//        add(scrollPane);
//
//        JButton refreshBtn = new JButton("Refresh");
//        refreshBtn.setBounds(290, 440, 120, 35);
//        add(refreshBtn);
//
//        refreshBtn.addActionListener(e -> load(studentId));
//
//        load(studentId);
//    }
//
//    private void load(int studentId) {
//        model.setRowCount(0);
//
//        List<TimetableEntry> timetable = studentService.getTimetable();
//
//        for (TimetableEntry entry : timetable) {
//            List<Section> sections = studentService.listAvailableSections(entry.getCourseCode());
//
//            for (Section s : sections) {
//                if (s.getId() == entry.getSectionId()) {
//
//                    String secName = studentService.getSectionName(s.getId());
//
//                    model.addRow(new Object[]{
//                            secName,
//                            entry.getCourseCode(),
//                            entry.getCourseName(),
//                            s.getDayTime(),
//                            s.getRoom(),
//                            s.getSemester(),
//                            s.getYear()
//                    });
//                }
//            }
//        }
//
//        if (model.getRowCount() == 0) {
//            JOptionPane.showMessageDialog(this, "No timetable entries found.");
//        }
//    }
//}


package ui.Student.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

import erp.service.StudentService;
import erp.dto.TimetableEntry;
import erp.domain.Section;

public class TimetablePanel extends JPanel {

    private final StudentService studentService;
    private final JTable table;
    private final DefaultTableModel model;

    // *** Added flag so popup appears only when user triggers load ***
    public boolean loadTriggeredByUser = false;

    public TimetablePanel(int studentId) {

        setLayout(null);
        studentService = new StudentService();

        JLabel title = new JLabel("My Timetable");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setBounds(250, 10, 300, 30);
        add(title);

        model = new DefaultTableModel(
                new String[]{"Section", "Course Code", "Course Name", "Day & Time", "Room", "Semester", "Year"},
                0
        );

        table = new JTable(model);
        table.setRowHeight(26);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(40, 60, 620, 360);
        add(scrollPane);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setBounds(290, 440, 120, 35);
        add(refreshBtn);

        // *** Refresh should ALSO show popup if empty ***
        refreshBtn.addActionListener(e -> {
            loadTriggeredByUser = true;
            load(studentId);
        });

        // *** Removed auto-load here so popup doesn't appear at login ***
        // load(studentId);
    }

    public void load(int studentId) {
        model.setRowCount(0);

        List<TimetableEntry> timetable = studentService.getTimetable();

        for (TimetableEntry entry : timetable) {
            List<Section> sections = studentService.listAvailableSections(entry.getCourseCode());

            for (Section s : sections) {
                if (s.getId() == entry.getSectionId()) {

                    String secName = studentService.getSectionName(s.getId());

                    model.addRow(new Object[]{
                            secName,
                            entry.getCourseCode(),
                            entry.getCourseName(),
                            s.getDayTime(),
                            s.getRoom(),
                            s.getSemester(),
                            s.getYear()
                    });
                }
            }
        }

        // *** Popup ONLY IF triggered by user ***
        if (model.getRowCount() == 0 && loadTriggeredByUser) {
            JOptionPane.showMessageDialog(this, "No timetable entries found.");
        }

        // reset flag after load
        loadTriggeredByUser = false;
    }
}
