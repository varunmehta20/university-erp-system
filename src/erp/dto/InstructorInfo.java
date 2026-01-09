package erp.dto;

public class InstructorInfo {
    private int instructorId;
    private String username;

    public InstructorInfo(int instructorId, String username) {
        this.instructorId = instructorId;
        this.username = username;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }
}
