package erp.data;

import erp.domain.Grade;
import erp.ERPConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GradeDAO {

    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        int id = rs.getInt("grade_id");
        int enrollmentId = rs.getInt("enrollment_id");
        String component = rs.getString("component");
        double score = rs.getDouble("score");
        double finalGrade=rs.getDouble("final_grade");
        return new Grade(id, enrollmentId, component, score, finalGrade);
    }

    public List<Grade> listByEnrollment(int enrollmentId) {
        List<Grade> grades = new ArrayList<>();
        final String SQL = "SELECT * FROM grades WHERE enrollment_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, enrollmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Grade grade = new Grade();
                    grade.setId(rs.getInt("grade_id"));
                    grade.setEnrollmentId(rs.getInt("enrollment_id"));
                    grade.setComponent(rs.getString("component"));
                    grade.setScore(rs.getDouble("score"));
                    grade.setFinalGrade(rs.getObject("final_grade") == null ? null : rs.getDouble("final_grade"));
                    grades.add(grade);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (listByEnrollment): " + e.getMessage());
        }
        return grades;
    }

    public boolean insertOrUpdateScore(Grade grade) {
        final String CHECK_SQL = "SELECT grade_id FROM grades WHERE enrollment_id = ? AND component = ?";
        final String INSERT_SQL = "INSERT INTO grades (enrollment_id, component, score, final_grade) VALUES (?, ?, ?, ?)";
        final String UPDATE_SQL = "UPDATE grades SET score = ?, final_grade = ? WHERE enrollment_id = ? AND component = ?";

        try (Connection conn = ERPConnector.getConnection()) {

            // Step 1️⃣ — Check if entry already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(CHECK_SQL)) {
                checkStmt.setInt(1, grade.getEnrollmentId());
                checkStmt.setString(2, grade.getComponent());
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Step 2️⃣ — If exists, UPDATE it
                    try (PreparedStatement updateStmt = conn.prepareStatement(UPDATE_SQL)) {
                        updateStmt.setDouble(1, grade.getScore());
                        if (grade.getFinalGrade() != null) {
                            updateStmt.setDouble(2, grade.getFinalGrade());
                        } else {
                            updateStmt.setNull(2, java.sql.Types.DOUBLE);
                        }
                        updateStmt.setInt(3, grade.getEnrollmentId());
                        updateStmt.setString(4, grade.getComponent());

                        return updateStmt.executeUpdate() == 1;
                    }
                }
            }

            // Step 3️⃣ — Otherwise, INSERT new record
            try (PreparedStatement insertStmt = conn.prepareStatement(INSERT_SQL)) {
                insertStmt.setInt(1, grade.getEnrollmentId());
                insertStmt.setString(2, grade.getComponent());
                insertStmt.setDouble(3, grade.getScore());
                if (grade.getFinalGrade() != null) {
                    insertStmt.setDouble(4, grade.getFinalGrade());
                } else {
                    insertStmt.setNull(4, java.sql.Types.DOUBLE);
                }

                return insertStmt.executeUpdate() == 1;
            }

        } catch (SQLException e) {
            System.err.println("Database Error (insertOrUpdateScore): " + e.getMessage());
            return false;
        }
    }



    /**
     * Records the final computed grade for an enrollment.
     */
    public boolean updateFinalGrade(int enrollmentId, Double finalGrade) {
        final String SQL = "UPDATE grades SET final_grade = ? WHERE enrollment_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            // Set finalGrade. Use setDouble if not null, or setNull if passing a null grade
            if (finalGrade != null) {
                stmt.setDouble(1, finalGrade);
            } else {
                stmt.setNull(1, Types.DOUBLE);
            }

            stmt.setInt(2, enrollmentId);

            return stmt.executeUpdate() > 0; // Might update multiple rows if finalGrade is stored once per enrollment
        } catch (SQLException e) {
            System.err.println("Database Error (updateFinalGrade): Failed to update final grade for enrollment " + enrollmentId + ". " + e.getMessage());
            return false;
        }
    }
    public List<Grade> getGradesBySection(int sectionId) {
        List<Grade> grades = new ArrayList<>();
        // SQL needs to join 'grades' with 'enrollments' to filter by 'sectionId'
        final String SQL =
                "SELECT g.* FROM grades g " +
                        "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                        "WHERE e.section_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getGradesBySection): Cannot retrieve grades for section " + sectionId + ". " + e.getMessage());
        }
        return grades;
    }
    public void deleteGradesByEnrollment(int enrollmentId) {
        final String SQL = "DELETE FROM grades WHERE enrollment_id = ?";
        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {
            stmt.setInt(1, enrollmentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database Error (deleteGradesByEnrollment): " + e.getMessage());
        }
    }




}