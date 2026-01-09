
package erp.data;

import erp.domain.SectionLabel;
import erp.ERPConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SectionLabelDAO {

    private SectionLabel map(ResultSet rs) throws SQLException {
        return new SectionLabel(
                rs.getInt("label_id"),
                rs.getInt("section_id"),
                rs.getInt("course_id"),
                rs.getString("label")
        );
    }

    public boolean insert(int sectionId, int courseId, String label) {
        final String SQL = """
            INSERT INTO section_labels (section_id, course_id, label)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);
            stmt.setInt(2, courseId);
            stmt.setString(3, label);

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Error inserting section label: " + e.getMessage());
            return false;
        }
    }

    public SectionLabel getBySectionId(int sectionId) {
        final String SQL = "SELECT * FROM section_labels WHERE section_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching section label: " + e.getMessage());
        }
        return null;
    }

    public boolean existsForCourse(int courseId, String label) {
        final String SQL = """
            SELECT COUNT(*) AS cnt
            FROM section_labels
            WHERE course_id = ? AND label = ?
        """;

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, courseId);
            stmt.setString(2, label);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt("cnt") > 0;

        } catch (SQLException e) {
            System.err.println("Error checking duplicate section label: " + e.getMessage());
        }
        return false;
    }

    public List<SectionLabel> listByCourseId(int courseId) {
        final String SQL = "SELECT * FROM section_labels WHERE course_id = ? ORDER BY label";
        List<SectionLabel> list = new ArrayList<>();

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching section labels for course: " + e.getMessage());
        }

        return list;
    }

    public SectionLabel getByLabel(String label) {
        final String SQL = "SELECT * FROM section_labels WHERE label = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, label);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("Error fetching section label by label: " + e.getMessage());
        }

        return null;
    }

}
