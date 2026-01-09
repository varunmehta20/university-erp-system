package erp.data;

import erp.domain.AssessmentComponent;
import erp.ERPConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssessmentDAO {
    private AssessmentComponent mapResultSetToComponent(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int sectionId = rs.getInt("section_id");
        String name = rs.getString("name");
        double weight = rs.getDouble("weight");

        return new AssessmentComponent(id, sectionId, name, weight);
    }

    public void insertComponent(int sectionId, String name, double weight) {
        final String SQL = "INSERT INTO assessment_components (section_id, name, weight) VALUES (?, ?, ?)";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);
            stmt.setString(2, name);
            stmt.setDouble(3, weight);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Database Error (insertComponent): Failed to insert assessment component '" + name + "'. " + e.getMessage());
        }
    }

    public List<AssessmentComponent> getComponents(int sectionId) {
        List<AssessmentComponent> components = new ArrayList<>();
        final String SQL = "SELECT id, section_id, name, weight FROM assessment_components WHERE section_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    AssessmentComponent component = new AssessmentComponent();
                    component.setId(rs.getInt("id"));
                    component.setSectionId(rs.getInt("section_id"));
                    component.setName(rs.getString("name"));
                    component.setWeight(rs.getDouble("weight"));
                    components.add(component);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getComponents): " + e.getMessage());
        }
        return components;
    }

    public double getWeightForComponent(int sectionId, String componentName) {
        final String SQL = "SELECT weight FROM assessment_components WHERE section_id = ? AND name = ?";
        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);
            stmt.setString(2, componentName);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("weight");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getWeightForComponent): " + e.getMessage());
        }
        return 0.0;
    }


}