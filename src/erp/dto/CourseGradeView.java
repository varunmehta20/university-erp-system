package erp.dto;

import java.util.List;

public class CourseGradeView {
    private String courseCode;
    private String courseName;
    private String finalGrade;
    private List<AssessmentScore> componentScores;

    public CourseGradeView(String courseCode, String courseName, String finalGrade, List<AssessmentScore> componentScores) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.finalGrade = finalGrade;
        this.componentScores = componentScores;
    }

    public String getCourseCode() { return courseCode; }
    public String getCourseName() { return courseName; }
    public String getFinalGrade() { return finalGrade; }
    public List<AssessmentScore> getComponentScores() { return componentScores; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nCourse: ").append(courseCode).append(" - ").append(courseName).append("\n");
        sb.append("----------------------------------------\n");

        if (componentScores == null || componentScores.isEmpty()) {
            sb.append("No grades yet. (IP In Progress)\n");
        } else {
            for (AssessmentScore score : componentScores) {
                sb.append(score).append("\n");
            }
        }

        sb.append("Final Grade: ").append(finalGrade).append("\n");
        return sb.toString();
    }
}
