
package ui.Instructor.panels;

import erp.data.AssessmentDAO;
import erp.data.SectionLabelDAO;
import erp.domain.Section;
import erp.domain.SectionLabel;
import erp.service.InstructorService;
import authen.SetSession;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DefineComponentPanel extends JPanel {

    private JComboBox<SectionLabel> sectionDropdown;
    private JTextField nameField, weightField;
    private JButton addButton;

    private AssessmentDAO assessmentDAO;
    private SectionLabelDAO labelDAO;
    private InstructorService instructorService;

    public DefineComponentPanel() {

        this.assessmentDAO = new AssessmentDAO();
        this.labelDAO = new SectionLabelDAO();
        this.instructorService = new InstructorService();

        int instructorId = SetSession.getUserId();

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Define Assessment Component", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1;

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Select Section:"), gbc);

        gbc.gridx = 1;
        sectionDropdown = new JComboBox<>();
        loadSections(instructorId);
        add(sectionDropdown, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Component Name:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField();
        add(nameField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        add(new JLabel("Weight (e.g., 0.2):"), gbc);

        gbc.gridx = 1;
        weightField = new JTextField();
        add(weightField, gbc);

        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        addButton = new JButton("Add Component");
        add(addButton, gbc);

        addButton.addActionListener(e -> saveComponent());
    }

    private void loadSections(int instructorId) {
        List<Section> mySections = instructorService.getMySections(instructorId);

        for (Section s : mySections) {
            SectionLabel label = labelDAO.getBySectionId(s.getId());

            if (label != null) {
                sectionDropdown.addItem(label);
            } else {
                sectionDropdown.addItem(new SectionLabel(0, s.getId(), s.getCourseId(), "Section " + s.getId()));
            }
        }
    }

    private void saveComponent() {
        try {
            SectionLabel selected = (SectionLabel) sectionDropdown.getSelectedItem();
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a section.");
                return;
            }

            int sectionId = selected.getSectionId();
            String compName = nameField.getText().trim();
            double weight = Double.parseDouble(weightField.getText().trim());

            if (compName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Component name cannot be empty!");
                return;
            }

            assessmentDAO.insertComponent(sectionId, compName, weight);
            JOptionPane.showMessageDialog(this, "Component added successfully!");

            nameField.setText("");
            weightField.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
