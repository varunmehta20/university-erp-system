package erp.domain;

public class Grade {
    private int id;
    private int enrollmentId;
    private String component;
    private double score;
    private Double finalGrade;

    public Grade(int id, int enrollmentId, String component, double score, Double finalGrade) {
        this.id = id;
        this.enrollmentId = enrollmentId;
        this.component = component;
        this.score = score;
        this.finalGrade = finalGrade;
    }

    public Grade() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public Double getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(Double finalGrade) {
        this.finalGrade = finalGrade;
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", enrollmentId=" + enrollmentId +
                ", component='" + component + '\'' +
                ", score=" + score +
                ", finalGrade=" + finalGrade +
                '}';
    }
}
