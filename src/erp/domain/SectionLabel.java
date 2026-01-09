

package erp.domain;

import erp.data.CourseDAO;

public class SectionLabel {

    private int labelId;
    private int sectionId;
    private int courseId;
    private String label;

    private static final CourseDAO courseDAO = new CourseDAO();

    public SectionLabel(int labelId, int sectionId, int courseId, String label) {
        this.labelId = labelId;
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.label = label;
    }

    public SectionLabel(int sectionId, int courseId, String label) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.label = label;
    }

    public int getLabelId() { return labelId; }
    public int getSectionId() { return sectionId; }
    public int getCourseId() { return courseId; }
    public String getLabel() { return label; }

    public void setLabelId(int labelId) { this.labelId = labelId; }
    public void setSectionId(int sectionId) { this.sectionId = sectionId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public void setLabel(String label) { this.label = label; }

    @Override
    public String toString() {
        String code;
        try {
            code = courseDAO.getById(courseId).getCode();
        } catch (Exception e) {
            code = "COURSE-" + courseId;
        }

        return code + " | " + label;
    }
}
