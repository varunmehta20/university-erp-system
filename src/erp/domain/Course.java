package erp.domain;

public class Course {
    private int id;
    private String code;
    private String title;
    private int credits;


    public Course(int id, String code, String title, int credits) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.credits = credits;
    }

    public Course(String code, String title, int credits) {
        this(0, code, title, credits);
    }

    public int getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }

    public void setId(int id) {
        this.id = id;
    }
    public void setCode(String code){
        this.code=code;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public void setCredits(int credits){
        this.credits=credits;
    }

    @Override
    public String toString() {
        return id + ": " + code + " - " + title + " (" + credits + " credits)";
    }


}