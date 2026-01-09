package erp.data;

import erp.domain.Section;
import erp.ERPConnector;
import erp.domain.SectionLabel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import java.util.Set;
import java.util.TreeSet;

public class SectionDAO {

    private Section mapResultSetToSection(ResultSet rs) throws SQLException {
        int id = rs.getInt("section_id");
        int courseId = rs.getInt("course_id");
        int instructorId = rs.getInt("instructor_id");
        String dayTime = rs.getString("day_time");
        String room = rs.getString("room");
        int capacity = rs.getInt("capacity");
        String semester = rs.getString("semester");
        int year = rs.getInt("year");
        String status= rs.getString("status");
        return new Section(id, courseId, instructorId, dayTime, room, capacity, semester, year,status);
    }

    public Section getById(int sectionId) {
        final String SQL = "SELECT * FROM sections WHERE section_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSection(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getById): Cannot retrieve section ID " + sectionId + ". " + e.getMessage());
        }
        return null;
    }

    public List<Section> getSectionsByInstructor(int instructorId) {
        List<Section> sections = new ArrayList<>();
        final String SQL = "SELECT * FROM sections WHERE instructor_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, instructorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(mapResultSetToSection(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (getSectionsByInstructor): Cannot retrieve sections for instructor " + instructorId + ". " + e.getMessage());
        }
        return sections;
    }

    public int countEnrolled(int sectionId) {
        final String SQL = "SELECT COUNT(enrollment_id) FROM enrollments WHERE section_id = ? AND status = 'ENROLLED'";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (countEnrolled): Cannot count enrollments for section " + sectionId + ". " + e.getMessage());
        }
        return -1;
    }

    public void create(Section section) {
        final String SQL = "INSERT INTO sections (course_id, instructor_id, day_time, room, capacity, semester, year,status) VALUES (?, ?, ?, ?, ?, ?, ?,?)";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, section.getCourseId());
            stmt.setInt(2, section.getInstructorId());
            stmt.setString(3, section.getDayTime());
            stmt.setString(4, section.getRoom());
            stmt.setInt(5, section.getCapacity());
            stmt.setString(6, section.getSemester());
            stmt.setInt(7, section.getYear());
            stmt.setString(8,section.getStatus());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    section.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (create): Cannot create new section. " + e.getMessage());
        }
    }

    public boolean update(Section section) {
        final String SQL = "UPDATE sections SET instructor_id = ?, day_time = ?, room = ?, capacity = ?, semester = ?, year = ?, status = ? WHERE section_id = ?";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            if (section.getInstructorId() <= 0) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, section.getInstructorId());
            }

            stmt.setString(2, section.getDayTime());
            stmt.setString(3, section.getRoom());
            stmt.setInt(4, section.getCapacity());
            stmt.setString(5, section.getSemester());
            stmt.setInt(6, section.getYear());
            stmt.setString(7, section.getStatus());
            stmt.setInt(8, section.getId());

            int rows = stmt.executeUpdate();
            return rows == 1;

        } catch (SQLException e) {
            System.err.println("Database Error (update section): " + e.getMessage());
            return false;
        }
    }

    public List<Section> listByCourse(int courseId) {
        List<Section> sections = new ArrayList<>();
        final String SQL = "SELECT * FROM sections WHERE course_id = ? ORDER BY semester, year, day_time";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(mapResultSetToSection(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error (listByCourse): Cannot retrieve sections for course " + courseId + ". " + e.getMessage());
        }
        return sections;
    }

    public boolean updateFull(Section s) {
        final String SQL = """
        UPDATE sections 
        SET instructor_id = ?, 
            day_time = ?, 
            room = ?, 
            capacity = ?, 
            semester = ?, 
            year = ?, 
            status = ?
        WHERE section_id = ?
    """;

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL)) {

            stmt.setInt(1, s.getInstructorId());
            stmt.setString(2, s.getDayTime());
            stmt.setString(3, s.getRoom());
            stmt.setInt(4, s.getCapacity());
            stmt.setString(5, s.getSemester());
            stmt.setInt(6, s.getYear());
            stmt.setString(7, s.getStatus());
            stmt.setInt(8, s.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error updating section: " + e.getMessage());
        }
        return false;
    }

    public List<Section> listAllSections() {
        List<Section> list = new ArrayList<>();

        final String SQL = "SELECT * FROM sections ORDER BY course_id, section_id";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToSection(rs));
            }

        } catch (SQLException e) {
            System.err.println("Database Error (listAllSections): " + e.getMessage());
        }
        return list;
    }

public List<Integer> getAvailableSemesters() {
    List<Integer> semesters = new ArrayList<>();

    final String SQL = "SELECT DISTINCT semester FROM sections WHERE semester IS NOT NULL ORDER BY semester";

    try (Connection conn = ERPConnector.getConnection();
         PreparedStatement stmt = conn.prepareStatement(SQL);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            semesters.add(Integer.parseInt(rs.getString("semester")));
        }

    } catch (SQLException e) {
        System.err.println("Database Error (getAvailableSemesters): " + e.getMessage());
    }

    return semesters;
}

public List<Section> getSectionsBySemester(int semester) {
    List<Section> sections = new ArrayList<>();

    final String SQL = "SELECT * FROM sections WHERE TRIM(semester) = ? ORDER BY course_id, section_id";

    try (Connection conn = ERPConnector.getConnection();
         PreparedStatement stmt = conn.prepareStatement(SQL)) {

        stmt.setString(1, String.valueOf(semester).trim());

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            sections.add(mapResultSetToSection(rs));
        }

    } catch (SQLException e) {
        System.err.println("Database Error (getSectionsBySemester): " + e.getMessage());
    }

    return sections;
}

    public List<Integer> getAllSemesters() {
        List<Integer> list = new ArrayList<>();
        String sql = "SELECT DISTINCT semester FROM sections ORDER BY semester";

        try (Connection conn = ERPConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) list.add(rs.getInt(1));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean deleteSectionCompletely(int sectionId) {
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
        SectionLabelDAO labelDAO = new SectionLabelDAO();

        int activeCount = enrollmentDAO.countActiveStudentsInSection(sectionId);
        if (activeCount == -1) {
            System.err.println("Error checking enrollments.");
            return false;
        }
        if (activeCount > 0) {
            System.out.println("Cannot delete section. Active students exist.");
            return false;
        }

        List<SectionLabel> labels = labelDAO.listByCourseId(getById(sectionId).getCourseId());
        try (Connection conn = ERPConnector.getConnection()) {
            conn.setAutoCommit(false);

            final String deleteLabelSQL = "DELETE FROM section_labels WHERE section_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteLabelSQL)) {
                stmt.setInt(1, sectionId);
                stmt.executeUpdate();
            }

            final String deleteSectionSQL = "DELETE FROM sections WHERE section_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSectionSQL)) {
                stmt.setInt(1, sectionId);
                int rows = stmt.executeUpdate();
                conn.commit();
                return rows == 1;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error deleting section: " + e.getMessage());
                return false;
            }

        } catch (SQLException e) {
            System.err.println("DB transaction error: " + e.getMessage());
            return false;
        }
    }
}