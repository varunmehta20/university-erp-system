package erp.data;

import erp.domain.Enrollment;
import erp.ERPConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        int id = rs.getInt("enrollment_id");
        int studentId = rs.getInt("student_id");
        int sectionId = rs.getInt("section_id");
        String status = rs.getString("status");

        return new Enrollment(id, studentId, sectionId, status);
    }

    public Enrollment getByStudentAndSection(int studentId, int sectionId) {
        final String SQL = "SELECT * FROM enrollments WHERE student_id = ? AND section_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEnrollment(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getByStudentAndSection): Cannot retrieve enrollment record. " + e.getMessage());
        }
        return null;
    }

    public List<Enrollment> getEnrollmentsByStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        final String SQL = "SELECT * FROM enrollments WHERE student_id = ? ORDER BY status, section_id";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getEnrollmentsByStudent): Cannot retrieve enrollments for student " + studentId + ". " + e.getMessage());
        }
        return enrollments;
    }

    public List<Enrollment> getEnrollmentsBySection(int sectionId) {
        List<Enrollment> enrollments = new ArrayList<>();
        final String SQL = "SELECT * FROM enrollments WHERE section_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getEnrollmentsBySection): Cannot retrieve enrollments for section " + sectionId + ". " + e.getMessage());
        }
        return enrollments;
    }

    public boolean create(Enrollment enrollment) {
        final String SQL = "INSERT INTO enrollments (student_id, section_id, status) VALUES (?, ?, ?)";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, enrollment.getStudentId());
            stmt.setInt(2, enrollment.getSectionId());
            stmt.setString(3, enrollment.getStatus());

            int affectedRows = stmt.executeUpdate();
            return affectedRows == 1;

        } catch (SQLException e) {
            System.err.println("Database Error (create): Failed to create enrollment for student " + enrollment.getStudentId() + ". " + e.getMessage());
            return false;
        }
    }



    public void markAsDropped(int studentId, int sectionId) {
        final String SQL = "UPDATE enrollments SET status = 'DROPPED' WHERE student_id = ? AND section_id = ?";
        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error (markAsDropped): " + e.getMessage());
        }
    }

    public boolean updateStatus(int studentId, int sectionId, String newStatus) {
        final String SQL = "UPDATE enrollments SET status = ? WHERE student_id = ? AND section_id = ?";
        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setString(1, newStatus);
            stmt.setInt(2, studentId);
            stmt.setInt(3, sectionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating enrollment status: " + e.getMessage());
            return false;
        }
    }

    public boolean isStudentEnrolledInCourse(int studentId, int courseId) {
        String sql = "SELECT COUNT(*) FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "WHERE e.student_id = ? AND s.course_id = ? AND e.status = 'ENROLLED'";
        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public String getStatus(int enrollmentId) {
        String sql = "SELECT status FROM enrollments WHERE enrollment_id = ?";
        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching enrollment status: " + e.getMessage());
        }
        return null;
    }

    public int getSectionId(int enrollmentId) {
        String sql = "SELECT section_id FROM enrollments WHERE enrollment_id = ?";
        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("section_id");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching section ID: " + e.getMessage());
        }
        return -1;
    }

    public Enrollment getEnrollmentById(int enrollmentId) {
        final String SQL = "SELECT * FROM enrollments WHERE enrollment_id = ?";
        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, enrollmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("enrollment_id");
                int studentId = rs.getInt("student_id");
                int sectionId = rs.getInt("section_id");
                String status = rs.getString("status");
                return new Enrollment(id, studentId, sectionId, status);
            }
        } catch (SQLException ex) {
            System.err.println("DB Error: " + ex.getMessage());
        }
        return null;
    }

    public int countActiveStudentsInSection(int sectionId) {
        final String SQL = """
        SELECT COUNT(*) 
        FROM enrollments 
        WHERE section_id = ? 
        AND status != 'DROPPED'
    """;

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.err.println("DB Error (countActiveStudentsInSection): " + e.getMessage());
        }
        return -1;
    }
}