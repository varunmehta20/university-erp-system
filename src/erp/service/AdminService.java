

package erp.service;

import erp.data.*;
import erp.domain.*;
import authen.SetSession;
import java.util.List;
import java.io.IOException;
import erp.dto.*;
import java.util.*;

public class AdminService {

    private final CourseDAO courseDAO;
    private final SectionDAO sectionDAO;
    private final StudentDAO studentDAO;
    private final InstructorDAO instructorDAO;
    private final SettingsDAO settingsDAO;
    private final AccessHelper accessHelper;
    private final AuthService authService;
    private SectionLabelDAO labelDAO;
    private final NotificationDAO notificationDAO; // <-- added field

    public AdminService() {
        this.courseDAO = new CourseDAO();
        this.sectionDAO = new SectionDAO();
        this.studentDAO = new StudentDAO();
        this.instructorDAO = new InstructorDAO();
        this.settingsDAO = new SettingsDAO();
        this.accessHelper = new AccessHelper();
        this.authService= new AuthService();
        this.labelDAO= new SectionLabelDAO();
        this.notificationDAO = new NotificationDAO();
    }
    private static final String DB_NAME = "univ_erp";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Varun#13";
    private static final String BACKUP_PATH = "C:/erp_backups/erp_backup.sql";
    private static final String MYSQL_BIN   = "C:/Users/Lenovo/Downloads/mysql-9.5.0-winx64/mysql-9.5.0-winx64/bin";
    private static final String BACKUP_DIR  = "C:/erp_backups";
    private static final String BACKUP_FILE = BACKUP_DIR + "/univ_erp_backup.sql";


    private String checkAdminAccess() {
        if (!accessHelper.isAdminRole()) {
            return "ERROR: Access Denied. Only Administrators can perform this action.";
        }
        return "OK";
    }

    public int createAuthUser(String username, String password, String role) {
        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return -1;

        return authService.addUser(username, password, role);
    }

    public String createStudentProfile(int userId, String rollNo, String program, int year) {
        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return accessStatus;

        Student student = new Student(userId, rollNo, program, year);
        studentDAO.create(student);
        return " Student profile created for User " + userId;
    }

    public String createInstructorProfile(int userId, String department, String designation) {
        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return accessStatus;

        Instructor instructor = new Instructor(userId, department, designation);
        instructorDAO.create(instructor);
        return " Instructor profile created for User " + userId;
    }

    public String createCourse(String code, String title, int credits) {
        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return accessStatus;

        Course course = new Course(0, code, title, credits);

        boolean created = courseDAO.create(course);

        if (!created) {
            return "FAILED: Course with code '" + code + "' already exists.";
        }

        try {
            List<Student> allStudents = studentDAO.listAll();
            for (Student s : allStudents) {
                notificationDAO.sendNotification(
                        s.getUserId(),
                        "New course available: " + code + " - " + title + "."
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "SUCCESS: Course " + code + " created with ID " + course.getId();
    }

    public String editCourse(int courseId, String newCode, String newTitle, int newCredits) {
        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return accessStatus;

        Course course = courseDAO.getById(courseId);
        if (course == null) {
            return "ERROR: Course ID " + courseId + " not found.";
        }

        course.setCode(newCode);
        course.setTitle(newTitle);
        course.setCredits(newCredits);

        if (courseDAO.update(course)) {
            try {
                List<Student> allStudents = studentDAO.listAll();
                for (Student s : allStudents) {
                    notificationDAO.sendNotification(
                            s.getUserId(),
                            "Course updated: " + newCode + " - " + newTitle + "."
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "SUCCESS: Course ID " + courseId + " updated to " + newCode + ".";
        } else {
            return "ERROR: Failed to update course details in database.";
        }
    }

    public String createSection(int courseId, int instructorId, String dayTime,
                                String room, int capacity, String semester, int year,
                                String status, String sectionLabel) {

        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return accessStatus;

        if (courseDAO.getById(courseId) == null) {
            return "ERROR: Course ID " + courseId + " does not exist.";
        }

        if (labelDAO.existsForCourse(courseId, sectionLabel)) {
            return "ERROR: Section name '" + sectionLabel + "' already exists for this course.";
        }

        Section section = new Section(
                0, courseId, instructorId, dayTime, room,
                capacity, semester, year, status
        );

        sectionDAO.create(section);

        if (section.getId() <= 0) {
            return "ERROR: Failed to create section.";
        }

        boolean labelOK = labelDAO.insert(section.getId(), courseId, sectionLabel);

        if (!labelOK) {
            return "WARNING: Section created (ID " + section.getId() +
                    "), but label could not be saved.";
        }

        try {
            if (instructorId > 0) {
                notificationDAO.sendNotification(
                        instructorId,
                        "You have been assigned to Section '" + sectionLabel + "' (Section ID " + section.getId() + ") for Course ID " + courseId + "."
                );
            }
            List<Student> allStudents = studentDAO.listAll();
            for (Student s : allStudents) {
                notificationDAO.sendNotification(
                        s.getUserId(),
                        "New section available: '" + sectionLabel + "' (Course ID " + courseId + "). You may enroll now."
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "SUCCESS: Section '" + sectionLabel +
                "' created with ID " + section.getId();
    }

    public String editSection(int sectionId,
                              int newInstructorId,
                              String newDayTime,
                              String newRoom,
                              int newCapacity,
                              String newSemester,
                              int newYear,
                              String newStatus) {

        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return accessStatus;

        Section section = sectionDAO.getById(sectionId);
        if (section == null) {
            return "ERROR: Section ID " + sectionId + " not found.";
        }

        section.setInstructorId(newInstructorId);
        section.setDayTime(newDayTime);
        section.setRoom(newRoom);
        section.setCapacity(newCapacity);
        section.setSemester(newSemester);
        section.setYear(newYear);
        section.setStatus(newStatus);

        boolean ok = sectionDAO.update(section);
        if (ok) {
            try {
                if (newInstructorId > 0) {
                    notificationDAO.sendNotification(
                            newInstructorId,
                            "Section " + sectionId + " assigned to you has been updated by admin."
                    );
                }
                List<Integer> studentIds = notificationDAO.getStudentIdsInSection(sectionId);
                for (int sid : studentIds) {
                    notificationDAO.sendNotification(
                            sid,
                            "Section " + sectionId + " you are enrolled in has been updated by admin."
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "SUCCESS: Section ID " + sectionId + " updated.";
        } else {
            return "ERROR: Failed to update section details.";
        }
    }

    public String assignInstructorToSection(int sectionId, int instructorId) {
        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return accessStatus;

        Section section = sectionDAO.getById(sectionId);
        if (section == null) {
            return "ERROR: Section ID " + sectionId + " not found.";
        }

        section.setInstructorId(instructorId);

        if (sectionDAO.update(section)) {
            try {
                notificationDAO.sendNotification(
                        instructorId,
                        "You have been assigned to Section " + sectionId + "."
                );
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "SUCCESS: Instructor " + instructorId + " assigned to Section " + sectionId + ".";
        } else {
            return "ERROR: Failed to update database for section assignment.";
        }
    }

    public String toggleMaintenance(boolean enable) {
        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return accessStatus;

        settingsDAO.toggleMaintenance(enable);

        if (enable) {
            try {
                List<Student> allStudents = studentDAO.listAll();
                for (Student s : allStudents) {
                    notificationDAO.sendNotification(
                            s.getUserId(),
                            "System maintenance has been ENABLED. Login and write operations are temporarily disabled."
                    );
                }
                List<Integer> instructorIds = instructorDAO.getAllInstructorIds();
                for (int iid : instructorIds) {
                    notificationDAO.sendNotification(
                            iid,
                            "System maintenance has been ENABLED. Login and write operations are temporarily disabled."
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "SUCCESS: Maintenance mode has been ENABLED. Users cannot log in now.";
        } else {

            try {
                List<Student> allStudents = studentDAO.listAll();
                for (Student s : allStudents) {
                    notificationDAO.sendNotification(
                            s.getUserId(),
                            "System maintenance has been DISABLED. System is available again."
                    );
                }
                List<Integer> instructorIds = instructorDAO.getAllInstructorIds();
                for (int iid : instructorIds) {
                    notificationDAO.sendNotification(
                            iid,
                            "System maintenance has been DISABLED. System is available again."
                    );
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return "SUCCESS: Maintenance mode has been DISABLED. Users can log in again.";
        }
    }

    public String getMaintenanceStatus() {
        boolean isOn = settingsDAO.isMaintenanceOn();
        return isOn ? "Maintenance Mode is currently ON" : "Maintenance Mode is currently OFF";
    }

    public String setDropDeadline(String deadlineString) {
        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return accessStatus;

        settingsDAO.setDropDeadline(deadlineString);

        try {
            List<Student> allStudents = studentDAO.listAll();
            for (Student s : allStudents) {
                notificationDAO.sendNotification(
                        s.getUserId(),
                        "Drop/Add deadline has been updated: " + deadlineString
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "SUCCESS: Drop/Add deadline set to " + deadlineString;
    }

    public String getDropDeadline() {
        String value = settingsDAO.getValue("dropDeadline");
        if (value == null) return "No drop deadline set yet.";
        return "Current drop deadline: " + value;
    }

public String backupDatabase() {
    String accessStatus = checkAdminAccess();
    if (!"OK".equals(accessStatus)) return accessStatus;

    new java.io.File(BACKUP_DIR).mkdirs();

    String dumpPath = MYSQL_BIN + "/mysqldump.exe";

    String command = String.format(
            "\"%s\" -u%s -p%s %s -r \"%s\"",
            dumpPath, DB_USER, DB_PASSWORD, DB_NAME, BACKUP_FILE
    );

    try {
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            return "SUCCESS: ERP database backup completed → " + BACKUP_FILE;
        } else {
            return "ERROR: Backup failed. Exit code: " + exitCode;
        }
    } catch (IOException | InterruptedException e) {
        return "ERROR: Backup process failed: " + e.getMessage();
    }
}
    public String restoreDatabase() {
        String accessStatus = checkAdminAccess();
        if (!"OK".equals(accessStatus)) return accessStatus;

        String mysqlPath = MYSQL_BIN + "/mysql.exe";

        String command = String.format(
                "\"%s\" -u%s -p%s %s < \"%s\"",
                mysqlPath, DB_USER, DB_PASSWORD, DB_NAME, BACKUP_FILE
        );

        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                return "SUCCESS: ERP database restored from → " + BACKUP_FILE;
            } else {
                return "ERROR: Restore failed. Exit code: " + exitCode;
            }
        } catch (IOException | InterruptedException e) {
            return "ERROR: Restore process failed: " + e.getMessage();
        }
    }


    public List<Course> getAllCourses() {
        return courseDAO.listAll();
    }

    public Course getCourseByCode(String code) {
        return courseDAO.getByCode(code);
    }
    public int getCourseIdFromCode(String code) {
        Course c = courseDAO.findCourseByCode(code);
        if (c == null) return -1;
        return c.getId();
    }
    public List<InstructorInfo> getInstructorDropdownList() {
        InstructorDAO instructorDAO = new InstructorDAO();
        AuthService authService = new AuthService();

        List<InstructorInfo> list = new ArrayList<>();

        for (int instructorId : instructorDAO.getAllInstructorIds()) {

            String username = authService.getUsernameByUserId(instructorId);

            if (username != null) {
                list.add(new InstructorInfo(instructorId, username));
            }
        }

        return list;
    }
    public String editSectionFull(int sectionId,
                                  int instructorId,
                                  String dayTime,
                                  String room,
                                  int capacity,
                                  String semester,
                                  int year,
                                  String status) {

        SectionDAO sectionDAO = new SectionDAO();

        Section existing = sectionDAO.getById(sectionId);
        if (existing == null) {
            return "Error: Section not found.";
        }

        existing.setInstructorId(instructorId);
        existing.setDayTime(dayTime);
        existing.setRoom(room);
        existing.setCapacity(capacity);
        existing.setSemester(semester);
        existing.setYear(year);
        existing.setStatus(status);

        boolean updated = sectionDAO.updateFull(existing);

        return updated ? "Section updated successfully!" : "Error updating section.";
    }

    public boolean removeSection(int sectionId) {
        return sectionDAO.deleteSectionCompletely(sectionId);
    }















}
