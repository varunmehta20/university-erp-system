package erp.data;

import erp.domain.Instructor;
import erp.ERPConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstructorDAO {

    private Instructor map(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String department = rs.getString("department");
        String designation = rs.getString("title");
        return new Instructor(userId, department, designation);
    }

    public Instructor getByUserId(int userId) {
        final String SQL = "SELECT user_id, department, title FROM instructors WHERE user_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return map(rs);
            }

        } catch (SQLException e) {
            System.err.println("Database Error (getByUserId): " + e.getMessage());
        }
        return null;
    }

    public List<Instructor> listAll() {
        List<Instructor> list = new ArrayList<>();
        final String SQL = "SELECT user_id, department, title FROM instructors ORDER BY department";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(map(rs));

        } catch (SQLException e) {
            System.err.println("Database Error (listAll): " + e.getMessage());
        }

        return list;
    }

    public void create(Instructor instructor) {
        final String SQL = """
            INSERT INTO instructors (user_id, department, title)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, instructor.getUserId());
            stmt.setString(2, instructor.getDepartment());
            stmt.setString(3, instructor.getDesignation());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Database Error (create): " + e.getMessage());
        }
    }

    public boolean update(Instructor instructor) {
        final String SQL = """
            UPDATE instructors
            SET department = ?, title = ?
            WHERE user_id = ?
        """;

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, instructor.getDepartment());
            stmt.setString(2, instructor.getDesignation());
            stmt.setInt(3, instructor.getUserId());
            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Database Error (update): " + e.getMessage());
            return false;
        }
    }
    public List<Integer> getAllInstructorIds() {
        List<Integer> list = new ArrayList<>();
        final String SQL = "SELECT user_id FROM instructors ORDER BY user_id";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) list.add(rs.getInt("user_id"));

        } catch (SQLException e) {
            System.err.println("Database Error (getAllInstructorIds): " + e.getMessage());
        }

        return list;
    }
}
