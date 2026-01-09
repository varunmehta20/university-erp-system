package erp.service;
import java.util.HashMap;
import erp.data.*;
import erp.domain.*;
import erp.dto.*;
import authen.SetSession;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class StudentService {

    private final SectionDAO sectionDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final GradeDAO gradeDAO;
    private final AccessHelper accessHelper;
    private final CourseDAO courseDAO;
    private final AssessmentDAO assessmentDAO;
    private AuthService authService ;


    public StudentService() {
        this.sectionDAO = new SectionDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.gradeDAO = new GradeDAO();
        this.courseDAO= new CourseDAO();
        this.accessHelper = new AccessHelper();
        this.assessmentDAO=new AssessmentDAO();
        this.authService= new AuthService();
    }

    public List<Course> browseCatalog() {
        List<Course> allCourses = courseDAO.listAll();
        List<Course> availableCourses = new ArrayList<>();

        for (Course course : allCourses) {
            List<Section> sections = sectionDAO.listByCourse(course.getId());
            if (sections != null && !sections.isEmpty()) {
                availableCourses.add(course);
            }
        }
        return availableCourses;
    }

    public List<Section> listAvailableSections(String courseCode) {
        Course course = courseDAO.getByCode(courseCode);
        if (course == null) {
            return new ArrayList<>();
        }
        return sectionDAO.listByCourse(course.getId());
    }

    public String registerForSection(int sectionId) {
        int studentId = SetSession.getUserId();

        if (!accessHelper.isStudentRole()) {
            return "ERROR: Access Denied. Only students can register.";
        }
        if (accessHelper.isMaintenanceMode()) {
            return "WARNING: System is under maintenance. Registration is closed.";
        }

        Section section = sectionDAO.getById(sectionId);
        if (section == null) {
            return "ERROR: Registration failed. Section ID " + sectionId + " not found.";
        }

        if (!"OPEN".equalsIgnoreCase(section.getStatus())) {
            return "WARNING: Registration failed. The section is " + section.getStatus() + ".";
        }

        int enrolledCount = sectionDAO.countEnrolled(sectionId);
        int capacity = section.getCapacity();
        if (enrolledCount >= capacity) {
            return "WARNING: Registration failed. The class section is full (" + capacity + ").";
        }

        Enrollment existingEnrollment = enrollmentDAO.getByStudentAndSection(studentId, sectionId);
        if (existingEnrollment != null) {
            String status = existingEnrollment.getStatus();

            if ("ENROLLED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                return "WARNING: You are already registered or have completed this section.";
            }

            if ("DROPPED".equalsIgnoreCase(status) || "WITHDRAWN".equalsIgnoreCase(status)) {
                boolean updated = enrollmentDAO.updateStatus(studentId, sectionId, "ENROLLED");
                if (updated) {
                    int seatsRemaining = capacity - (enrolledCount + 1);
                    return "SUCCESS: You have been re-enrolled in section " + sectionId +
                            ". Available seats remaining: " + seatsRemaining;
                } else {
                    return "ERROR: Failed to re-enroll due to a system error.";
                }
            }
        }

        int courseId = section.getCourseId();
        boolean alreadyEnrolledInCourse = enrollmentDAO.isStudentEnrolledInCourse(studentId, courseId);

        if (alreadyEnrolledInCourse) {
            return "WARNING: You are already enrolled in another section of this course.";
        }

        Enrollment newEnrollment = new Enrollment(0, studentId, sectionId, "ENROLLED");
        if (enrollmentDAO.create(newEnrollment)) {
            int seatsRemaining = capacity - (enrolledCount + 1);
            return "SUCCESS: Registered for section " + sectionId +
                    ". Available seats remaining: " + seatsRemaining;
        } else {
            return "ERROR: Registration failed due to a system error. Please try again.";
        }
    }


    public String dropSection(int sectionId) {
        int studentId = SetSession.getUserId();

        if (!accessHelper.isStudentRole()) {
            return "ERROR: Access Denied. Only students can drop sections.";
        }
        if (accessHelper.isMaintenanceMode()) {
            return "WARNING: System is under maintenance. Dropping is closed.";
        }

        if (accessHelper.isDropPeriodClosed()) {
            return "WARNING: The official drop deadline has passed. Dropping is not allowed at this time.";
        }

        Enrollment existingEnrollment = enrollmentDAO.getByStudentAndSection(studentId, sectionId);
        if (existingEnrollment == null) {
            return "WARNING: You are not enrolled in this section.";
        }

        if (!"ENROLLED".equalsIgnoreCase(existingEnrollment.getStatus())) {
            return "WARNING: Cannot drop section. Your current status is " + existingEnrollment.getStatus() + ".";
        }

        try {
            int enrollmentId = existingEnrollment.getId();
            gradeDAO.deleteGradesByEnrollment(enrollmentId);

            enrollmentDAO.markAsDropped(studentId, sectionId);

            return "SUCCESS: Section " + sectionId + " dropped successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR: Failed to drop section due to a database issue.";
        }
    }

    public List<TimetableEntry> getTimetable() {
        List<TimetableEntry> timetable = new ArrayList<>();
        int studentId = SetSession.getUserId();

        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);

        for (Enrollment enrollment : enrollments) {

            if ("ENROLLED".equalsIgnoreCase(enrollment.getStatus())) {
                int sectionId = enrollment.getSectionId();

                Section section = sectionDAO.getById(sectionId);

                if (section == null) {
                    System.err.println("Timetable Error: Section ID " + sectionId + " not found.");
                    continue;
                }

                Course course = courseDAO.getById(section.getCourseId());


                String courseCode;
                String courseName;

                if (course != null) {
                    courseCode = course.getCode();
                    courseName = course.getTitle();
                } else {
                    courseCode = "N/A";
                    courseName = "Unknown Course";
                }

                TimetableEntry entry = new TimetableEntry(
                        sectionId,
                        courseCode,
                        courseName,
                        section.getDayTime()
                );

                timetable.add(entry);
            }
        }

        return timetable;
    }

public List<CourseGradeView> getGradesView() {
    List<CourseGradeView> gradesView = new ArrayList<>();
    int studentId = SetSession.getUserId();

    List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);

    for (Enrollment enrollment : enrollments) {

        if (!"DROPPED".equalsIgnoreCase(enrollment.getStatus()) &&
                !"WITHDRAWN".equalsIgnoreCase(enrollment.getStatus())) {

            int sectionId = enrollment.getSectionId();
            int enrollmentId = enrollment.getId();

            Section section = sectionDAO.getById(sectionId);
            if (section == null) continue;

            Course course = courseDAO.getById(section.getCourseId());
            String courseCode = (course != null) ? course.getCode() : "N/A";
            String courseName = (course != null) ? course.getTitle() : "Unknown Course";

            List<Grade> studentScores = gradeDAO.listByEnrollment(enrollmentId);

            List<AssessmentComponent> components = assessmentDAO.getComponents(sectionId);

            List<AssessmentScore> componentScores = new ArrayList<>();
            String finalLetterGrade = "IP (In Progress)";

            for (Grade grade : studentScores) {
                String compName = grade.getComponent();

                double weight = 0.0;
                for (AssessmentComponent comp : components) {
                    if (comp.getName().equalsIgnoreCase(compName)) {
                        weight = comp.getWeight();
                        break;
                    }
                }

                componentScores.add(new AssessmentScore(
                        compName,
                        grade.getScore(),
                        weight
                ));

                if (grade.getFinalGrade() != null) {
                    finalLetterGrade = String.valueOf(grade.getFinalGrade());
                }
            }

            CourseGradeView view = new CourseGradeView(
                    courseCode,
                    courseName,
                    finalLetterGrade,
                    componentScores
            );

            gradesView.add(view);
        }
    }

    return gradesView;
}


    public List<TranscriptEntry> getTranscriptData() {
        List<TranscriptEntry> transcript = new ArrayList<>();
        int studentId = SetSession.getUserId();

        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsByStudent(studentId);

        for (Enrollment enrollment : enrollments) {

            int enrollmentId = enrollment.getId();
            List<Grade> studentScores = gradeDAO.listByEnrollment(enrollmentId);

            Double numericFinalGrade = null;
            for (Grade grade : studentScores) {
                if (grade.getFinalGrade() != null) {
                    numericFinalGrade = grade.getFinalGrade();
                    break;
                }
            }
            if (numericFinalGrade != null) {

                int sectionId = enrollment.getSectionId();
                Section section = sectionDAO.getById(sectionId);
                if (section == null) continue;

                Course course = courseDAO.getById(section.getCourseId());
                if (course == null) continue;
                String finalLetterGrade = String.valueOf(numericFinalGrade);

                TranscriptEntry entry = new TranscriptEntry(
                        course.getCode(),
                        course.getTitle(),
                        course.getCredits(),
                        finalLetterGrade
                );

                transcript.add(entry);
            }
        }

        return transcript;
    }

    public String exportTranscriptCSV(String filePath) {
        List<TranscriptEntry> transcript = getTranscriptData();

        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("Course Code,Course Title,Credits,Grade\n");
            for (TranscriptEntry entry : transcript) {
                writer.write(String.format("%s,%s,%.2f,%s\n",
                        entry.getCourseCode(),
                        entry.getCourseName(),
                        entry.getCredits(),
                        entry.getFinalGrade()
                ));
            }

            return "SUCCESS: Transcript CSV generated at " + filePath;
        } catch (IOException e) {
            return "ERROR: Failed to export transcript CSV: " + e.getMessage();
        }
    }
    public String getInstructorName(int instructorId) {
        return authService.getUsernameByUserId(instructorId);
    }
    public List<Enrollment> getMyEnrollments(int studentId) {
        return enrollmentDAO.getEnrollmentsByStudent(studentId);
    }

    public Course getCourseById(int courseId) {
        return courseDAO.getById(courseId);
    }


    public Section getSectionById(int sectionId) {
        return sectionDAO.getById(sectionId);
    }
    public String getSectionName(int sectionId) {
        SectionLabelDAO labelDAO = new SectionLabelDAO();
        SectionLabel label = labelDAO.getBySectionId(sectionId);

        if (label == null) return "Unnamed Section";

        return label.getLabel();
    }


    public List<Integer> getAvailableSemesters() {
        return sectionDAO.getAvailableSemesters();
    }

    public List<Section> getSectionsBySemester(int semester) {
        return sectionDAO.getSectionsBySemester(semester);
    }
    public List<Course> getAllCourses() {
        return courseDAO.listAll();
    }
    public String getInstructorForCourse(int courseId) {


        List<Section> sections = sectionDAO.listByCourse(courseId);

        if (sections == null || sections.isEmpty()) {
            return null;
        }

        Section section = sections.get(0);

        Integer instructorId = section.getInstructorId();
        if (instructorId == null) return null;

        return authService.getUsernameByUserId(instructorId);
    }










}