package edu.univ.erp.access;

import edu.univ.erp.domain.Role;
import edu.univ.erp.domain.UserSession;
import edu.univ.erp.service.MaintenanceService;

public class AccessManager {

    private final MaintenanceService maintenanceService = new MaintenanceService();

    // Student can modify only if:
    // - They are a student
    // - Maintenance mode is OFF
    public boolean canStudentModify(UserSession session) {
        try {
            return session.getRole() == Role.STUDENT && !maintenanceService.isMaintenanceOn();
        } catch (Exception e) {
            // fail-safe: do NOT allow modifications if maintenance check fails
            return false;
        }
    }

    // Anyone can check maintenance safely
    public boolean isMaintenanceOn() {
        try {
            return maintenanceService.isMaintenanceOn();
        } catch (Exception e) {
            // fail-safe: treat as OFF (or return true if you prefer stricter lock)
            return false;
        }
    }
}
