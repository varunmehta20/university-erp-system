package erp.domain;

public class Enrollment {
    private int id;
    private int studentId;
    private int sectionId;
    private String status;

    public Enrollment(int id, int studentId, int sectionId, String status) {
        this.id = id;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.status = status;
    }

    public Enrollment(int studentId, int sectionId, String status) {
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.status = status;
    }

    public int getId() { return id; }
    public int getStudentId() { return studentId; }
    public int getSectionId() { return sectionId; }
    public String getStatus() { return status; }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Enrollment [id=" + id +
                ", studentId=" + studentId +
                ", sectionId=" + sectionId +
                ", status=" + status + "]";
    }
}
