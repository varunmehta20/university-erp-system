package erp.dto;

public class TranscriptEntry {
    private String courseCode;
    private String courseName;
    private double credits;
    private String finalGrade;

    public TranscriptEntry(String courseCode, String courseName, double credits, String finalGrade) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.finalGrade = finalGrade;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public double getCredits() { return credits; }
    public String getFinalGrade() { return finalGrade; }
}
