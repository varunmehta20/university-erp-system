package erp.domain;

public class Instructor {
    private int userId;
    private String department;
    private String designation;

    public Instructor(int userId, String department, String designation) {
        this.userId = userId;
        this.department = department;
        this.designation = designation;
    }

    public int getUserId() { return userId; }
    public String getDepartment() { return department; }
    public String getDesignation() { return designation; }

    @Override
    public String toString() {
        return designation + " (" + department + ")";
    }
}
