package erp.data;

import erp.domain.Student;
import erp.ERPConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class StudentDAO {
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        int userId = rs.getInt("user_id");
        String rollNo = rs.getString("roll_no");
        String program = rs.getString("program");
        int year = rs.getInt("year");
        return new Student(userId, rollNo, program, year);
    }

    public List<Student> listAll() {
        List<Student> students = new ArrayList<>();
        final String SQL = "SELECT user_id, roll_no, program, year FROM students ORDER BY roll_no";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }

        } catch (SQLException e) {
            System.err.println("Database Error (listAll): " + e.getMessage());
        }
        return students;
    }

    public Student getByUserId(int userId) {
        final String SQL = "SELECT user_id, roll_no, program, year FROM students WHERE user_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Database Error (getByUserId): Cannot retrieve student user_id " + userId + ". " + e.getMessage());
        }
        return null;
    }

    public Student getByRollNo(String rollNo) {
        final String SQL = "SELECT user_id, roll_no, program, year FROM students WHERE roll_no = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, rollNo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Database Error (getByRollNo): Cannot retrieve roll " + rollNo + ". " + e.getMessage());
        }
        return null;
    }

    public void create(Student student) {
        final String SQL = "INSERT INTO students (user_id, roll_no, program, year) VALUES (?, ?, ?, ?)";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, student.getUserId());
            stmt.setString(2, student.getRollNo());
            stmt.setString(3, student.getProgram());
            stmt.setInt(4, student.getYear());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Database Error (create): Cannot create student " + student.getRollNo() + ". " + e.getMessage());
        }
    }

    public boolean update(Student student) {
        final String SQL = "UPDATE students SET roll_no = ?, program = ?, year = ? WHERE user_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setString(1, student.getRollNo());
            stmt.setString(2, student.getProgram());
            stmt.setInt(3, student.getYear());
            stmt.setInt(4, student.getUserId());

            return stmt.executeUpdate() == 1;

        } catch (SQLException e) {
            System.err.println("Database Error (update): Cannot update student user_id " + student.getUserId() + ". " + e.getMessage());
            return false;
        }
    }
}
