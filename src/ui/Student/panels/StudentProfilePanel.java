package ui.Student.panels;

import javax.swing.*;
import java.awt.*;
import erp.data.StudentDAO;
import erp.domain.Student;
import erp.service.AuthService;

public class StudentProfilePanel extends JPanel {

    private final int studentId;
    private final StudentDAO studentDAO = new StudentDAO();

    private JLabel usernameValue, programValue, yearValue, rollValue;

    public StudentProfilePanel(int studentId) {
        this.studentId = studentId;

        setLayout(null);
        setBackground(new Color(245,245,245)); // subtle soft background

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setBounds(330, 20, 400, 40);
        add(title);

        JPanel card = new JPanel(null);
        card.setBackground(Color.WHITE);
        card.setBounds(200, 90, 480, 260);
        card.setBorder(BorderFactory.createLineBorder(new Color(180,180,180), 1)); // soft border
        add(card);

        int y = 30;

        card.add(makeLabel("Username:", 40, y));
        usernameValue = makeValueLabel(200, y); card.add(usernameValue); y+=50;

        card.add(makeLabel("Roll No:", 40, y));
        rollValue = makeValueLabel(200, y); card.add(rollValue); y+=50;

        card.add(makeLabel("Program:", 40, y));
        programValue = makeValueLabel(200, y); card.add(programValue); y+=50;

        card.add(makeLabel("Year:", 40, y));
        yearValue = makeValueLabel(200, y); card.add(yearValue);

        loadProfile();
    }

    private JLabel makeLabel(String text,int x,int y){
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 15));
        l.setBounds(x,y,150,30);
        return l;
    }

    private JLabel makeValueLabel(int x,int y){
        JLabel l=new JLabel();
        l.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        l.setBounds(x,y,230,30);
        return l;
    }

    private void loadProfile() {
        try {
            AuthService authService = new AuthService();
            String username = authService.getUsernameByUserId(studentId);
            if (username != null) usernameValue.setText(username);

            Student s = studentDAO.getByUserId(studentId);
            rollValue.setText(s.getRollNo());
            programValue.setText(s.getProgram());
            yearValue.setText(String.valueOf(s.getYear()));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + ex.getMessage());
        }
    }
}
