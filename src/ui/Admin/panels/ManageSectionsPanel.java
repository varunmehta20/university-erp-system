package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;

public class ManageSectionsPanel extends JPanel {

    public ManageSectionsPanel(int adminId, JPanel rightPanel, CardLayout cardLayout) {
        setLayout(null);
        setBackground(Color.WHITE);

        JLabel title = new JLabel("Manage Sections", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBounds(0, 20, 880, 40);
        add(title);

        JButton addBtn = new JButton("Add Section");
        addBtn.setBounds(300, 150, 250, 40);
        add(addBtn);

        JButton editBtn = new JButton("Edit Section");
        editBtn.setBounds(300, 220, 250, 40);
        add(editBtn);

        addBtn.addActionListener(e -> cardLayout.show(rightPanel, "ADD_SECTION"));
        editBtn.addActionListener(e -> cardLayout.show(rightPanel, "EDIT_SECTION"));
    }
}
