package erp.dto;

public class AssessmentScore {
    private String componentName;
    private double score;
    private double weight;

    public AssessmentScore(String componentName, double score, double weight) {
        this.componentName = componentName;
        this.score = score;
        this.weight = weight;
    }

    public String getComponentName() { return componentName; }
    public double getScore() { return score; }
    public double getWeight() { return weight; }

    public void setComponentName(String componentName) { this.componentName = componentName; }
    public void setScore(double score) { this.score = score; }
    public void setWeight(double weight) { this.weight = weight; }

    @Override
    public String toString() {
        return String.format("Component: %-10s | Score: %-5.2f | Weight: %.0f%%", componentName, score, weight);
    }
}
