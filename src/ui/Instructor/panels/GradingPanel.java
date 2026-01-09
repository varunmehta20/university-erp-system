
package ui.Instructor.panels;

import javax.swing.*;
import java.awt.*;

public class GradingPanel extends JPanel {

    private int instructorId;

    public GradingPanel(int instructorId) {
        this.instructorId = instructorId;

        setLayout(null);

        int startX = 50;
        int startY = 40;
        int width = 350;
        int height = 40;
        int gap = 20;

        JLabel title = new JLabel("Grading & Assessment");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setBounds(startX, startY, 400, 40);
        add(title);

        JButton defineComponentBtn = new JButton("Define Assessment Components");
        defineComponentBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        defineComponentBtn.setBounds(startX, startY + 60, width, height);
        add(defineComponentBtn);

        JButton enterScoreBtn = new JButton("Enter Student Scores");
        enterScoreBtn.setFont(new Font("Arial", Font.PLAIN, 16));
        enterScoreBtn.setBounds(startX, startY + 60 + height + gap, width, height);
        add(enterScoreBtn);

        defineComponentBtn.addActionListener(e -> {
            JFrame popup = new JFrame("Define Component");
            popup.setSize(500, 400);
            popup.setLocationRelativeTo(null);
            popup.add(new DefineComponentPanel());
            popup.setVisible(true);
        });

        enterScoreBtn.addActionListener(e -> {
            JFrame popup = new JFrame("Enter Scores");
            popup.setSize(700, 500);
            popup.setLocationRelativeTo(null);
            popup.add(new EnterScorePanel(instructorId));
            popup.setVisible(true);
        });
    }
}

