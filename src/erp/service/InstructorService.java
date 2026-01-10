


package erp.service;

import erp.domain.*;
import erp.data.AssessmentDAO;
import erp.data.SectionDAO;
import erp.data.EnrollmentDAO;
import erp.data.GradeDAO;
import erp.data.NotificationDAO;
import erp.domain.Section;
import erp.domain.Enrollment;
import erp.domain.Grade;
import authen.SetSession;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class InstructorService {

    private final SectionDAO sectionDAO;
    private final EnrollmentDAO enrollmentDAO;
    private final GradeDAO gradeDAO;
    private final AccessHelper accessHelper;
    private final AssessmentDAO assessmentDAO;
    private final NotificationDAO notificationDAO;

    public InstructorService() {
        this.sectionDAO = new SectionDAO();
        this.enrollmentDAO = new EnrollmentDAO();
        this.gradeDAO = new GradeDAO();
        this.accessHelper = new AccessHelper();
        this.assessmentDAO= new AssessmentDAO();
        this.notificationDAO = new NotificationDAO();
    }

    private void notifyStudents(int sectionId, String message) {
        List<Integer> studentIds = notificationDAO.getStudentIdsInSection(sectionId);
        for (int sid : studentIds) {
            notificationDAO.sendNotification(sid, message);
        }
    }

    public List<Section> getMySections(int instructorId) {
        return sectionDAO.getSectionsByInstructor(instructorId);
    }

    public String computeFinalGrades(int sectionId) {

        AssessmentDAO assessmentDAO = new AssessmentDAO();
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
        GradeDAO gradeDAO = new GradeDAO();

        List<AssessmentComponent> components = assessmentDAO.getComponents(sectionId);
        if (components.isEmpty()) {
            return "No assessment components defined for section " + sectionId;
        }

        double totalWeight = components.stream()
                .mapToDouble(AssessmentComponent::getWeight)
                .sum();
        if (Math.abs(totalWeight - 1.0) > 0.001) {
            return "Total component weight must equal 1.0";
        }

        List<Enrollment> enrollments = enrollmentDAO.getEnrollmentsBySection(sectionId);
        if (enrollments.isEmpty()) {
            return "No students enrolled in this section.";
        }

        int updated = 0;

        for (Enrollment en : enrollments) {

            double finalGrade = 0.0;

            List<Grade> grades = gradeDAO.listByEnrollment(en.getId());

            for (AssessmentComponent comp : components) {
                double score = grades.stream()
                        .filter(g -> g.getComponent().equalsIgnoreCase(comp.getName()))
                        .mapToDouble(Grade::getScore)
                        .findFirst()
                        .orElse(0.0);

                finalGrade += score * comp.getWeight();
            }

            gradeDAO.updateFinalGrade(en.getId(), finalGrade);
            updated++;
        }
        notifyStudents(sectionId, "Final grades for Section " + sectionId + " have been published.");

        return "Successfully computed final grades for " + updated + " students.";
    }

    public Map<String, Object> getClassStats(int sectionId, int instructorId) {
        Map<String, Object> stats = new HashMap<>();

        if (!accessHelper.isInstructorForSection(instructorId, sectionId)) {
            stats.put("ERROR", "Access Denied. You are not assigned to teach section " + sectionId + ".");
            return stats;
        }

        List<Grade> allSectionGrades = gradeDAO.getGradesBySection(sectionId);
        List<AssessmentComponent> components = assessmentDAO.getComponents(sectionId);

        if ((allSectionGrades == null || allSectionGrades.isEmpty()) &&
                (components == null || components.isEmpty())) {
            stats.put("WARNING", "No grades or components found for this section.");
            return stats;
        }

        List<Map<String, Object>> compStatsList = new ArrayList<>();

        if (components != null) {
            for (AssessmentComponent comp : components) {
                String compName = comp.getName();

                List<Double> scores = allSectionGrades.stream()
                        .filter(g -> g.getComponent() != null && g.getComponent().equalsIgnoreCase(compName))
                        .map(Grade::getScore)
                        .filter(d -> d != null)
                        .map(Double::valueOf)
                        .collect(Collectors.toList());

                Map<String, Object> cmap = new HashMap<>();
                cmap.put("component", compName);

                if (scores.isEmpty()) {
                    cmap.put("avg", "N/A");
                    cmap.put("max", "N/A");
                    cmap.put("min", "N/A");
                    cmap.put("count", 0);
                } else {
                    double avg = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
                    double max = scores.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
                    double min = scores.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
                    cmap.put("avg", String.format("%.2f", avg));
                    cmap.put("max", String.format("%.2f", max));
                    cmap.put("min", String.format("%.2f", min));
                    cmap.put("count", scores.size());
                }

                compStatsList.add(cmap);
            }
        }

        stats.put("component_stats", compStatsList);

        Map<Integer, Double> finalGradesByEnrollment = new HashMap<>();
        for (Grade g : allSectionGrades) {
            Double fg = g.getFinalGrade();
            if (fg != null) {
                finalGradesByEnrollment.putIfAbsent(g.getEnrollmentId(), fg);
            }
        }

        List<Double> finalGrades = new ArrayList<>(finalGradesByEnrollment.values());

        if (finalGrades.isEmpty()) {
            stats.put("final_avg", "N/A");
            stats.put("final_max", "N/A");
            stats.put("final_min", "N/A");
            stats.put("total_students", 0);
        } else {
            double finalAvg = finalGrades.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double finalMax = finalGrades.stream().mapToDouble(Double::doubleValue).max().orElse(0.0);
            double finalMin = finalGrades.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
            stats.put("final_avg", String.format("%.2f", finalAvg));
            stats.put("final_max", String.format("%.2f", finalMax));
            stats.put("final_min", String.format("%.2f", finalMin));
            stats.put("total_students", finalGrades.size());
        }

        stats.put("Grading_Policy", components);

        return stats;
    }

    public String exportGradesCSV(int sectionId, String filePath) {
        GradeDAO gradeDAO = new GradeDAO();
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("enrollmentId,studentId,component,score,finalGrade");
            writer.newLine();

            List<Grade> grades = gradeDAO.getGradesBySection(sectionId);
            if (grades == null || grades.isEmpty()) {
                return "INFO: No grades found for section " + sectionId + ". File not created.";
            }

            for (Grade g : grades) {
                int enrollmentId = g.getEnrollmentId();
                int studentId = -1;
                Enrollment en = enrollmentDAO.getEnrollmentById(enrollmentId);
                if (en != null) studentId = en.getStudentId();

                String line = enrollmentId + "," +
                        (studentId > 0 ? studentId : "") + "," +
                        (g.getComponent() != null ? g.getComponent() : "") + "," +
                        g.getScore() + "," +
                        (g.getFinalGrade() != null ? g.getFinalGrade() : "");

                writer.write(line);
                writer.newLine();
            }

            return "SUCCESS: Grades exported to " + filePath;
        } catch (IOException e) {
            return "ERROR: Failed to write CSV: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: Unexpected issue: " + e.getMessage();
        }
    }

    public String importGradesCSV(String filePath, int sectionId) {
        AssessmentDAO assessmentDAO = new AssessmentDAO();
        GradeDAO gradeDAO = new GradeDAO();
        EnrollmentDAO enrollmentDAO = new EnrollmentDAO();

        int imported = 0;
        int createdComponents = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String header = reader.readLine();
            if (header == null) return "ERROR: Empty file.";

            boolean headerHasStudentId = header.toLowerCase().contains("studentid");
            boolean headerHasEnrollmentId = header.toLowerCase().contains("enrollmentid");

            List<AssessmentComponent> existing = assessmentDAO.getComponents(sectionId);
            Set<String> compNames = new HashSet<>();
            if (existing != null) {
                for (AssessmentComponent ac : existing) {
                    if (ac.getName() != null) compNames.add(ac.getName().trim().toLowerCase());
                }
            }

            String line;
            int lineno = 1;
            while ((line = reader.readLine()) != null) {
                lineno++;
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", -1);
                try {
                    Integer enrollmentId = null;
                    Integer studentId = null;
                    String component;
                    String scoreStr;
                    String finalGradeStr = null;

                    if (headerHasStudentId) {
                        if (parts.length < 3) throw new IllegalArgumentException("Bad columns on line " + lineno);
                        studentId = parts[0].trim().isEmpty() ? null : Integer.parseInt(parts[0].trim());
                        component = parts[1].trim();
                        scoreStr = parts[2].trim();
                        if (parts.length >= 4) finalGradeStr = parts[3].trim();
                    } else if (headerHasEnrollmentId) {
                        if (parts.length < 3) throw new IllegalArgumentException("Bad columns on line " + lineno);
                        enrollmentId = parts[0].trim().isEmpty() ? null : Integer.parseInt(parts[0].trim());
                        component = parts[1].trim();
                        scoreStr = parts[2].trim();
                        if (parts.length >= 4) finalGradeStr = parts[3].trim();
                    } else {
                        if (parts.length < 3) throw new IllegalArgumentException("Bad columns on line " + lineno);
                        String first = parts[0].trim();
                        if (!first.isEmpty()) {
                            try {
                                enrollmentId = Integer.parseInt(first);
                            } catch (NumberFormatException nfe) {
                                try {
                                    studentId = Integer.parseInt(first);
                                } catch (NumberFormatException nfe2) {
                                    throw new IllegalArgumentException("Cannot parse id in first column on line " + lineno);
                                }
                            }
                        }
                        component = parts[1].trim();
                        scoreStr = parts[2].trim();
                        if (parts.length >= 4) finalGradeStr = parts[3].trim();
                    }
                    if (enrollmentId == null && studentId != null) {
                        Enrollment e = enrollmentDAO.getByStudentAndSection(studentId, sectionId);
                        if (e == null) {
                            errors.add("Line " + lineno + ": no enrollment found for student " + studentId + " in section " + sectionId);
                            continue;
                        }
                        enrollmentId = e.getId();
                    }
                    if (enrollmentId == null) {
                        errors.add("Line " + lineno + ": could not determine enrollmentId.");
                        continue;
                    }
                    double score = 0.0;
                    if (scoreStr != null && !scoreStr.isEmpty()) {
                        score = Double.parseDouble(scoreStr);
                    }
                    Double finalGrade = null;
                    if (finalGradeStr != null && !finalGradeStr.isEmpty()) {
                        finalGrade = Double.parseDouble(finalGradeStr);
                    }
                    String compLower = component.trim().toLowerCase();
                    if (!compNames.contains(compLower)) {
                        try {
                            assessmentDAO.insertComponent(sectionId, component.trim(), 0.0);
                            compNames.add(compLower);
                            createdComponents++;
                        } catch (Exception ce) {
                            errors.add("Line " + lineno + ": component '" + component + "' missing and creation failed: " + ce.getMessage());
                            continue;
                        }
                    }

                    Grade g = new Grade();
                    g.setEnrollmentId(enrollmentId);
                    g.setComponent(component.trim());
                    g.setScore(score);
                    g.setFinalGrade(finalGrade);

                    boolean ok = gradeDAO.insertOrUpdateScore(g);
                    if (ok) imported++;
                    else errors.add("Line " + lineno + ": failed to insert/update grade for enrollment " + enrollmentId);

                } catch (NumberFormatException nfe) {
                    errors.add("Line " + lineno + ": number format error -> " + nfe.getMessage());
                } catch (IllegalArgumentException iae) {
                    errors.add("Line " + lineno + ": " + iae.getMessage());
                } catch (Exception ex) {
                    errors.add("Line " + lineno + ": unexpected error -> " + ex.getMessage());
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("Import finished. Imported=").append(imported);
            sb.append(", NewComponents=").append(createdComponents);
            if (!errors.isEmpty()) {
                sb.append(", Errors=").append(errors.size()).append(". See details:\n");
                for (String err : errors) sb.append(err).append("\n");
            }
            return sb.toString();

        } catch (IOException e) {
            return "ERROR: Failed to read CSV file: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: Unexpected issue: " + e.getMessage();
        }
    }
    public List<Section> getMySectionsBySemester(int instructorId, int semester) {
        List<Section> all = sectionDAO.getSectionsBySemester(semester);

        List<Section> filtered = new ArrayList<>();
        for (Section s : all) {
            if (s.getInstructorId() == instructorId) {
                filtered.add(s);
            }
        }
        return filtered;
    }

    public List<Integer> getAvailableSemesters() {
        return sectionDAO.getAllSemesters();
    }

}
