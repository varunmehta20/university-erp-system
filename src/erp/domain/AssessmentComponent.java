package erp.domain;

public class AssessmentComponent {
    private int id;
    private int sectionId;
    private String name;
    private double weight;


    public AssessmentComponent() {
    }

    public AssessmentComponent(int id, int sectionId, String name, double weight) {
        this.id = id;
        this.sectionId = sectionId;
        this.name = name;
        this.weight = weight;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "AssessmentComponent{" +
                "id=" + id +
                ", sectionId=" + sectionId +
                ", name='" + name + '\'' +
                ", weight=" + weight +
                '}';
    }
}
