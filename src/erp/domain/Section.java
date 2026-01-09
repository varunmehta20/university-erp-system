package erp.domain;

public class Section {
    private int id;
    private int courseId;
    private int instructorId;
    private String dayTime;
    private String room;
    private int capacity;
    private String semester;
    private int year;
    private String status;

    public Section(int id, int courseId, int instructorId, String dayTime, String room, int capacity, String semester, int year,String status) {
        this.id = id;
        this.courseId = courseId;
        this.instructorId = instructorId;
        this.dayTime = dayTime;
        this.room = room;
        this.capacity = capacity;
        this.semester = semester;
        this.year = year;
        this.status=status;
    }

    public Section() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", instructorId=" + instructorId +
                ", dayTime='" + dayTime + '\'' +
                ", room='" + room + '\'' +
                ", capacity=" + capacity +
                ", semester='" + semester + '\'' +
                ", year=" + year +'\''+
                ", status"+status +
                '}';
    }
}
