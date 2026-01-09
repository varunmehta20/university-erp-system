package ui.Admin.panels;

import erp.data.SectionDAO;
import erp.data.SectionLabelDAO;
import erp.domain.Section;
import erp.domain.SectionLabel;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class RemoveSectionPanel extends JPanel {

    private final SectionDAO sectionDAO = new SectionDAO();
    private final SectionLabelDAO labelDAO = new SectionLabelDAO();

    private JComboBox<String> sectionDropDown;
    private Map<String, Integer> labelToSectionId = new HashMap<>();

    public RemoveSectionPanel() {

        setLayout(null);

        JLabel title = new JLabel("Remove Section");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBounds(30, 10, 300, 30);
        add(title);

        JLabel selectLabel = new JLabel("Select Section:");
        selectLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        selectLabel.setBounds(250, 50, 150, 25);
        add(selectLabel);

        sectionDropDown = new JComboBox<>();
        sectionDropDown.setBounds(200, 80, 450, 28);
        populateSectionLabels();
        add(sectionDropDown);

        JButton deleteBtn = new JButton("Delete Section");
        deleteBtn.setBounds(200, 130, 450, 35);
        deleteBtn.addActionListener(e -> deleteSection());
        add(deleteBtn);
    }

    private void populateSectionLabels() {
        sectionDropDown.removeAllItems();
        labelToSectionId.clear();

        for (Section s : sectionDAO.listAllSections()) {
            SectionLabel label = labelDAO.getBySectionId(s.getId());
            if (label != null) {
                sectionDropDown.addItem(label.getLabel());
                labelToSectionId.put(label.getLabel(), s.getId());
            }
        }
    }

    private void deleteSection() {
        String selectedLabel = (String) sectionDropDown.getSelectedItem();

        if (selectedLabel == null) {
            JOptionPane.showMessageDialog(this, "No section selected!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int sectionId = labelToSectionId.get(selectedLabel);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete section \"" + selectedLabel + "\" ?",
                "Confirm Remove",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = sectionDAO.deleteSectionCompletely(sectionId);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Section removed successfully.");
            populateSectionLabels();
        } else {
            JOptionPane.showMessageDialog(this, "Failed! Section might have active enrollments.");
        }
    }
}
