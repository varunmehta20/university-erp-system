package erp.service;
import erp.domain.*;
import erp.data.*;

import erp.data.SettingsDAO;
import authen.SetSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class AccessHelper {

    private static final String MAINTENANCE_KEY = "maintenance";
    private static final String DROP_DEADLINE_KEY = "dropDeadline";
    private static final DateTimeFormatter DEADLINE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SettingsDAO settingsDAO;
    private final SectionDAO sectionDAO;

    public AccessHelper() {
        this.settingsDAO = new SettingsDAO();
        this.sectionDAO = new SectionDAO();
    }

    public boolean isMaintenanceMode() {
        String value = settingsDAO.getValue(MAINTENANCE_KEY);
        return "true".equalsIgnoreCase(value);
    }

    public boolean isDropPeriodClosed() {
        String deadlineString = settingsDAO.getValue(DROP_DEADLINE_KEY);

        if (deadlineString == null || deadlineString.isEmpty()) {
            return false;
        }

        try {
            LocalDateTime deadline = LocalDateTime.parse(deadlineString, DEADLINE_FORMATTER);
            return LocalDateTime.now().isAfter(deadline);

        } catch (DateTimeParseException e) {
            System.err.println("ERROR: Drop deadline format is invalid. Cannot parse date: " + deadlineString);
            return true;
        } catch (Exception e) {
            System.err.println("ERROR checking deadline: " + e.getMessage());
            return true;
        }
    }


    public boolean isStudentRole() {
        return "STUDENT".equalsIgnoreCase(SetSession.getRole());
    }
    public boolean isInstructorRole() {
        return "INSTRUCTOR".equalsIgnoreCase(SetSession.getRole());
    }
    public boolean isAdminRole() {
        return "ADMIN".equalsIgnoreCase(SetSession.getRole());
    }

    public SettingsDAO getSettingsDAO() {
        return this.settingsDAO;
    }
    public boolean isInstructorForSection(int instructorId, int sectionId) {
        if (!isInstructorRole() && !isAdminRole()) {
            return false;
        }

        if (isAdminRole()) {
            return true;
        }

        Section section = sectionDAO.getById(sectionId);

        if (section == null) {
            return false;
        }

        return section.getInstructorId() == instructorId;
    }
}