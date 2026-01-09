package ui.Admin.panels;

import javax.swing.*;
import java.awt.*;

public class AdminProfilePanel extends JPanel {

    public AdminProfilePanel(String username) {

        setLayout(null);

        JLabel heading = new JLabel("My Profile");
        heading.setFont(new Font("Arial", Font.BOLD, 30));
        heading.setBounds(300, 40, 400, 50);
        add(heading);

        JLabel msg = new JLabel("Hello, " + username);
        msg.setFont(new Font("Arial", Font.PLAIN, 24));
        msg.setBounds(320, 150, 400, 40);
        add(msg);
    }
}
