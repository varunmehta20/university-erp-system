package erp.dto;

public class TimetableEntry {

    private int sectionId;
    private String courseCode;
    private String courseName;
    private String scheduleDetails;

    public TimetableEntry(int sectionId, String courseCode, String courseName, String scheduleDetails) {
        this.sectionId = sectionId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.scheduleDetails = scheduleDetails;
    }


    public int getSectionId() { return sectionId; }
    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getScheduleDetails() { return scheduleDetails; }

    @Override
    public String toString() {
        return "TimetableEntry{" +
                "sectionId=" + sectionId +
                ", courseCode='" + courseCode + '\'' +
                ", courseName='" + courseName + '\'' +
                ", scheduleDetails='" + scheduleDetails + '\'' +
                '}';
    }
}
