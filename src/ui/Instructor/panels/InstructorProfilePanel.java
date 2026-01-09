package ui.Instructor.panels;

import javax.swing.*;
import java.awt.*;
import erp.service.AuthService;
import erp.data.InstructorDAO;
import erp.domain.Instructor;

public class InstructorProfilePanel extends JPanel {

    private int instructorId;
    private InstructorDAO instructorDAO = new InstructorDAO();
    private AuthService authService = new AuthService();

    public InstructorProfilePanel(int instructorId) {
        this.instructorId = instructorId;
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("My Profile", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBounds(290, 30, 300, 40);
        add(title);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameLabel.setBounds(180, 120, 150, 30);
        add(usernameLabel);

        JLabel deptLabel = new JLabel("Department:");
        deptLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        deptLabel.setBounds(180, 170, 150, 30);
        add(deptLabel);

        JLabel desigLabel = new JLabel("Designation:");
        desigLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        desigLabel.setBounds(180, 220, 150, 30);
        add(desigLabel);

        JLabel usernameValue = new JLabel("");
        usernameValue.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameValue.setBounds(340, 120, 300, 30);
        add(usernameValue);

        JLabel deptValue = new JLabel("");
        deptValue.setFont(new Font("Arial", Font.PLAIN, 16));
        deptValue.setBounds(340, 170, 300, 30);
        add(deptValue);

        JLabel desigValue = new JLabel("");
        desigValue.setFont(new Font("Arial", Font.PLAIN, 16));
        desigValue.setBounds(340, 220, 300, 30);
        add(desigValue);

        loadProfile(usernameValue, deptValue, desigValue);
    }

    private void loadProfile(JLabel usernameValue, JLabel deptValue, JLabel desigValue) {
        try {
            Instructor instructor = instructorDAO.getByUserId(instructorId);
            String username = authService.getUsernameByUserId(instructorId);
            usernameValue.setText(username);

            if (instructor != null) {
                deptValue.setText(instructor.getDepartment());
                desigValue.setText(instructor.getDesignation());
            } else {
                deptValue.setText("N/A");
                desigValue.setText("N/A");
            }

        }
        catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + ex.getMessage());
        }
    }
}