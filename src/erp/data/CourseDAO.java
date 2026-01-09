package erp.data;

import erp.domain.Course;
import erp.ERPConnector;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {

    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        int id = rs.getInt("course_id");
        String code = rs.getString("code");
        String title = rs.getString("title");
        int credits = rs.getInt("credits");

        return new Course(id, code, title, credits);
    }

    public List<Course> listAll() {
        List<Course> courses = new ArrayList<>();
        final String SQL = "SELECT course_id, code, title, credits FROM courses ORDER BY code";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database Error (listAll): Cannot retrieve courses. " + e.getMessage());
        }
        return courses;
    }

    public Course getById(int courseId) {
        final String SQL = "SELECT course_id, code, title, credits FROM courses WHERE course_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getById): Cannot retrieve course ID " + courseId + ". " + e.getMessage());
        }
        return null;
    }

    public boolean create(Course course) {
        final String CHECK_SQL = "SELECT course_id FROM courses WHERE code = ?";
        final String INSERT_SQL = "INSERT INTO courses (code, title, credits) VALUES (?, ?, ?)";

        try (Connection conn = ERPConnector.getConnection()) {
            try (PreparedStatement checkStmt = conn.prepareStatement(CHECK_SQL)) {
                checkStmt.setString(1, course.getCode());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return false;
                }
            }
            try (PreparedStatement insertStmt =
                         conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

                insertStmt.setString(1, course.getCode());
                insertStmt.setString(2, course.getTitle());
                insertStmt.setInt(3, course.getCredits());

                int affectedRows = insertStmt.executeUpdate();
                if (affectedRows == 0) return false;

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        course.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("DB Error: " + e.getMessage());
        }

        return false;
    }

    public boolean update(Course course) {
        final String SQL = "UPDATE courses SET code = ?, title = ?, credits = ? WHERE course_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getTitle());
            stmt.setInt(3, course.getCredits());
            stmt.setInt(4, course.getId());

            int affectedRows = stmt.executeUpdate();

            return affectedRows == 1;

        } catch (SQLException e) {
            System.err.println("Database Error (update): Cannot update course ID "
                    + course.getId() + ". " + e.getMessage());
            return false;
        }
    }

    public Course getByCode(String courseCode) {
        final String SQL = "SELECT course_id, code, title, credits FROM courses WHERE code = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, courseCode);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getByCode): Cannot retrieve course profile for code " + courseCode + ". " + e.getMessage());
        }
        return null;
    }

    public Course findCourseByCode(String code) {
        final String SQL = "SELECT * FROM courses WHERE code = ?";
        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Course(
                            rs.getInt("course_id"),
                            rs.getString("code"),
                            rs.getString("title"),
                            rs.getInt("credits")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Database Error (findCourseByCode): " + e.getMessage());
        }
        return null;
    }
    public String getCourseCodeById(int courseId) {
        Course c = getById(courseId);
        return (c != null) ? c.getCode() : null;
    }
}